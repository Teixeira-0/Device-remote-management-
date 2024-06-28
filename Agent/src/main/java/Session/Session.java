package Session;


import Protocol.ReadapCodes;
import Protocol.ReadapMessage;
import Settings.Application;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;


@RestController
public class Session implements Runnable {

    private SSLSocket sessionSocket;
    private static int SESSION_ID_GENERATOR = 0;
    private final int SESSION_ID;

    private static int payloadMaximumSize = Application.settings().getPayloadMaximumSize();


    public int getSESSION_ID() {
        return SESSION_ID;
    }

    public Session () {
        this.SESSION_ID = SESSION_ID_GENERATOR + 1;
        SESSION_ID_GENERATOR ++;
    }


    public void establishSocket (SSLSocket agentSocket){
        this.sessionSocket = agentSocket;
    }

    @Override
    public void run() {
        System.out.println("THREAD: " + Thread.currentThread().getName());

        try{

        InputStream in = sessionSocket.getInputStream();
        OutputStream out = sessionSocket.getOutputStream();

        byte [] chunk = new byte[payloadMaximumSize + 4];
        in.read(chunk);

        ReadapMessage receivedMessage = ReadapMessage.fromByteArrayRemainder(chunk);

        do{
            switch(receivedMessage.getCode()){

                case ReadapCodes.REMOTESTART:
                    this.remoteShell(in,out);
                    break;
                case ReadapCodes.DONWLOAD:
                    this.downloadData(in,out);
                    break;
                case ReadapCodes.UPLOAD:
                    this.uploadData(in,out);
                    break;

            }

            in.read(chunk);
            receivedMessage = ReadapMessage.fromByteArray(chunk);
        }while(receivedMessage.getCode() != ReadapCodes.EXIT);

            Thread.currentThread().interrupt();

        }catch (Exception e){

        }

    }

    public void uploadData(InputStream in, OutputStream out) throws IOException {

        //Send ACk response
        ReadapMessage response = new ReadapMessage(ReadapCodes.VERSION, ReadapCodes.ACK, 0, new byte[0]);
        out.write(response.toByteArray());

        ReadapMessage message;


        //Client response to initial message with the download length
        byte [] chunk = new byte[payloadMaximumSize + 4];
        in.read(chunk);
        response =  ReadapMessage.fromByteArray(chunk);

        long fileLength = ByteBuffer.wrap(response.getChunk()).getLong();

        if(response.getCode() != ReadapCodes.ACK){
            //END CONNECTION
        }

        File file = new File("/Users/felix/Documents/3 Ano/PESTI/Device-remote-management-/AgentFolderPath/transfered.pdf");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

        for (int i = 0; i < fileLength; i = i + payloadMaximumSize) {

            try {
                in.read(chunk);
                response = ReadapMessage.fromByteArray(chunk);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            bufferedOutputStream.write(response.getChunk(),0,response.getChunkLength());

            message = new ReadapMessage(ReadapCodes.VERSION, ReadapCodes.ACK, 0, new byte[0]);
            out.write(message.toByteArray());
        }

        bufferedOutputStream.flush();
    }

    public void downloadData(InputStream in, OutputStream out) throws IOException {

        byte[] chunk = new byte[payloadMaximumSize + 4];
        ReadapMessage receivedMessage;
        byte[] fileBytes ;


        File file = new File("/Users/felix/Documents/3 Ano/PESTI/Device-remote-management-/AgentFolderPath/test.txt");

        //Send download length response
        ReadapMessage response = new ReadapMessage(ReadapCodes.VERSION, ReadapCodes.ACK, 0, ByteBuffer.allocate(Long.BYTES).putLong(file.length()).array());
        out.write(response.toByteArray());


        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

        try{

        long i = 0;
        while(i < file.length()) {

            if(i + payloadMaximumSize > file.length()){
                long remainder = file.length() - i;
                fileBytes = new byte[(int)remainder];
                bufferedInputStream.read(fileBytes, 0, (int)remainder);

                ReadapMessage outputMessage = new ReadapMessage(ReadapCodes.VERSION, ReadapCodes.DONWLOAD, 0, fileBytes);
                out.write(outputMessage.toByteArray());
                out.flush();

            }else {
                fileBytes = new byte[payloadMaximumSize];
                bufferedInputStream.read(fileBytes, (int) 0, payloadMaximumSize);

                ReadapMessage outputMessage = new ReadapMessage(ReadapCodes.VERSION, ReadapCodes.DONWLOAD, 0, fileBytes);
                out.write(outputMessage.toByteArray());
                out.flush();


                in.read(chunk);
                receivedMessage = ReadapMessage.fromByteArray(chunk);

                if(receivedMessage.getCode() != ReadapCodes.ACK){
                    break;
                }
            }

            i = i + payloadMaximumSize;
        }

        }catch (Exception e){
            System.out.println(e);
        }



    }


    public void remoteShell(InputStream in, OutputStream out) throws IOException {

        //Send ACk response
        ReadapMessage response = new ReadapMessage(ReadapCodes.VERSION, ReadapCodes.ACK,  new byte[0]);
        out.write(response.toByteArrayRemainder());


        //Initialize local variables
        String cmd = "/bin/sh";
        Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();

        OutputStream stdin = p.getOutputStream();
        InputStream stdout = p.getInputStream();

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));


        //Read initial command
        byte[] chunk = new byte[payloadMaximumSize + 4];
        in.read(chunk);
        ReadapMessage receivedMessage = ReadapMessage.fromByteArrayRemainder(chunk);

        while (receivedMessage.getCode() == ReadapCodes.REMOTECOMMAND) {

            String input;

            //Transform message chunk into the desired command
            if ((input = new String(receivedMessage.getChunk(), 0, receivedMessage.getChunkLength(), StandardCharsets.UTF_8)).equals(" ;echo ;echo 123098123214123")){
                ReadapMessage outputMessage = new ReadapMessage(ReadapCodes.VERSION, ReadapCodes.REMOTECOMMANDEND,"".getBytes());
                out.write(outputMessage.toByteArrayRemainder());
                break;
            }

            //Send command to the ps buffer
            writer.write(input);
            writer.newLine();
            writer.flush();


            //Handle command execution
            StringBuilder s = new StringBuilder();
            String line;
            try{
                while(!Objects.equals(line = reader.readLine(), "123098123214123")){
                    if(line != null) {

                        if (s.length() + line.length() < payloadMaximumSize) {
                            s.append(line).append('\0');
                        } else {
                            ReadapMessage outputMessage = new ReadapMessage(ReadapCodes.VERSION, ReadapCodes.REMOTECOMMANDMESSAGE, s.toString().getBytes());
                            out.write(outputMessage.toByteArrayRemainder());
                            //FLUSH?????

                            in.read(chunk);
                            receivedMessage = ReadapMessage.fromByteArrayRemainder(chunk);

                            if(receivedMessage.getCode() != ReadapCodes.ACK){
                                break;
                            }

                            s = new StringBuilder();
                            s.append(line).append('\0');
                        }
                    }

                }

                //Send last chunk of output
                ReadapMessage outputMessage = new ReadapMessage(ReadapCodes.VERSION, ReadapCodes.REMOTECOMMANDEND,s.toString().getBytes());
                out.write(outputMessage.toByteArrayRemainder());

                in.read(chunk);
                receivedMessage = ReadapMessage.fromByteArrayRemainder(chunk);

                if(receivedMessage.getCode() != ReadapCodes.ACK){
                    break;
                }

                in.read(chunk);
                receivedMessage = ReadapMessage.fromByteArrayRemainder(chunk);

            } catch (Exception e) {
                System.out.println(e.getMessage());
                //System.out.println("Merdou");
            }




/*
            Thread d = getThread(reader, out);
            Long current_time = System.currentTimeMillis();
            activeThreads.put(current_time, d);

            in.read(chunk);


 */

        }
    }

    private static Thread getThread(BufferedReader reader,OutputStream out) {



        Thread d = new Thread( () -> {

            StringBuilder s = new StringBuilder();
            String line;
            try{
                while(!Objects.equals(line = reader.readLine(), "0#%%64868$")){

                    if(s.length() < 8192){
                        s.append(line).append('\0');
                    }else {
                        ReadapMessage outputMessage = new ReadapMessage(ReadapCodes.VERSION, ReadapCodes.REMOTECOMMANDMESSAGE,0,s.toString().getBytes());
                        out.write(outputMessage.toByteArray());
                    }

                }


                ReadapMessage outputMessage = new ReadapMessage(ReadapCodes.VERSION, ReadapCodes.REMOTECOMMANDEND,0,s.toString().getBytes());

                out.write(outputMessage.toByteArray());


            } catch (Exception e) {
                System.out.println("Merdou");
            }

        });

        d.start();
        return d;
    }
}
