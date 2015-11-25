package com.jget.core.linkresolver;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jget.core.ManifestProvider;
import com.jget.core.utils.file.FileSystemUtils;

public class LinkResolverManager {

    public void commenceResolving(){
            
        List<Path> htmlFiles = null;
        try {
            htmlFiles = FileSystemUtils.populateFilesList(ManifestProvider.getManifest().getRootDir());
        } catch (IOException e) {
            logger.info("Failed to populate file list", e);
            return;
        }
        logger.info("Total number of files to search: {}", htmlFiles.size());
        
    }
    
    private static final Logger logger = LoggerFactory.getLogger(LinkResolverManager.class);

}
