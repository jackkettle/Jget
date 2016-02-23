package com.jget.core;

public class ManifestProvider {

    private static Manifest manifest;

    public static Manifest getManifest() {
        return manifest;
    }

    public static void setManifest(Manifest manifest) {
        ManifestProvider.manifest = manifest;
    }

    public String getReportSummary() {
        
        StringBuilder summary = new StringBuilder();
        return summary.toString();

    }

}
