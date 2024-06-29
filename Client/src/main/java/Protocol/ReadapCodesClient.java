package Protocol;

public class ReadapCodesClient {

    public static final byte VERSION = 1;
    public static final byte TESTCOM = 0;
    public static final byte ACK = 1;
    public static final byte REMOTESTART = 3;
    public static final byte REMOTECOMMAND = 4;
    public static final byte REMOTECOMMANDMESSAGE = 5;
    public static final byte REMOTECOMMANDEND = 6;
    public static final byte REMOTEEXIT = 7;
    public static final byte DONWLOAD = 8;
    public static final byte DOWNLOADACK = 9;
    public static final byte DOWNLOADPAYLOAD = 10;
    public static final byte UPLOAD = 11;
    public static final byte UPLOADACK = 12;
    public static final byte UPLOADPAYLOAD = 13;
    public static final byte STATUS = 14;
    public static final byte STATUSACK = 15;
    public static final byte STATUSDATA = 16;
    public static final byte EXIT = 17;

    private ReadapCodesClient(){
        //ensure it isn't initialized
    }
}
