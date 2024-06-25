package Protocol;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ReadapMessage {

    private Byte version;

    private Byte code;

    private Integer totalChunks;

    private  short chunkLength;

    //chunk size = 32 * chunkLength
    private byte [] chunk;


    public ReadapMessage(Byte version, Byte code, Integer totalChunks, byte[] chunk) {
        this.version = version;
        this.code = code;
        this.totalChunks = totalChunks;
        this.chunkLength = (short) chunk.length;
        this.chunk = chunk;
    }

    public ReadapMessage(Byte version, Byte code, byte[] chunk) {
        this.version = version;
        this.code = code;
        this.chunkLength = (short) chunk.length;
        this.chunk = chunk;
    }


    public byte[] toByteArray() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeByte(this.version);
        dos.writeByte(this.code);
        dos.writeInt(this.totalChunks);
        dos.writeShort(this.chunkLength & 0xFFFF);
        dos.write(this.chunk);

        return baos.toByteArray();

    }


    public byte[] toByteArrayRemainder() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeByte(version);
        dos.writeByte(code);
        dos.writeShort(chunkLength);
        dos.write(chunk);

        return baos.toByteArray();

    }

    public static ReadapMessage fromByteArray(byte[] bytes){

        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        byte version = buffer.get();
        byte code = buffer.get();
        int totalChunks = buffer.getInt();
        short chunkLength = buffer.getShort();
        byte [] chunk = new byte[chunkLength];
        buffer.get(chunk);

        return new ReadapMessage(version,code,totalChunks,chunk);

    }

    public static ReadapMessage fromByteArrayRemainder(byte[] bytes){

        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        byte version = buffer.get();
        byte code = buffer.get();
        short chunkLength = buffer.getShort();
        byte [] chunk = new byte[chunkLength];
        buffer.get(chunk);

        return new ReadapMessage(version,code,chunk);

    }

    public Byte getCode() {
        return code;
    }

    public byte[] getChunk() {
        return chunk;
    }

    public short getChunkLength() {
        return chunkLength;
    }
}
