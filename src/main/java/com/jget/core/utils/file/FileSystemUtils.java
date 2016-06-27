package com.jget.core.utils.file;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

public class FileSystemUtils {

    public static boolean pathExists(Path path) {

        return Files.exists(path);

    }

    public static List<Path> populateFilesList(Path path) throws IOException {
        return populateFilesList(path, "", "");
    }

    public static List<Path> populateFilesListByExtension(Path path, String extension) throws IOException {
        return populateFilesList(path, extension, "");
    }
    
    public static List<Path> populateFilesListByName(Path path, String name) throws IOException {
        return populateFilesList(path, "", name);
    }

    public static List<Path> populateFilesList(Path path, String extension, String fullName) throws IOException {

        List<Path> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {

                if (Files.isDirectory(entry))
                    files.addAll(populateFilesList(path, extension, fullName));

                String fileName = entry.getFileName().toString();

                if (!StringUtils.isEmpty(fullName) && fullName.equals(fileName)) {
                    files.add(entry);
                    continue;
                }

                if (!StringUtils.isEmpty(fullName) && fileName.toLowerCase().endsWith(extension.toLowerCase())) {
                    files.add(entry);
                    continue;
                }

                if (StringUtils.isEmpty(fullName) && StringUtils.isEmpty(extension)) {
                    files.add(entry);
                    continue;
                }

            }
        }
        return files;
    }

}
