package uk.ac.openlab.radio;

import android.util.Log;

import org.junit.Test;
import org.zeromq.ZMQ;

import static org.junit.Assert.assertTrue;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    String zmqHost = "tcp://freeswitch.di-test.com";
    String sessionID = "53oi-rHKiB3bv2XG11saHxBZ0vG3cTum";


    @Test
    public void studioLogin(){
    }

    @Test
    public void realtimeControl(){
        int port = 3002;
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket requester = context.socket(ZMQ.REQ);
        Log.d("URL",String.format("%s:%d/",zmqHost,port));
        requester.connect(String.format("%s:%d/",zmqHost,port));

        String txt = "============================================== WOW ====================================";
        String msg = String.format("{\n" +
                "    \"cmd\"     :\"send_msg\",\n" +
                "    \"session\" :%s,\n" +
                "    \"studio\"  :%d,\n" +
                "    \"text\"    :%s\n" +
                "}",sessionID,1,txt);
        requester.send(msg.getBytes(),0);
        requester.close();
        assertTrue(true);
    }


    @Test
    public void subscription(){
        int port = 3001;
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket requester = context.socket(ZMQ.REQ);
        requester.connect(String.format("%s:%d/",zmqHost,port));


        assertTrue(true);
    }
}