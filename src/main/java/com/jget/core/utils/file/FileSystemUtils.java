package com.jget.core.utils.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

public class FileSystemUtils {

    public static boolean pathExists(Path path) {

        File dirFile = path.toFile();

        if (dirFile.exists())
            return true;

        return false;

    }

    public static List<Path> populateFilesList(Path path) throws IOException {

        return populateFilesList(path, "");
    }

    public static List<Path> populateFilesList(Path path, String filter) throws IOException {

        List<Path> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry))
                    files.addAll(populateFilesList(entry, filter));

                if (StringUtils.isEmpty(filter))
                    files.add(entry);
                else if (entry.toString().endsWith(filter))
                    files.add(entry);
            }
        }
        return files;
    }

}
