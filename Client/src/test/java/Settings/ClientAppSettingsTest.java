package Settings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class ClientAppSettingsTest {

    private ClientAppSettings clientAppSettings;

    @BeforeEach
    public void setUp() {
        clientAppSettings = new ClientAppSettings();
    }

    //Unit test for default loading of properties
    @Test
    public void testDefaultPropertiesLoadedWhenFileNotFound() {

        try{
            clientAppSettings = new ClientAppSettings() {
                @Override
                protected InputStream getResourceAsStream(String resourceName) {
                    if ("application.properties".equals(resourceName)) {
                        return null;  // Simulate file not found
                    }
                    return super.getResourceAsStream(resourceName);
                }
            };
        }catch (Exception e){

        }

        assertEquals(61010, clientAppSettings.getConnectionPort());
        assertEquals("JKS", clientAppSettings.getKeystoreType());
        assertEquals("SunX509", clientAppSettings.getManagersAlgorithm());
        assertEquals("PKCS12", clientAppSettings.getTruststoreType());
        assertEquals("TLS", clientAppSettings.getContextProtocol());
        assertEquals(8192, clientAppSettings.getPayloadMaximumSize());
    }

    //Unit test for loading and getters of properties
    @Test
    public void testPropertiesLoadedFromFile() throws Exception {
       String propertiesContent = "ConnectionPort=61010\n"
                + "KeystorePath=PESTI/Device-remote-management-/TLS/client-keystore.jks\n"
                + "KeystoreType=JKS\n"
                + "ManagersAlgorithm=SunX509\n"
                + "TruststorePath=PESTI/Device-remote-management-/TLS/client-truststore.p12\n"
                + "TruststoreType=PKCS12\n"
                + "ContextProtocol=TLS\n"
                + "server.port=8080\n"
                + "PayloadMaximumSize=8192\n"
                + "DownloadFolder=PESTI/Device-remote-management-/ClientFolderPath\n";
        InputStream inputStream = new ByteArrayInputStream(propertiesContent.getBytes());

        clientAppSettings = new ClientAppSettings() {
            @Override
            protected InputStream getResourceAsStream(String resourceName) {
                if ("application.properties".equals(resourceName)) {
                    return inputStream;  // Provide custom properties
                }
                return super.getResourceAsStream(resourceName);
            }
        };

        assertEquals(61010, clientAppSettings.getConnectionPort());
        assertEquals("PESTI/Device-remote-management-/TLS/client-keystore.jks", clientAppSettings.getKeystorePath());
        assertEquals("JKS", clientAppSettings.getKeystoreType());
        assertEquals("SunX509", clientAppSettings.getManagersAlgorithm());
        assertEquals("PESTI/Device-remote-management-/TLS/client-truststore.p12", clientAppSettings.getTruststorePath());
        assertEquals("PKCS12", clientAppSettings.getTruststoreType());
        assertEquals("TLS", clientAppSettings.getContextProtocol());
        assertEquals(8192, clientAppSettings.getPayloadMaximumSize());
        assertEquals("PESTI/Device-remote-management-/ClientFolderPath", clientAppSettings.getDownloadFolder());

    }


}