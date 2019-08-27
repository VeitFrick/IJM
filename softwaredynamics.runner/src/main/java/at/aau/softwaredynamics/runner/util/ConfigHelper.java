package at.aau.softwaredynamics.runner.util;

import at.aau.softwaredynamics.runner.git.RepositoryAnalyzer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by thomas on 13.02.2017.
 */
public class ConfigHelper {
    private static final Logger logger = LogManager.getLogger(ConfigHelper.class);

    private String configFile;
    private long lastModified;

    private Properties properties;

    public ConfigHelper(String configFile) {

        this.configFile = configFile;
    }

    public ConfigHelper() {
        this("config.properties");
    }

    public String getString(String key) {
        return (String) getProperties().getOrDefault(key,null);
    }

    private Properties getProperties() {
        File configFile = new File(this.configFile);

        if (this.lastModified != configFile.lastModified()) {
            this.properties = new Properties();
            InputStream input = null;

            try {
                input = new FileInputStream(this.configFile);
                this.properties.load(input);
                this.lastModified = configFile.lastModified();
            } catch (IOException ex) {
                ex.printStackTrace();
                logger.error("Could not load config file", ex);
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.error(e);
                    }
                }
            }
        }

        return this.properties;
    }
}
