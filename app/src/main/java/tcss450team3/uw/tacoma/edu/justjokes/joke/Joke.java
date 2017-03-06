package tcss450team3.uw.tacoma.edu.justjokes.joke;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Joke class to handle all the pieces of an individual joke.
 *
 * @author Vlad (2.15.17)
 */
public class Joke implements Serializable, Comparable {
    public static final String JOKE_ID = "jokeID", JOKE_TITLE = "jokeTitle"
            , JOKE_SETUP = "jokeSetup", JOKE_PUNCHLINE = "jokePunchline"
            , NUM_UPVOTES = "numUVotes", NUM_DOWNVOTES = "numDVotes"
            , USER_SUBMITTED="userName";

    /** A Joke's unique ID number. */
    private int mJokeID;

    /** A Joke's title. (i.e. The Monkey Joke) */
    private String mJokeTitle;

    /** A Joke's setup/beginning. */
    private String mJokeSetup;

    /** A Joke's ending. */
    private String mJokePunchline;

    /** A Joke's ending. */
    private int mNumUpvotes;

    /** A Joke's ending. */
    private int mNumDownvotes;

    /**
     * Constructor to initialize all the fields.
     *
     * @param jokeID The Joke's unique ID number.
     * @param jokeTitle The Joke's title.
     * @param jokeSetup The Joke's beginning.
     * @param jokePunchline The Joke's conclusion.
     */
    public Joke(int jokeID, String jokeTitle, String jokeSetup, String jokePunchline, int numUpvotes, int numDownvotes) {
        this.mJokeID = jokeID;
        this.mJokeTitle = jokeTitle;
        this.mJokeSetup = jokeSetup;
        this.mJokePunchline = jokePunchline;
        this.mNumUpvotes = numUpvotes;
        this.mNumDownvotes = numDownvotes;
    }

    /**
     * Parses the json string, returns an error message if unsuccessful.
     * Returns course list if success.
     * @param courseJSON The JSON that contains all of the Jokes' info.
     * @return reason or null if successful.
     */
    public static String parseCourseJSON(String courseJSON, List<Joke> jokeList) {
        String reason = null;
        if (courseJSON != null) {
            try {
                JSONArray arr = new JSONArray(courseJSON);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Joke joke = new Joke(obj.getInt(Joke.JOKE_ID), obj.getString(Joke.JOKE_TITLE)
                            , obj.getString(Joke.JOKE_SETUP), obj.getString(Joke.JOKE_PUNCHLINE)
                            , obj.getInt(Joke.NUM_UPVOTES), obj.getInt(Joke.NUM_DOWNVOTES));
                    jokeList.add(joke);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }
        }
        return reason;
    }

    /**
     * Parses the json string, returns an error message if unsuccessful.
     * Returns course list if success.
     * @param courseJSON The JSON that contains all of the Jokes' info.
     * @return reason or null if successful.
     */
    public static String parseCourseJSON(String courseJSON, Map<Integer, Joke> jokeList) {
        String reason = null;
        if (courseJSON != null) {
            try {
                JSONArray arr = new JSONArray(courseJSON);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Joke joke = new Joke(obj.getInt(Joke.JOKE_ID), obj.getString(Joke.JOKE_TITLE)
                            , obj.getString(Joke.JOKE_SETUP), obj.getString(Joke.JOKE_PUNCHLINE)
                            , obj.getInt(Joke.NUM_UPVOTES), obj.getInt(Joke.NUM_DOWNVOTES));
                    jokeList.put(joke.getJokeID(), joke);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }

        }
        return reason;
    }

    /**
     * Used to incremement the upvote counter field.
     */
    public void incrementNumUpvotes() {
        mNumUpvotes += 1;
    }

    /**
     * Used to decrement the upvote counter field if.
     * If the upvote count can't be decremented below 0.
     */
    public void decrementNumUpvotes() {
        if (mNumUpvotes > 0) {
            mNumUpvotes -= 1;
        } else {
            mNumUpvotes = 0;
        }
    }

    /**
     * Used to incremement the downvote counter field.
     */
    public void incrementNumDownvotes() {
        mNumDownvotes += 1;
    }

    /**
     * Used to decrement the downvote counter field if.
     * If the upvote count can't be decremented below 0.
     */
    public void decrementNumDownvotes() {
        if (mNumDownvotes > 0) {
            mNumDownvotes -= 1;
        } else {
            mNumDownvotes = 0;
        }
    }

    /**
     * Getter method for Joke ID field.
     * @return integer representing the Joke ID
     */
    public int getJokeID() {
        return mJokeID;
    }

    /**
     * Getter method for Joke Title field.
     * @return String containing joke title.
     */
    public String getJokeTitle() {
        return mJokeTitle;
    }

    /**
     * Setter method for Joke Title field.
     */
    public void setJokeTitle(String jokeTitle) {
        this.mJokeTitle = jokeTitle;
    }

    /**
     * Getter method for Joke Setup field.
     * @return String containing joke setup.
     */
    public String getJokeSetup() {
        return mJokeSetup;
    }

    /**
     * Setter method for Joke Setup field.
     */
    public void setJokeSetup(String jokeSetup) {
        this.mJokeSetup = jokeSetup;
    }

    /**
     * Getter method for Joke Punchline field.
     * @return String containing joke punchline.
     */
    public String getJokePunchline() {
        return mJokePunchline;
    }

    /**
     * Setter method for Joke Punchline field.
     */
    public void setJokePunchline(String jokePunchline) {
        this.mJokePunchline = jokePunchline;
    }

    /**
     * Getter method for Joke Upvotes field.
     * @return integer representing the number of upvotes.
     */
    public int getmNumUpvotes() {
        return mNumUpvotes;
    }

    /**
     * Setter method for Joke Upvotes field.
     */
    public void setmNumUpvotes(int mNumUpvotes) {
        this.mNumUpvotes = mNumUpvotes;
    }

    /**
     * Getter method for Joke Downvotes field.
     * @return integer representing the number of downvotes.
     */
    public int getmNumDownvotes() {
        return mNumDownvotes;
    }

    /**
     * Setter method for Joke Downvotes field.
     */
    public void setmNumDownvotes(int mNumDownvotes) {
        this.mNumDownvotes = mNumDownvotes;
    }

    /**
     * Overriden method used by Collections.sort in order to sort jokes by their vote counts.
     *
     * @param o the object being compared to this object.
     * @return integer -1, 0, 1 if less than, same or greater than respectively.
     */
    @Override
    public int compareTo(Object o) {
        Joke other = (Joke) o;
        if (this.getmNumUpvotes() != other.getmNumUpvotes())
            return this.getmNumUpvotes() - other.getmNumUpvotes();
        else {
            if (this.getmNumDownvotes() != other.getmNumDownvotes())
                return other.getmNumDownvotes() - this.getmNumDownvotes();
            else
                return other.getJokeTitle().compareTo(this.getJokeTitle());
        }
    }
}