package Session;

import Protocol.ReadapCodesClient;
import Protocol.ReadapMessageClient;

import javax.net.ssl.SSLSocket;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ClientSession {
    private SSLSocket sessionSocket;
    private static int SESSION_ID_GENERATOR = 0;
    private final int SESSION_ID;

    private String command;
    public Thread remoteShellThread;

    public int getSESSION_ID() {
        return SESSION_ID;
    }

    public ClientSession () {
        this.SESSION_ID = SESSION_ID_GENERATOR + 1;
        SESSION_ID_GENERATOR ++;


        remoteShellThread = new Thread(this::initializeRemoteShell);
    }

    //SWITCH TO PRIVATE WITH THREAD
    public void initializeRemoteShell (){

        try{

            //Declaration of local variables
            InputStream in = sessionSocket.getInputStream();
            OutputStream out = sessionSocket.getOutputStream();
            ReadapMessageClient initialMessage;
            ReadapMessageClient response;
            ReadapMessageClient message;


            //Request to start remote execution
            initialMessage = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.REMOTESTART,0,new byte[0]);
            out.write(initialMessage.toByteArray());


            //Server response to initial message
            byte [] chunk = new byte[8196];
            in.read(chunk);
            response =  ReadapMessageClient.fromByteArray(chunk);

            if(response.getCode() != ReadapCodesClient.ACK){
                //END CONNECTION
            }


            //Send command
            message = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.REMOTECOMMAND,0,(command +" ;echo ;echo 123098123214123").getBytes());
            out.write(message.toByteArray());


            do{

                //obtain response
                in.read(chunk);
                response =  ReadapMessageClient.fromByteArray(chunk);

                //handle output in a string
                String[] output =  new String(response.getChunk(), 0, response.getChunkLength(), StandardCharsets.UTF_8).split("\0");


                for (String s: output) {
                    System.out.println(s);
                    //Thread.sleep(50);
                }

                message = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.ACK, 0, new byte[0]);
                out.write(message.toByteArray());

            } while(response.getCode() == ReadapCodesClient.REMOTECOMMANDMESSAGE);

            //Request to exit remote execution
            message = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.REMOTEEXIT,0,new byte[0]);
            out.write(message.toByteArray());


        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void establishSocket (SSLSocket agentSocket){
        this.sessionSocket = agentSocket;
    }

}
