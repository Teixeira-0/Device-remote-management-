package Protocol;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

public class ReadapMessageClient {

    private Byte version;

    private Byte code;

    private Integer totalChunks;

    private short chunkLength;

    //chunk size = 32 * chunkLength
    private byte [] chunk;


    public ReadapMessageClient(Byte version, Byte code, Integer totalChunks, byte[] chunk) {
        this.version = version;
        this.code = code;
        this.totalChunks = totalChunks;
        this.chunkLength = (short) chunk.length;
        this.chunk = chunk;
    }

    public ReadapMessageClient(Byte version, Byte code, byte[] chunk) {
        this.version = version;
        this.code = code;
        this.chunkLength = (short) chunk.length;
        this.chunk = chunk;
    }


    public byte[] toByteArray() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeByte(version);
        dos.writeByte(code);
        dos.writeInt(totalChunks);
        dos.writeShort(chunkLength);
        dos.write(chunk);

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

    public static ReadapMessageClient fromByteArray(byte[] bytes){

        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        byte version = buffer.get();
        byte code = buffer.get();
        int totalChunks = buffer.getInt();
        short chunkLength = buffer.getShort();
        byte [] chunk = new byte[chunkLength];
        buffer.get(chunk);

        return new ReadapMessageClient(version,code,totalChunks,chunk);

    }

    public static ReadapMessageClient fromByteArrayRemainder(byte[] bytes){

        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        byte version = buffer.get();
        byte code = buffer.get();
        short chunkLength = buffer.getShort();
        byte [] chunk = new byte[chunkLength];
        buffer.get(chunk);

        return new ReadapMessageClient(version,code,chunk);

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

    public void setChunk(byte[] chunk) {
        this.chunk = chunk;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReadapMessageClient that = (ReadapMessageClient) o;
        return chunkLength == that.chunkLength && Objects.equals(version, that.version) && Objects.equals(code, that.code) && Objects.equals(totalChunks, that.totalChunks) && Arrays.equals(chunk, that.chunk);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(version, code, totalChunks, chunkLength);
        result = 31 * result + Arrays.hashCode(chunk);
        return result;
    }
}
