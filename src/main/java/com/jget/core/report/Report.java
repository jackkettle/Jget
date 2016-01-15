package com.jget.core.report;

import java.util.HashSet;

public class Report {

    HashSet<ReportEntry> reportEntries;

    public Report() {
        this.reportEntries = new HashSet<ReportEntry>();
    }

    public void addEntry(int reportType, String originatingURL, String linkedURL) {
        ReportEntry reportEntry = new ReportEntry();
        reportEntry.setLinkedURL(linkedURL);
        reportEntry.setOriginatingURL(originatingURL);
        reportEntries.add(reportEntry);
    }

    public HashSet<ReportEntry> getReportsByType(int reportType) {

        HashSet<ReportEntry> reportEntries = new HashSet<>();

        for (ReportEntry reportEntry : reportEntries) {

            if (reportEntry.getReportType() == reportType)
                reportEntries.add(reportEntry);

        }

        return reportEntries;

    }

}
