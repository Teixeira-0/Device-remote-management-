package Protocol;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ReadapMessageClient {

    private static byte LENGTHMULTIPLE = 32;

    private Byte version;

    private Byte code;

    private Integer totalChunks;

    private byte chunkLength;

    //chunk size = 32 * chunkLength
    private byte [] chunk;


    public ReadapMessageClient(Byte version, Byte code, Integer totalChunks, byte[] chunk) {
        this.version = version;
        this.code = code;
        this.totalChunks = totalChunks;
        this.chunkLength = (byte) (chunk.length / LENGTHMULTIPLE);
        this.chunk = chunk;
    }

    public ReadapMessageClient(Byte version, Byte code, byte[] chunk) {
        this.version = version;
        this.code = code;
        this.chunkLength = (byte) (chunk.length / LENGTHMULTIPLE);
        this.chunk = chunk;
    }


    public byte[] toByteArray() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeByte(version);
        dos.writeByte(code);
        dos.writeInt(totalChunks);
        dos.writeByte(chunkLength);
        dos.write(chunk);

        return baos.toByteArray();

    }


    public byte[] toByteArrayRemainder() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeByte(version);
        dos.writeByte(code);
        dos.writeByte(chunkLength);
        dos.write(chunk);

        return baos.toByteArray();

    }

    public static ReadapMessageClient fromByteArray(byte[] bytes){

        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        byte version = buffer.get();
        byte code = buffer.get();
        int totalChunks = buffer.getInt();
        byte chunkLength = buffer.get();
        byte [] chunk = new byte[chunkLength];
        buffer.get(chunk);

        return new ReadapMessageClient(version,code,totalChunks,chunk);

    }

    public static ReadapMessageClient fromByteArrayRemainder(byte[] bytes){

        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        byte version = buffer.get();
        byte code = buffer.get();
        byte chunkLength = buffer.get();
        byte [] chunk = new byte[chunkLength];
        buffer.get(chunk);

        return new ReadapMessageClient(version,code,chunk);

    }


}
