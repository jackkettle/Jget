package com.jget.core.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jget.core.download.DownloadConfig;
import com.jget.core.manifest.Manifest;
import com.jget.core.manifest.ManifestProvider;

public class ReportProvider {

    private static Report report;

    public ReportProvider() {
    	report = new Report();
    }
    
    public static void setReport(Report report) {
        ReportProvider.report = report;
    }

    public static Report getReport() {
        return report;
    }
    
    public static void printReportSummaryString() {

        Manifest manifest = ManifestProvider.getCurrentManifest();

        logger.info(DownloadConfig.LINE_BREAK);
        logger.info("Download Report");
        logger.info(DownloadConfig.LINE_BREAK);

        logger.info("Total Files download: {}", manifest.getLinkMap().size());
        logger.info("Total 404 URLs found: {}", report.getReportsByType(ReportConstants.FILE_NOT_FOUND).size());

    }

    private static final Logger logger = LoggerFactory.getLogger(ReportProvider.class);

}
