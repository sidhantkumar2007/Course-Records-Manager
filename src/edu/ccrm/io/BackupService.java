package edu.ccrm.io;

import java.nio.file.*;

public class BackupService {
    public void backupData(String sourceDir, String backupDir) throws Exception {
        Path source = Paths.get(sourceDir);
        Path target = Paths.get(backupDir);
        Files.walk(source).forEach(path -> {
            try {
                Path dest = target.resolve(source.relativize(path));
                if (Files.isDirectory(path)) {
                    Files.createDirectories(dest);
                } else {
                    Files.copy(path, dest, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
