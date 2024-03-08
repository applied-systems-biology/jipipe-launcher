package org.hkijena.jipipe.launcher.api;

import org.hkijena.jipipe.utils.StringUtils;

import java.util.Comparator;

public class VersionComparator implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        return StringUtils.compareVersions(o1, o2);
    }
}
