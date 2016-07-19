package com.jget.core.download;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.jget.core.manifest.ManifestProvider;
import com.jget.core.report.ReportConstants;
import com.jget.core.report.ReportProvider;
import com.jget.core.report.ReportUtils;
import com.jget.core.spring.ApplicationContextProvider;
import com.jget.core.utils.url.UrlAnalyser;
import com.jget.core.utils.url.UrlAnalysisResult;
import com.jget.core.utils.url.UrlUtils;

@Component
@Scope("singleton")
public class DownloadManager {

	@Autowired
	ThreadPoolTaskExecutor taskExecutor;
	
    public static List<Future<?>> runningTasks;
    public static int processedLinks;

    public DownloadManager() {
        runningTasks = new ArrayList<Future<?>>();
        logger.info ("Marker 117: {}", ApplicationContextProvider.getApplicationContext ());
    }

    public void commenceDownload() {

        processedLinks = 0;

        logger.info(DownloadConfig.LINE_BREAK);
        logger.info("Beginning download");
        logger.info(DownloadConfig.LINE_BREAK);

        while (!ManifestProvider.getCurrentManifest().getFrontier().isEmpty()) {

            if (ManifestProvider.getCurrentManifest().getFileCount().get() >= DownloadConfig.MAX_TOTAL_DOWNLOADED_FILES) {
                logger.info("The maximum number of files have been downloaded: {}", DownloadConfig.MAX_TOTAL_DOWNLOADED_FILES);
                waitForTasksToComplete();
                break;
            }

            if (processedLinks > DownloadConfig.MAX_TOTAL_PROCESSED_LINKS) {
                logger.info("The maximum number of links have been processed: {}", DownloadConfig.MAX_TOTAL_PROCESSED_LINKS);
                waitForTasksToComplete();
                break;
            }

            processedLinks++;

            logger.info("Total threads running: {}", taskExecutor.getActiveCount());
            logger.info("Total size of frontier: {}", ManifestProvider.getCurrentManifest().getFrontier().size());
            logger.info("Total size of linkMap: {}", ManifestProvider.getCurrentManifest().getLinkMap().size());
            logger.info("Total links processed: {}", processedLinks);

            ReferencedURL referencedURL = ManifestProvider.getCurrentManifest().getFrontier().poll();

            if (UrlUtils.hasLinkBeenProcessed(referencedURL.getURL())) {
                logger.info("Link has previously been processed: {}", referencedURL.getURL());
                continue;
            }

            UrlAnalysisResult urlAnalyserResult = UrlAnalyser.analyse(referencedURL.getURL());

            if (!urlAnalyserResult.isValidLink()) {
                logger.info("Invalid link: {}\n", referencedURL.getURL());
                int reportType = ReportUtils.getReportTypeFromResponseCode(urlAnalyserResult.getResponseCode());
                ReportProvider.getReport().addEntry(reportType, referencedURL.getLocation(), referencedURL.getURL().toString());
                continue;
            }

            if (UrlUtils.exceedsUrlDepth(urlAnalyserResult.getURL())) {
                logger.info("The following URL is too deep: {}", referencedURL.getURL());
                ReportProvider.getReport().addEntry(ReportConstants.TOO_MANY_REDIRECTS, referencedURL.getLocation(),
                        referencedURL.getURL().toString());
                continue;
            }

            int redirectIndex = 0;
            boolean brokenRedirect = false;
            boolean exceedRedirectMax = false;
            boolean processedLink = false;
            URL originalUrl = referencedURL.getURL();
            while (urlAnalyserResult.isRedirect()) {

                redirectIndex++;
                if (redirectIndex > DownloadConfig.MAX_REDIRECT_DEPTH) {
                    logger.info("Exceeded redirect depth");
                    exceedRedirectMax = true;
                    break;
                }

                Optional<UrlAnalysisResult> newUrlAnalyserResult = handleRedirect(urlAnalyserResult, referencedURL.getURL());

                if (!newUrlAnalyserResult.isPresent()) {
                    brokenRedirect = true;
                    break;
                }

                urlAnalyserResult = newUrlAnalyserResult.get();
                if (UrlUtils.hasLinkBeenProcessed(urlAnalyserResult.getURL())) {
                    processedLink = true;
                    break;
                }
                referencedURL.setURL(urlAnalyserResult.getURL());
            }
            if (exceedRedirectMax) {
                logger.info("Too many redirects from original Url: {}", originalUrl.toString());
                continue;
            }
            if (brokenRedirect) {
                logger.info("Broken redirect from original Url: {}", originalUrl.toString());
                continue;
            }
            if (processedLink) {
                logger.info("Link has previously been processed: {}", urlAnalyserResult.getURL());
                continue;
            }

            logger.info("Processing url: {}", referencedURL.getURL().toString());
            logger.info("Mime type: {}", urlAnalyserResult.getContentType().getMimeType());

            ManifestProvider.getCurrentManifest().getFileCount().incrementAndGet();

            if (urlAnalyserResult.getContentType().getMimeType().equals(ContentType.TEXT_HTML.getMimeType())) {
                DownloadPageTask downloadPageTask = new DownloadPageTask(referencedURL);
                runningTasks.add(taskExecutor.submit(downloadPageTask));
            } else {
                DownloadMediaTask downloadMediaTask = new DownloadMediaTask(referencedURL);
                runningTasks.add(taskExecutor.submit(downloadMediaTask));
            }

            if (ManifestProvider.getCurrentManifest().getFrontier().isEmpty() && (taskExecutor.getActiveCount() > 0)) {
                logger.info("No urls to process, Waiting for tasks to finish");
                waitForTasksToComplete();
            }
            reviewRunningTasks();
        }
    }

    public void reviewRunningTasks() {

        List<Future<?>> finishedTasks = new ArrayList<Future<?>>();
        for (Future<?> future : runningTasks) {
            if (future.isDone()) {
                finishedTasks.add(future);
            }
        }
        runningTasks.removeAll(finishedTasks);

    }

    public void waitForTasksToComplete() {

        for (Future<?> future : runningTasks) {
            try {
                if (!future.isDone())
                    future.get();
            } catch (InterruptedException | NoSuchElementException | ExecutionException e) {
                logger.error("Failed to get info: {}", future.toString(), e);
            }
        }

    }

    public Optional<UrlAnalysisResult> handleRedirect(UrlAnalysisResult urlAnalyserResult, URL url) {
        logger.info("Handling redirect for link: {}", url.toString());
        try {
            url = new URL(urlAnalyserResult.getLocation());
            urlAnalyserResult = UrlAnalyser.analyse(url);

            if (!urlAnalyserResult.isValidLink()) {
                return Optional.empty();
            }

            logger.info("New location found: {}", url.toString());
            return Optional.of(urlAnalyserResult);

        } catch (MalformedURLException e) {
            return Optional.empty();
        }

    }

    private static final Logger logger = LoggerFactory.getLogger(DownloadManager.class);

}
