package com.jget.core.report;

import java.util.UUID;

public class ReportEntry {

    private UUID id;

    private int reportType;

    private Exception exception;

    private String message;

    private String originatingURL;

    private String linkedURL;

    private int responseStatus;

    private String exceptionType;

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLinkedURL() {
        return linkedURL;
    }

    public void setLinkedURL(String linkedURL) {
        this.linkedURL = linkedURL;
    }

    public UUID getId() {
        return id;
    }

    public String getOriginatingURL() {
        return originatingURL;
    }

    public void setOriginatingURL(String originatingURL) {
        this.originatingURL = originatingURL;
    }

    public int getReportType() {
        return reportType;
    }

    public void setReportType(int reportType) {
        this.reportType = reportType;
    }

    public ReportEntry() {
        this.exception = new Exception();
        ;
        this.message = "";
        this.setOriginatingURL("");
        this.linkedURL = "";
        this.responseStatus = 0;
        this.exceptionType = "";
        this.id = UUID.randomUUID();
    }

}
