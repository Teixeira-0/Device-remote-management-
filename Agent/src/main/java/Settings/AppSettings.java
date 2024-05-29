package Settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppSettings {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppSettings.class);

    private static final String PROPERTIES_RESOURCE = "application.properties";
    private static final String SESSION_POOL_SIZE = "SessionPoolSize";
    private static final String CONNECTION_PORT = "ConnectionPort";

    private final Properties applicationProperties = new Properties();

    public AppSettings() {
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream propertiesStream = this.getClass().getClassLoader().getResourceAsStream(PROPERTIES_RESOURCE)) {
            if (propertiesStream != null) {

                this.applicationProperties.load(propertiesStream);

            } else {
                throw new FileNotFoundException(

                        "Property file '" + PROPERTIES_RESOURCE + "' not found!!!");
            }
        } catch (final IOException exio) {
            setDefaultProperties();

            LOGGER.warn("Default properties loaded ...", exio);
        }
    }

    private void setDefaultProperties() {
        //Default Session Pool Size
        this.applicationProperties.setProperty(SESSION_POOL_SIZE,"15");

        //Default Connection Port
        this.applicationProperties.setProperty(CONNECTION_PORT,"61010");
    }

    public Integer getSessionPoolSize(){
        return Integer.valueOf(this.applicationProperties.getProperty(SESSION_POOL_SIZE));
    }

    public Integer getConnectionPort(){
        return Integer.valueOf(this.applicationProperties.getProperty(CONNECTION_PORT));
    }

    public String getProperty(final String prop) {
        return this.applicationProperties.getProperty(prop);
    }
}
