package Session;


import Protocol.ReadapCodes;
import Protocol.ReadapMessage;
import Settings.AppSettings;
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

    private void setOs(){

        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            System.out.println("The operating system is Windows.");
            AppSettings.setOS("win");
        } else if (osName.contains("mac")) {
            System.out.println("The operating system is macOS.");
            AppSettings.setOS("mac");
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            System.out.println("The operating system is Unix or Linux.");
            AppSettings.setOS("nux");
        } else {
            System.out.println("The operating system is not recognized.");
        }


    }

    @Override
    public void run() {
        System.out.println("THREAD: " + Thread.currentThread().getName());
        setOs();
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
                    this.downloadData(in,out,receivedMessage.getChunk());
                    break;
                case ReadapCodes.UPLOAD:
                    String p =  new String(receivedMessage.getChunk(), 0, receivedMessage.getChunkLength(), StandardCharsets.UTF_8);
                    this.uploadData(in,out, p);
                    break;
                case ReadapCodes.STATUS:
                    this.statusGathering(in,out);
                    break;

            }

            in.read(chunk);
            receivedMessage = ReadapMessage.fromByteArrayRemainder(chunk);
        }while(receivedMessage.getCode() != ReadapCodes.EXIT);

            Thread.currentThread().interrupt();

        }catch (Exception e){

        }

    }

    public void uploadData(InputStream in, OutputStream out,String fileName) throws IOException {

        //Send ACk response
        ReadapMessage response = new ReadapMessage(ReadapCodes.VERSION, ReadapCodes.UPLOADACK,  new byte[0]);
        out.write(response.toByteArrayRemainder());

        ReadapMessage message;


        //Client response to initial message with the download length
        byte [] chunk = new byte[payloadMaximumSize + 4];
        in.read(chunk);
        response =  ReadapMessage.fromByteArrayRemainder(chunk);

        long fileLength = ByteBuffer.wrap(response.getChunk()).getLong();

        if(response.getCode() != ReadapCodes.UPLOADACK){
            //END CONNECTION
        }


        File file = new File(Application.settings().getUploadFolder() + "/"+ fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

        for (int i = 0; i < fileLength; i = i + payloadMaximumSize) {

            try {
                in.read(chunk);
                response = ReadapMessage.fromByteArrayRemainder(chunk);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            bufferedOutputStream.write(response.getChunk(),0,response.getChunkLength());

            message = new ReadapMessage(ReadapCodes.VERSION, ReadapCodes.UPLOADACK, new byte[0]);
            out.write(message.toByteArrayRemainder());
        }

        bufferedOutputStream.flush();
    }

    public void downloadData(InputStream in, OutputStream out,byte[] path) throws IOException {

        byte[] chunk = new byte[payloadMaximumSize + 4];
        ReadapMessage receivedMessage;
        byte[] fileBytes ;


        File file = new File(new String(path,StandardCharsets.UTF_8));

        //Send download length response
        ReadapMessage response = new ReadapMessage(ReadapCodes.VERSION, ReadapCodes.DOWNLOADACK, ByteBuffer.allocate(Long.BYTES).putLong(file.length()).array());
        out.write(response.toByteArrayRemainder());

        //Ack

        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

        try{

        long i = 0;
        while(i < file.length()) {

            if(i + payloadMaximumSize > file.length()){
                long remainder = file.length() - i;
                fileBytes = new byte[(int)remainder];
                bufferedInputStream.read(fileBytes, 0, (int)remainder);

                ReadapMessage outputMessage = new ReadapMessage(ReadapCodes.VERSION, ReadapCodes.DOWNLOADPAYLOAD, fileBytes);
                out.write(outputMessage.toByteArrayRemainder());
                out.flush();

            }else {
                fileBytes = new byte[payloadMaximumSize];
                bufferedInputStream.read(fileBytes, 0, payloadMaximumSize);

                ReadapMessage outputMessage = new ReadapMessage(ReadapCodes.VERSION, ReadapCodes.DOWNLOADPAYLOAD, fileBytes);
                out.write(outputMessage.toByteArrayRemainder());
                out.flush();

                in.read(chunk);
                receivedMessage = ReadapMessage.fromByteArrayRemainder(chunk);

                if(receivedMessage.getCode() != ReadapCodes.DOWNLOADACK){
                    break;
                }
            }

            i = i + payloadMaximumSize;
        }

        }catch (Exception e){
            System.out.println(e);
        }

    }

    public void statusGathering(InputStream in, OutputStream out) throws IOException {

        //Send ACk response
        ReadapMessage response = new ReadapMessage(ReadapCodes.VERSION, ReadapCodes.STATUSACK,  new byte[0]);
        out.write(response.toByteArrayRemainder());

        //Initialize local variables

        String cmd;
        if (Objects.equals(Application.settings().getOS(), "win")) {
            cmd = "powershell.exe" ;
        } else {
            cmd = "/bin/sh";
        }

        Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();


        OutputStream stdin = p.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

        InputStream stdout = p.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));


        //Send command to the ps buffer
        writer.write("top -l 1 | grep \"CPU usage\"" + " ;echo ;echo 123098123214123");
        writer.newLine();
        writer.flush();


        StringBuilder s = new StringBuilder();
        String line;


        while(!Objects.equals(line = reader.readLine(), "123098123214123")){
            if(line != null) {
                s.append(line).append('\0');
            }
        }

        //Send last chunk of output
        ReadapMessage outputMessage = new ReadapMessage(ReadapCodes.VERSION, ReadapCodes.STATUSDATA,s.toString().getBytes());
        out.write(outputMessage.toByteArrayRemainder());


    }


    public void remoteShell(InputStream in, OutputStream out) throws IOException {

        //Send ACk response
        ReadapMessage response = new ReadapMessage(ReadapCodes.VERSION, ReadapCodes.REMOTEACK, new byte[0]);
        out.write(response.toByteArrayRemainder());


        //Initialize local variables
        String cmd;
        if (Objects.equals(Application.settings().getOS(), "win")) {
            cmd = "powershell.exe" ;
        } else {
            cmd = "/bin/sh";
        }
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

                            if(receivedMessage.getCode() != ReadapCodes.REMOTEACK){
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

                if(receivedMessage.getCode() != ReadapCodes.REMOTEACK){
                    break;
                }

                in.read(chunk);
                receivedMessage = ReadapMessage.fromByteArrayRemainder(chunk);

            } catch (Exception e) {
                System.out.println(e.getMessage());
                //System.out.println("Merdou");
            }

        }
    }

}
