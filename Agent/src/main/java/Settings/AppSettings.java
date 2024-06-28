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
    private static final String CONNECTION_PORT = "ConnectionPort";
    private static final String SESSION_POOL_SIZE = "SessionPoolSize";
    private static final String KEYSTORE_PATH = "KeystorePath";
    private static final String KEYSTORE_TYPE = "KeystoreType";
    private static final String MANAGERS_ALGORITHM = "ManagersAlgorithm";
    private static final String TRUSTSTORE_PATH = "TruststorePath";
    private static final String TRUSTSTORE_TYPE = "TruststoreType";
    private static final String CONTEXT_PROTOCOL = "ContextProtocol";
    private static final String PAYLOAD_MAXIMUM_SIZE = "PayloadMaximumSize";

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

        //Default Connection Port
        this.applicationProperties.setProperty(CONNECTION_PORT,"61010");

        //Default Session Pool Size
        this.applicationProperties.setProperty(SESSION_POOL_SIZE,"15");

        //Default Keystore Type
        this.applicationProperties.setProperty(KEYSTORE_TYPE,"JKS");

        //Default Managers Algorithm
        this.applicationProperties.setProperty(MANAGERS_ALGORITHM,"SunX509");

        //Default Truststore Type
        this.applicationProperties.setProperty(TRUSTSTORE_TYPE,"PKCS12");

        //Default Context Protocol
        this.applicationProperties.setProperty(CONTEXT_PROTOCOL,"TLS");

        //Default Payload Maximum Size
        this.applicationProperties.setProperty(PAYLOAD_MAXIMUM_SIZE,"8192");

    }

    public Integer getConnectionPort(){
        return Integer.valueOf(this.applicationProperties.getProperty(CONNECTION_PORT));
    }

    public Integer getSessionPoolSize(){
        return Integer.valueOf(this.applicationProperties.getProperty(SESSION_POOL_SIZE));
    }

    public String getKeystorePath(){
        return this.applicationProperties.getProperty(KEYSTORE_PATH);
    }

    public String getKeystoreType(){
        return this.applicationProperties.getProperty(KEYSTORE_TYPE);
    }

    public String getManagersAlgorithm(){
        return this.applicationProperties.getProperty(MANAGERS_ALGORITHM);
    }

    public String getTruststorePath(){
        return this.applicationProperties.getProperty(TRUSTSTORE_PATH);
    }

    public String getTruststoreType(){
        return this.applicationProperties.getProperty(TRUSTSTORE_TYPE);
    }

    public String getContextProtocol (){
        return this.applicationProperties.getProperty(CONTEXT_PROTOCOL);
    }

    public String getProperty(final String prop) {
        return this.applicationProperties.getProperty(prop);
    }

    public Integer getPayloadMaximumSize(){
        return  Integer.valueOf(this.applicationProperties.getProperty(PAYLOAD_MAXIMUM_SIZE));
    }
}
