package com.jget.core.configuration;

public enum ConfigurationConstant {

    FILESTORE("filestore.root.path");

    private final String name;

    private ConfigurationConstant(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}