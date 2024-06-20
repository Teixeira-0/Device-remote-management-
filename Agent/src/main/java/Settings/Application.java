package Settings;

public class Application {

    public static final String VERSION = "1.0";

    private static final AppSettings SETTINGS = new AppSettings();

    public static AppSettings settings (){
        return SETTINGS;
    }

    private Application (){
        // ensure singleton
    }


}
