package uk.ac.openlab.radio.templates;

import uk.ac.openlab.radio.datatypes.Topic;
import uk.ac.openlab.radio.datatypes.Trailer;

/**
 * Created by kylemontague on 11/04/16.
 */
public interface ShowStructure {

    enum Role{
        Presenter,
        Guest,
        Producer,
        Annotator
    }

    class JoinCode {
        public Role role;
        public String code;

        public JoinCode(Role role, String code){
            this.role = role;
            this.code = code;
        }
    }

    int id();
    String type();
    int guestCount();
    int topicCount();
    int trailerCount();

    Topic topicAtIndex(int index);
    Trailer trailerAtIndex(int index); //todo need to update and use the correct data structure

    String[] twitterTerms();
    String joinCodeForRole(Role role);


    int titleDuration();
    int trailerDuration();
    int topicDuration();
}
