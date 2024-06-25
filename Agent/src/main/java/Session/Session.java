package Session;


import Protocol.ReadapCodes;
import Protocol.ReadapMessage;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


@RestController
public class Session implements Runnable {

    private SSLSocket sessionSocket;
    private static int SESSION_ID_GENERATOR = 0;
    private final int SESSION_ID;


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

        byte [] chunk = new byte[8196];
        in.read(chunk);

        ReadapMessage receivedMessage = ReadapMessage.fromByteArray(chunk);

        switch(receivedMessage.getCode()){

            case ReadapCodes.REMOTESTART:
                this.remoteShell(in,out);

            case ReadapCodes.EXIT:
                Thread.currentThread().interrupt();
        }
        

        }catch (Exception e){

        }

    }

    public void remoteShell(InputStream in, OutputStream out) throws IOException, InterruptedException {

        //Send ACk response
        ReadapMessage response = new ReadapMessage(ReadapCodes.VERSION, ReadapCodes.ACK, 0, new byte[0]);
        out.write(response.toByteArray());


        //Initialize local variables
        String cmd = "/bin/sh";
        Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();

        OutputStream stdin = p.getOutputStream();
        InputStream stdout = p.getInputStream();

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));

        //Map<Long, Thread> activeThreads = Collections.synchronizedMap(new HashMap<>());

        /*
        Thread monitor = new Thread(() -> {
            while (true) {
                try {
                    Long current_time = System.currentTimeMillis();
                    Long elapsed_time;

                    synchronized (activeThreads) { // Synchronize on the map for iteration
                        Iterator<Map.Entry<Long, Thread>> iterator = activeThreads.entrySet().iterator();

                        while (iterator.hasNext()) {
                            Map.Entry<Long, Thread> entry = iterator.next();
                            Long start_time = entry.getKey();

                            elapsed_time = current_time - start_time;

                            if (elapsed_time > 10000) {
                                Thread value = entry.getValue();
                                value.interrupt();
                                iterator.remove(); // Use iterator to remove the entry
                                //System.out.println("Killed Thread: " + value.getName());
                                //System.out.println("Active Threads: " + activeThreads.size());
                            }
                        }
                    }
                    Thread.sleep(1000); // Sleep to avoid busy-waiting
                } catch (Exception e) {
                    System.out.println(Arrays.toString(e.getStackTrace()));
                }
            }
        });

            monitor.start();
         */


        //Read initial command
        byte[] chunk = new byte[8196];
        in.read(chunk);
        ReadapMessage receivedMessage = ReadapMessage.fromByteArray(chunk);

        while (receivedMessage.getCode() == ReadapCodes.REMOTECOMMAND) {

            String input;

            //Transform message chunk into the desired command
            while ((input = new String(receivedMessage.getChunk(), 0, receivedMessage.getChunkLength(), StandardCharsets.UTF_8)).isEmpty());

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

                        if (s.length() + line.length() < 8192) {
                            s.append(line).append('\0');
                        } else {
                            ReadapMessage outputMessage = new ReadapMessage(ReadapCodes.VERSION, ReadapCodes.REMOTECOMMANDMESSAGE, 0, s.toString().getBytes());
                            out.write(outputMessage.toByteArray());

                            in.read(chunk);
                            receivedMessage = ReadapMessage.fromByteArray(chunk);

                            if(receivedMessage.getCode() != ReadapCodes.ACK){
                                break;
                            }

                            s = new StringBuilder();
                            s.append(line).append('\0');
                        }
                    }

                }

                //Send last chunk of output
                ReadapMessage outputMessage = new ReadapMessage(ReadapCodes.VERSION, ReadapCodes.REMOTECOMMANDEND,0,s.toString().getBytes());
                out.write(outputMessage.toByteArray());

                in.read(chunk);
                receivedMessage = ReadapMessage.fromByteArray(chunk);

                if(receivedMessage.getCode() != ReadapCodes.ACK){
                    break;
                }

                in.read(chunk);
                receivedMessage = ReadapMessage.fromByteArray(chunk);

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
