package Protocol;

public class ReadapCodes {

    public static final byte VERSION = 1;
    public static final byte TESTCOM = 0;
    public static final byte ACK = 1;
    public static final byte REMOTESTART = 3;
    public static final byte REMOTECOMMAND = 4;
    public static final byte REMOTECOMMANDMESSAGE = 5;
    public static final byte REMOTECOMMANDEND = 6;
    public static final byte REMOTEACK = 7;
    public static final byte REMOTEEXIT = 8;
    public static final byte DONWLOAD = 9;
    public static final byte DOWNLOADACK = 10;
    public static final byte DOWNLOADPAYLOAD = 11;
    public static final byte UPLOAD = 12;
    public static final byte UPLOADACK = 13;
    public static final byte UPLOADPAYLOAD = 14;
    public static final byte STATUS = 15;
    public static final byte STATUSACK = 16;
    public static final byte STATUSDATA = 17;
    public static final byte EXIT = 18;
    private ReadapCodes(){
        //ensure it isn't initialized
    }
}
