package com.jget.core.manifest;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class ManifestProvider {

    private static Manifest currentManifest;

    private static Set<Manifest> manifests;

    public ManifestProvider() {
        currentManifest = null;
        manifests = new HashSet<Manifest>();
    }

    public static Manifest getCurrentManifest() {
        return currentManifest;
    }

    public static void setCurrentManifest(Manifest manifest) {
        ManifestProvider.currentManifest = manifest;
    }

    public static Set<Manifest> getManifests() {
        return manifests;
    }

    public static void setManifests(Set<Manifest> manifests) {
        ManifestProvider.manifests = manifests;
    }

}
