package Protocol;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ReadapMessageClientTest {

    //Unit test to verify the conversion of the message into an array of bytes
    @Test
    void toByteArrayRemainder() throws IOException {

        byte[] chunk = new byte[] {1,2,3,4};

        ReadapMessageClient message = new ReadapMessageClient((byte) 1, (byte) 2,chunk);

        byte[] messageInBytes = message.toByteArrayRemainder();
        byte[] expected = new byte[] {1,2,0,4,1,2,3,4};


       Assertions.assertTrue(Arrays.compare(messageInBytes,expected) == 0);

    }

    @Test
    void fromByteArrayRemainder() {

        byte[] chunk = new byte[] {1,2,3,4};
        ReadapMessageClient expectedMessage = new ReadapMessageClient((byte) 1, (byte)2,chunk);

        byte[] receivedMessage = {1,2,0,4,1,2,3,4};
        ReadapMessageClient message = ReadapMessageClient.fromByteArrayRemainder(receivedMessage);

        Assertions.assertEquals(expectedMessage,message);

    }

    @Test
    void getCode() {
        byte[] chunk = new byte[] {1,2,3,4};
        ReadapMessageClient message = new ReadapMessageClient((byte) 1, (byte)2,chunk);

        Assertions.assertEquals(message.getCode(),(byte)2);
    }

    @Test
    void getChunk() {

        byte[] chunk = new byte[] {1,2,3,4};
        ReadapMessageClient message = new ReadapMessageClient((byte) 1, (byte)2,chunk);

        Assertions.assertTrue(Arrays.compare(chunk,message.getChunk()) == 0);
    }

    @Test
    void getChunkLength() {
        byte[] chunk = new byte[] {1,2,3,4};
        ReadapMessageClient message = new ReadapMessageClient((byte) 1, (byte)2,chunk);

        Assertions.assertEquals(message.getChunkLength(),(short)4);
    }

}