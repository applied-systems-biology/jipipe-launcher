package org.hkijena.jipipe.launcher.api;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class JIPipePackageRepository {
    private static JIPipePackageRepository INSTANCE;
    private final List<JIPipeAvailablePackage> availablePackageList = new ArrayList<>();
    private final List<JIPipeInstalledPackage> installedPackageList = new ArrayList<>();

    public JIPipePackageRepository() {
        for (int i = 0; i < 5; i++) {
            availablePackageList.add(new JIPipeAvailablePackage());
        }
        installedPackageList.add(new JIPipeInstalledPackage());
    }

    public static JIPipePackageRepository getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new JIPipePackageRepository();
        }
        return INSTANCE;
    }

    public List<JIPipeAvailablePackage> getAvailablePackageList() {
        return ImmutableList.copyOf(availablePackageList);
    }

    public List<JIPipeInstalledPackage> getInstalledPackageList() {
        return ImmutableList.copyOf(installedPackageList);
    }

    public boolean contains(JIPipePackage pipePackage) {
        return availablePackageList.contains(pipePackage) || installedPackageList.contains(pipePackage);
    }

    public JIPipeInstalledPackage findLatestInstalledPackage() {
        return installedPackageList.get(0);
    }

    public JIPipeAvailablePackage findLatestAvailablePackage() {
        return availablePackageList.get(0);
    }
}
