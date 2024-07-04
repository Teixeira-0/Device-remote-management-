package Settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class ClientApplicationTest {

    //Unit test to verify that the VERSION constant is correctly set
    @Test
    public void testVersion() {
        Assertions.assertEquals("1.0", ClientApplication.VERSION);
    }

    //Unit test to verify that the settings method returns a non-null ClientAppSettings instance and is from the correct instance
    @Test
    public void testSettings() {
        ClientAppSettings settings = ClientApplication.settings();
        Assertions.assertNotNull(settings);

        Assertions.assertEquals(ClientAppSettings.class, settings.getClass());
    }

    //Unit test to verify that the settings method always returns the same instance
    @Test
    public void testSettingsSingleton() {
        ClientAppSettings settings1 = ClientApplication.settings();
        ClientAppSettings settings2 = ClientApplication.settings();
        Assertions.assertEquals(settings1, settings2);
    }
}