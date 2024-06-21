package Protocol;

public class ReadapCodes {

    public static final byte TESTCOM = 0;

    public static final byte ACK = 1;

    public static final byte CONT = 2;

    public static final byte CONNECT = 3;

    public static final byte DONWLOAD = 4;

    public static final byte UPLOAD = 5;

    public static final byte STATUS = 6;

    public static final byte EXIT = 7;

    private ReadapCodes(){
        //ensure it isn't initialized
    }
}
