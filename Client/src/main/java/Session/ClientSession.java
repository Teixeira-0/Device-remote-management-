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

    public void uploadData() throws IOException {
        InputStream in = sessionSocket.getInputStream();
        OutputStream out = sessionSocket.getOutputStream();

        //Request to start upload
        ReadapMessageClient initialMessage = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.UPLOAD,0,new byte[0]);
        out.write(initialMessage.toByteArray());

        byte[] chunk = new byte[8192];
        ReadapMessageClient receivedMessage;
        byte[] fileBytes = new byte[8192];


        File file = new File("/Users/felix/Documents/3 Ano/PESTI/Device-remote-management-/ClientFolderPath/transferedfile.pdf");

        //Send download length response
        ReadapMessageClient response = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.ACK, 0, ByteBuffer.allocate(Long.BYTES).putLong(file.length()).array());
        out.write(response.toByteArray());


        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

        try{

            long i = 0;
            while(i < file.length()) {

                if(i + 8192 > file.length()){
                    long remainder = file.length() - i;
                    fileBytes = new byte[(int)remainder];
                    bufferedInputStream.read(fileBytes, 0, (int)remainder);

                    ReadapMessageClient outputMessage = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.DONWLOAD, 0, fileBytes);
                    out.write(outputMessage.toByteArray());
                    out.flush();

                }else {
                    fileBytes = new byte[8192];
                    bufferedInputStream.read(fileBytes, (int) 0, 8192);

                    ReadapMessageClient outputMessage = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.DONWLOAD, 0, fileBytes);
                    out.write(outputMessage.toByteArray());
                    out.flush();


                    in.read(chunk);
                    receivedMessage = ReadapMessageClient.fromByteArray(chunk);

                    if(receivedMessage.getCode() != ReadapCodesClient.ACK){
                        break;
                    }
                }

                i = i + 8192;
            }

        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void downloadData() throws IOException {

        InputStream in = sessionSocket.getInputStream();
        OutputStream out = sessionSocket.getOutputStream();
        ReadapMessageClient response;
        ReadapMessageClient message;

        //Request to start remote execution
        ReadapMessageClient initialMessage = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.DONWLOAD,0,new byte[0]);
        out.write(initialMessage.toByteArray());

        //Server response to initial message with the download length
        byte [] chunk = new byte[8200];
        in.read(chunk);
        response =  ReadapMessageClient.fromByteArray(chunk);

        long fileLength = ByteBuffer.wrap(response.getChunk()).getLong();

        if(response.getCode() != ReadapCodesClient.ACK){
            //END CONNECTION
        }

        File file = new File("/Users/felix/Documents/3 Ano/PESTI/Device-remote-management-/ClientFolderPath/transferedfile.txt");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

        for (int i = 0; i < fileLength; i = i + 8192) {

            try {
                in.read(chunk);
                response = ReadapMessageClient.fromByteArray(chunk);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            bufferedOutputStream.write(response.getChunk(),0,response.getChunkLength());

            message = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.ACK, 0, new byte[0]);
            out.write(message.toByteArray());
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

            int payloadMaximumSize = ClientApplication.settings().getPayloadMaximumSize();

            //Server response to initial message
            byte [] chunk = new byte[payloadMaximumSize + 4]; //payload + 2 Bytes and 1 Short
            in.read(chunk);
            response =  ReadapMessageClient.fromByteArrayRemainder(chunk);

            if(response.getCode() != ReadapCodesClient.ACK){
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

                message = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.ACK, new byte[0]);
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

                } while (response.getCode() == ReadapCodesClient.REMOTECOMMANDMESSAGE);

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
