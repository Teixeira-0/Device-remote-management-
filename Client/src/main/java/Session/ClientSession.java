package Session;

import Protocol.ReadapCodesClient;
import Protocol.ReadapMessageClient;
import Settings.ClientApplication;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

public class ClientSession {
    private SSLSocket sessionSocket;
    private static int SESSION_ID_GENERATOR = 0;
    private static final int payloadMaximumSize = ClientApplication.settings().getPayloadMaximumSize();
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

    public void uploadData(String path) throws IOException {
        InputStream in = sessionSocket.getInputStream();
        OutputStream out = sessionSocket.getOutputStream();

        String [] uploadPath = path.split("/");

        //Request to start upload
        ReadapMessageClient initialMessage = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.UPLOAD,uploadPath[uploadPath.length -1].getBytes());
        out.write(initialMessage.toByteArrayRemainder());

        byte[] chunk = new byte[payloadMaximumSize];
        ReadapMessageClient receivedMessage;
        byte[] fileBytes ;


        File file = new File(path);

        //Send download length response
        ReadapMessageClient response = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.UPLOADACK, ByteBuffer.allocate(Long.BYTES).putLong(file.length()).array());
        out.write(response.toByteArrayRemainder());


        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

        try{

            long i = 0;
            while(i < file.length()) {

                if(i + payloadMaximumSize > file.length()){
                    long remainder = file.length() - i;
                    fileBytes = new byte[(int)remainder];
                    bufferedInputStream.read(fileBytes, 0, (int)remainder);

                    ReadapMessageClient outputMessage = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.DONWLOAD, fileBytes);
                    out.write(outputMessage.toByteArrayRemainder());
                    out.flush();

                }else {
                    fileBytes = new byte[payloadMaximumSize];
                    bufferedInputStream.read(fileBytes, (int) 0, payloadMaximumSize);

                    ReadapMessageClient outputMessage = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.DONWLOAD, fileBytes);
                    out.write(outputMessage.toByteArrayRemainder());
                    out.flush();


                    in.read(chunk);
                    receivedMessage = ReadapMessageClient.fromByteArrayRemainder(chunk);

                    if(receivedMessage.getCode() != ReadapCodesClient.UPLOADACK){
                        break;
                    }
                }

                i = i + payloadMaximumSize;
            }

        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void downloadData(String path) throws IOException {

        InputStream in = sessionSocket.getInputStream();
        OutputStream out = sessionSocket.getOutputStream();
        ReadapMessageClient response;
        ReadapMessageClient message;

        //Request to start remote execution
        ReadapMessageClient initialMessage = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.DONWLOAD,path.getBytes());
        out.write(initialMessage.toByteArrayRemainder());

        //Server response to initial message with the download length
        byte [] chunk = new byte[payloadMaximumSize + 4];
        in.read(chunk);
        response =  ReadapMessageClient.fromByteArrayRemainder(chunk);

        long fileLength = ByteBuffer.wrap(response.getChunk()).getLong();

        if(response.getCode() != ReadapCodesClient.DOWNLOADACK){
            //END CONNECTION
        }

        String [] savePath = path.split("/");
        File file = new File(ClientApplication.settings().getDownloadFolder() + "/" + savePath[savePath.length -1]);

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

        for (int i = 0; i < fileLength; i = i + payloadMaximumSize) {

            try {
                in.read(chunk);
                response = ReadapMessageClient.fromByteArrayRemainder(chunk);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            bufferedOutputStream.write(response.getChunk(),0,response.getChunkLength());

            message = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.DOWNLOADACK, new byte[0]);
            out.write(message.toByteArrayRemainder());
        }

        bufferedOutputStream.flush();
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
            initialMessage = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.REMOTESTART,new byte[0]);
            out.write(initialMessage.toByteArrayRemainder());



            //Server response to initial message
            byte [] chunk = new byte[payloadMaximumSize + 4]; //payload + 2 Bytes and 1 Short
            in.read(chunk);
            response =  ReadapMessageClient.fromByteArrayRemainder(chunk);

            if(response.getCode() != ReadapCodesClient.REMOTEACK){
                //END CONNECTION
            }


            //Send command
            message = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.REMOTECOMMAND,(command +" ;echo ;echo 123098123214123").getBytes());
            out.write(message.toByteArrayRemainder());


            do{

                //obtain response
                in.read(chunk);
                response =  ReadapMessageClient.fromByteArrayRemainder(chunk);

                //handle output in a string
                String[] output =  new String(response.getChunk(), 0, response.getChunkLength(), StandardCharsets.UTF_8).split("\0");


                for (String s: output) {
                    System.out.println(s);
                    //Thread.sleep(50);
                }

                message = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.REMOTEACK, new byte[0]);
                out.write(message.toByteArrayRemainder());

            } while(response.getCode() == ReadapCodesClient.REMOTECOMMANDMESSAGE);

            if(!command.isEmpty()) {
                //Request to exit remote execution
                message = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.REMOTEEXIT, new byte[0]);
                out.write(message.toByteArrayRemainder());
            }


        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    public void initializeRemoteShellMultiple (){

        try{

            //Declaration of local variables
            InputStream in = sessionSocket.getInputStream();
            OutputStream out = sessionSocket.getOutputStream();
            ReadapMessageClient initialMessage;
            ReadapMessageClient response;
            ReadapMessageClient message;


            //Request to start remote execution
            initialMessage = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.REMOTESTART,new byte[0]);
            out.write(initialMessage.toByteArrayRemainder());


            //Server response to initial message
            byte [] chunk = new byte[8196];
            in.read(chunk);
            response =  ReadapMessageClient.fromByteArrayRemainder(chunk);

            if(response.getCode() != ReadapCodesClient.ACK){
                //END CONNECTION
            }

            Scanner scanner = new Scanner(System.in);

            while(!Objects.equals(command = scanner.next(), "exit")) {


                //Send command
                message = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.REMOTECOMMAND, (command + " ;echo ;echo 123098123214123").getBytes());
                out.write(message.toByteArrayRemainder());


                do {

                    //obtain response
                    in.read(chunk);
                    response = ReadapMessageClient.fromByteArrayRemainder(chunk);

                    //handle output in a string
                    String[] output = new String(response.getChunk(), 0, response.getChunkLength(), StandardCharsets.UTF_8).split("\0");


                    for (String s : output) {
                        System.out.println(s);
                        //Thread.sleep(50);
                    }

                    message = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.ACK, new byte[0]);
                    out.write(message.toByteArrayRemainder());

                } while (response.getCode() == ReadapCodesClient.REMOTECOMMANDEND);

            }
            //Request to exit remote execution
            message = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.REMOTEEXIT,new byte[0]);
            out.write(message.toByteArrayRemainder());


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
