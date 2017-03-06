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

    public void incrementNumUpvotes() {
        mNumUpvotes += 1;
    }

    public void decrementNumUpvotes() {
        if (mNumUpvotes > 0) {
            mNumUpvotes -= 1;
        } else {
            mNumUpvotes = 0;
        }
    }

    public void incrementNumDownvotes() {
        mNumDownvotes += 1;
    }

    public void decrementNumDownvotes() {
        if (mNumDownvotes > 0) {
            mNumDownvotes -= 1;
        } else {
            mNumDownvotes = 0;
        }
    }

    public int getJokeID() {
        return mJokeID;
    }

    public String getJokeTitle() {
        return mJokeTitle;
    }

    public void setJokeTitle(String jokeTitle) {
        this.mJokeTitle = jokeTitle;
    }

    public String getJokeSetup() {
        return mJokeSetup;
    }

    public void setJokeSetup(String jokeSetup) {
        this.mJokeSetup = jokeSetup;
    }

    public String getJokePunchline() {
        return mJokePunchline;
    }

    public void setJokePunchline(String jokePunchline) {
        this.mJokePunchline = jokePunchline;
    }

    public int getmNumUpvotes() {
        return mNumUpvotes;
    }

    public void setmNumUpvotes(int mNumUpvotes) {
        this.mNumUpvotes = mNumUpvotes;
    }

    public int getmNumDownvotes() {
        return mNumDownvotes;
    }

    public void setmNumDownvotes(int mNumDownvotes) {
        this.mNumDownvotes = mNumDownvotes;
    }

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