package uk.ac.openlab.radio.templates;

//import com.google.repacked.antlr.v4.runtime.misc.NotNull;

import uk.ac.openlab.radio.datatypes.Caller;
import uk.ac.openlab.radio.datatypes.Topic;
import uk.ac.openlab.radio.datatypes.Trailer;

/**
 * Created by Kyle Montague on 11/04/16.
 */
public class TwoTopicShow implements ShowStructure {


    Topic[] topics;
    Trailer[] trailers;
    Caller[] guests;
    String[] twitterTerms;
    int id = 1;
    String type = "Two Topic Talk Show";
    JoinCode[] joinCodes;

    public TwoTopicShow(Topic topic1, Topic topic2, Caller guest,  String[] twitterTerms, JoinCode[] joinCodes){
        this.guests = new Caller[]{guest};
        this.topics = new Topic[]{topic1,topic2};
        this.twitterTerms = twitterTerms;
        this.joinCodes = joinCodes;
    }

    // @NotNull Deprecated -dks
    /*public TwoTopicShow(@NotNull Topic topic1, @NotNull Topic topic2, @NotNull Caller guest,  @NotNull String[] twitterTerms, JoinCode[] joinCodes){
        this.guests = new Caller[]{guest};
        this.topics = new Topic[]{topic1,topic2};
        this.twitterTerms = twitterTerms;
        this.joinCodes = joinCodes;
    }*/

//    public TwoTopicShow(@NotNull Topic[] topics, @NotNull String[] trailers, @NotNull Caller[] guests, @NotNull String[] twitterTerms, JoinCode[] joinCodes) {
//        this.topics = topics;
//        this.trailers = trailers;
//        this.guests = guests;
//        this.twitterTerms = twitterTerms;
//        this.joinCodes = joinCodes;
//    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public int guestCount() {
        return guests.length;
    }

    @Override
    public int topicCount() {
        return topics.length;
    }

    @Override
    public int trailerCount() {
        return trailers.length;
    }

    @Override
    public Topic topicAtIndex(int index) {
        if(topics == null || index < 0 || index > topics.length-1 )
            return null;
        return topics[index];
    }

    @Override
    public Trailer trailerAtIndex(int index) {
        if( trailers == null || index < 0 || index > trailers.length-1)
            return null;
        return trailers[index];
    }

    @Override
    public String[] twitterTerms() {
        return this.twitterTerms;
    }

    @Override
    public String joinCodeForRole(Role role) {
        if( joinCodes != null) {
            for (JoinCode j : joinCodes)
                if (j.role == role)
                    return j.code;
        }
        return null;
    }

    private final int ONE_MINUTE = 60;
    @Override
    public int titleDuration() {
        return 10;
    }

    @Override
    public int trailerDuration() {
        return 30;
    }

    @Override
    public int topicDuration() {
        return 6*ONE_MINUTE;
    }
}
