package uk.ac.openlab.radio.network;

/**
 * Created by Kyle Montague on 25/03/16.
 */
public interface IMessageListener {
    void success();
    void fail();
    void error();
    void message(String message);
}
