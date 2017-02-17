package tcss450team3.uw.tacoma.edu.justjokes.joke;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Joke class to handle all the pieces of an individual joke.
 *
 * @author Vlad (2.15.17)
 */
public class Joke implements Serializable {
    public static final String JOKE_ID = "jokeID", JOKE_TITLE = "jokeTitle"
            , JOKE_SETUP = "jokeSetup", JOKE_PUNCHLINE = "jokePunchline";

    /** A Joke's unique ID number. */
    private int mJokeID;

    /** A Joke's title. (i.e. The Monkey Joke) */
    private String mJokeTitle;

    /** A Joke's setup/beginning. */
    private String mJokeSetup;

    /** A Joke's ending. */
    private String mJokePunchline;

    /**
     * Constructor to initialize all the fields.
     *
     * @param jokeID The Joke's unique ID number.
     * @param jokeTitle The Joke's title.
     * @param jokeSetup The Joke's beginning.
     * @param jokePunchline The Joke's conclusion.
     */
    public Joke(int jokeID, String jokeTitle, String jokeSetup, String jokePunchline) {
        this.mJokeID = jokeID;
        this.mJokeTitle = jokeTitle;
        this.mJokeSetup = jokeSetup;
        this.mJokePunchline = jokePunchline;
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
                            , obj.getString(Joke.JOKE_SETUP), obj.getString(Joke.JOKE_PUNCHLINE));
                    jokeList.add(joke);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }

        }
        return reason;
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
}
