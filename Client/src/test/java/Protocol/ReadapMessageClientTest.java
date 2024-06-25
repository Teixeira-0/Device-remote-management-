package Protocol;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReadapMessageClientTest {

    @Test
    void toByteArray() {

        ReadapMessageClient message = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.REMOTECOMMAND,0,"ls".getBytes());


    }

    @Test
    void toByteArrayRemainder() {
    }

    @Test
    void fromByteArray() {
    }

    @Test
    void fromByteArrayRemainder() {
    }

    @Test
    void getCode() {
    }

    @Test
    void getChunk() {
    }

    @Test
    void getChunkLength() {
    }
}