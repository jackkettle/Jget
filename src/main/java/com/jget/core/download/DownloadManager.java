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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.jget.core.ManifestProvider;
import com.jget.core.spring.ApplicationContextProvider;
import com.jget.core.utils.url.UrlAnalyser;
import com.jget.core.utils.url.UrlAnalysisResult;
import com.jget.core.utils.url.UrlUtils;

public class DownloadManager {

    public static ThreadPoolTaskExecutor taskExecutor;
    public static List<Future<?>> runningTasks;

    public DownloadManager() {

        runningTasks = new ArrayList<Future<?>>();
        taskExecutor = (ThreadPoolTaskExecutor) ApplicationContextProvider.getBean(ThreadPoolTaskExecutor.class);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
    }

    public void commenceDownload() {

        logger.info(DownloadConfig.LINE_BREAK);
        logger.info("Beginning download");
        logger.info(DownloadConfig.LINE_BREAK);
        while (!ManifestProvider.getManifest().getFrontier().isEmpty()) {

            logger.info("Total threads running: {}", taskExecutor.getActiveCount());
            logger.info("Total size of frontier: {}", ManifestProvider.getManifest().getFrontier().size());
            logger.info("Total size of linkMap: {}", ManifestProvider.getManifest().getLinkMap().size());

            ReferencedURL referencedURL = ManifestProvider.getManifest().getFrontier().poll();

            if (UrlUtils.hasLinkBeenProcessed(referencedURL.getURL())) {
                logger.info("Link has previously been processed: {}", referencedURL.getURL());
                continue;
            }

            UrlAnalysisResult urlAnalyserResult = UrlAnalyser.analyse(referencedURL.getURL());

            if (!urlAnalyserResult.isValidLink()) {
                logger.info("Invalid link: {}\n", referencedURL.getURL());
                continue;
            }

            int redirectIndex = 0;
            boolean brokenRedirect = false;
            boolean processedLink = false;
            URL originalUrl = referencedURL.getURL();
            while (urlAnalyserResult.isRedirect()) {

                redirectIndex++;
                if (redirectIndex > DownloadConfig.REDIRECT_DEPTH) {
                    logger.info("Exceeded redirect depth");
                    brokenRedirect = true;
                    break;
                }

                Optional<UrlAnalysisResult> newUrlAnalyserResult = handleRedirect(urlAnalyserResult, referencedURL.getURL());

                if (!newUrlAnalyserResult.isPresent()) {
                    brokenRedirect = true;
                    break;
                }

                urlAnalyserResult = newUrlAnalyserResult.get();
                if (UrlUtils.hasLinkBeenProcessed(urlAnalyserResult.getUrl())) {
                    processedLink = true;
                    break;
                }
                referencedURL.setURL(urlAnalyserResult.getUrl());
            }
            if (brokenRedirect) {
                logger.info("Broken redirect from original Url: {}", originalUrl.toString());
                continue;
            }
            if (processedLink) {
                logger.info("Link has previously been processed: {}", urlAnalyserResult.getUrl());
                continue;
            }

            logger.info("Processing url: {}", referencedURL.getURL().toString());
            logger.info("Mime type: {}", urlAnalyserResult.getContentType().getMimeType());

            if (urlAnalyserResult.getContentType().getMimeType().equals(ContentType.TEXT_HTML.getMimeType())) {
                DownloadPageTask downloadPageTask = new DownloadPageTask(referencedURL);
                runningTasks.add(taskExecutor.submit(downloadPageTask));
            } else {
                DownloadMediaTask downloadMediaTask = new DownloadMediaTask(referencedURL);
                runningTasks.add(taskExecutor.submit(downloadMediaTask));
            }

            if (ManifestProvider.getManifest().getFrontier().isEmpty() && (taskExecutor.getActiveCount() > 0)) {
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
