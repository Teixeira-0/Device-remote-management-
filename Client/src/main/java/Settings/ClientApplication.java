package Settings;

public class ClientApplication {

    public static final String VERSION = "1.0";

    private static final ClientAppSettings SETTINGS = new ClientAppSettings();

    public static ClientAppSettings settings (){
        return SETTINGS;
    }

    private ClientApplication(){
        //ensure singleton
    }
}
