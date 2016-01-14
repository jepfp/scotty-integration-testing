package ch.adoray.scotty.integrationtest.base;

import java.io.IOException;
import java.util.Properties;
public class Configuration {
    private static Configuration configuration = null;
    private Properties configFile = new Properties();

    private Configuration() {
        try {
            configFile.load(this.getClass().getClassLoader().getResourceAsStream("basic.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Configuration config() {
        if (configuration == null) {
            configuration = new Configuration();
        }
        return configuration;
    }

    public String getBaseUrl() {
        return configFile.getProperty("server") + configFile.getProperty("rootServerFolder");
    }

    public String getExtDirectUrl() {
        return getBaseUrl() + "/src/ext-direct-router.php";
    }

    public String getSqliteDbDumpUrl() {
        return getBaseUrl() + "/src/sqlite-db.php";
    }

    public String getLastUpdateUrl() {
        return getBaseUrl() + "/src/last-update.php";
    }

    public String getRestInterfaceUrl() {
        return getBaseUrl() + "/src/ext-rest-interface.php";
    }

    public String getConnectionString() {
        String connectionString = "jdbc:mysql://" + configFile.getProperty("db.host") + ":" + configFile.getProperty("db.port") + "/"//
            + configFile.getProperty("db.name") + "?"//
            + "user=" + configFile.getProperty("db.username") + "&"//
            + "password=" + configFile.getProperty("db.password") //
            + "&characterEncoding=utf8";
        return connectionString;
    }

    public String getTesterEmail() {
        return configFile.getProperty("tester.email");
    }

    public String getTesterPassword() {
        return configFile.getProperty("tester.password");
    }
}
