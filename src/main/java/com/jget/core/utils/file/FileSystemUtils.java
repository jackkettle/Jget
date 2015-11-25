package com.jget.core.utils.file;

import java.io.File;
import java.nio.file.Path;

public class FileSystemUtils {

    public static boolean pathExists(Path path){
        
        File dirFile = path.toFile();
        
        if(dirFile.exists())
            return true;
        
        return false;
        
    }
    
}
