package com.jget.core.report;

import org.apache.http.HttpStatus;

public class ReportUtils {

    public static int getReportTypeFromResponseCode(int responseCode) {

        if (responseCode == HttpStatus.SC_NOT_FOUND) {
            return ReportConstants.FILE_NOT_FOUND;
        }

        return 0;

    }

}
