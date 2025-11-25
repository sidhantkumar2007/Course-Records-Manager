package edu.ccrm.util;

import java.io.File;

public class RecursionUtil {
    public static long getDirectorySize(File dir) {
        if (dir == null || !dir.exists()) return 0;
        if (dir.isFile()) return dir.length();
        long total = 0;
        File[] list = dir.listFiles();
        if (list == null) return 0;
        for (File f : list) total += getDirectorySize(f);
        return total;
    }
}
