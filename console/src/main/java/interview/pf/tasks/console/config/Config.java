package interview.pf.tasks.console.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by admin on 24.10.2016.
 */
public final class Config {
    private String dbUrl;
    private String userName;
    private String password;
    private String pathToConfigFile;

    private static Config instance = new Config();

    private Config(){}

    public static Config getInstance(){
        return Objects.isNull(instance) ? new Config() : instance;
    }

    public Config init(String pathToConfigFile) throws IOException {
        this.pathToConfigFile = pathToConfigFile;
        readConfigFromFile(pathToConfigFile);
        return this;
    }

    private void readConfigFromFile(String pathToConfigFile) throws IOException {
        Properties prop = new Properties();
        try(InputStream input = new FileInputStream(pathToConfigFile)) {
            prop.load(input);
            this.dbUrl = prop.getProperty("dbUrl");
            this.userName = prop.getProperty("username");
            this.password = prop.getProperty("password");
        }
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getPathToConfigFile() {
        return pathToConfigFile;
    }

    @Override
    public String toString() {
        return "Config{" +
                "dbUrl='" + dbUrl + '\'' +
                ", userName='" + userName + '\'' +
                ", pathToConfigFile='" + pathToConfigFile + '\'' +
                '}';
    }
}
