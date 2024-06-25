package Protocol;

public class ReadapCodes {

    public static final byte VERSION = 1;
    public static final byte TESTCOM = 0;
    public static final byte ACK = 1;
    public static final byte CONT = 2;
    public static final byte REMOTESTART = 3;
    public static final byte REMOTECOMMAND = 4;
    public static final byte REMOTECOMMANDMESSAGE = 5;
    public static final byte REMOTECOMMANDEND = 6;
    public static final byte REMOTEEXIT = 7;
    public static final byte DONWLOAD = 8;
    public static final byte UPLOAD = 9;
    public static final byte STATUS = 10;
    public static final byte EXIT = 11;
    private ReadapCodes(){
        //ensure it isn't initialized
    }
}
