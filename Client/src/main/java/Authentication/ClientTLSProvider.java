package Authentication;

import Settings.ClientApplication;
import org.springframework.stereotype.Component;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

@Component
public class ClientTLSProvider {

    public SSLSocketFactory tlsConnection() {
        String keystoreType = ClientApplication.settings().getKeystoreType();
        String keystorePath = ClientApplication.settings().getKeystorePath();
        String managerAlgorithm = ClientApplication.settings().getManagersAlgorithm();
        String truststoreType = ClientApplication.settings().getTruststoreType();
        String trustStorePath = ClientApplication.settings().getTruststorePath();
        String contextProtocol = ClientApplication.settings().getContextProtocol();
        Integer connectionPort = ClientApplication.settings().getConnectionPort();


        try {
            // Load the KeyStore
            KeyStore keyStore = KeyStore.getInstance(keystoreType);
            FileInputStream keyStoreFile = new FileInputStream(keystorePath);
            char[] keyStorePassword = "Password1".toCharArray();
            keyStore.load(keyStoreFile, keyStorePassword);

            // Create KeyManagerFactory
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(managerAlgorithm);
            keyManagerFactory.init(keyStore, keyStorePassword);

            // Load the TrustStore
            KeyStore trustStore = KeyStore.getInstance(truststoreType);
            FileInputStream trustStoreFile = new FileInputStream(trustStorePath);
            char[] trustStorePassword = "Password1".toCharArray();
            trustStore.load(trustStoreFile, trustStorePassword);

            // Create TrustManagerFactory
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(managerAlgorithm);
            trustManagerFactory.init(trustStore);

            // Create SSLContext
            SSLContext sslContext = SSLContext.getInstance(contextProtocol);
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            // Create SSLServerSocketFactory
            SSLSocketFactory sslServerSocketFactory = sslContext.getSocketFactory();

            return sslServerSocketFactory;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
