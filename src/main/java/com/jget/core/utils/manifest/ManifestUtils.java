package com.jget.core.utils.manifest;

import java.util.Set;

import com.jget.core.manifest.Manifest;
import com.jget.core.manifest.ManifestProvider;

public class ManifestUtils {
    
    public static void saveManifestToFile(Manifest manifest){
        
    }
    
    public static Set<Manifest> populateManifestBucketFromFileSystem(){
        return null;
    }
    
    public static boolean manifestNameExists(String name){
        
        for(Manifest manifest: ManifestProvider.getManifests()){
            if(manifest.getName().toLowerCase().equals(name.toLowerCase().trim())){
                return true;
            }
        }
        return false;
    }
}
