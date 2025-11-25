package edu.ccrm.config;

public class AppConfig {
    private static AppConfig instance;
    private String dataFolder = "data";

    private AppConfig() {
    }

    public static synchronized AppConfig getInstance() {
        if (instance == null) instance = new AppConfig();
        return instance;
    }

    public String getDataFolder() {
        return dataFolder;
    }

    public void setDataFolder(String dataFolder) {
        this.dataFolder = dataFolder;
    }
}
