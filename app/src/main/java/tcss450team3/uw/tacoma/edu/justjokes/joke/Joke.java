package tcss450team3.uw.tacoma.edu.justjokes.joke;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Vlad on 2/15/17.
 */

public class Joke implements Serializable {
    public static final String JOKE_ID = "jokeID", JOKE_TITLE = "jokeTitle"
            , JOKE_SETUP = "jokeSetup", JOKE_PUNCHLINE = "jokePunchline";

    private int jokeID;
    private String jokeTitle;
    private String jokeSetup;
    private String jokePunchline;

    public Joke(int jokeID, String jokeTitle, String jokeSetup, String jokePunchline) {
        this.jokeID = jokeID;
        this.jokeTitle = jokeTitle;
        this.jokeSetup = jokeSetup;
        this.jokePunchline = jokePunchline;
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
        return jokeID;
    }

    public String getJokeTitle() {
        return jokeTitle;
    }

    public void setJokeTitle(String jokeTitle) {
        this.jokeTitle = jokeTitle;
    }

    public String getJokeSetup() {
        return jokeSetup;
    }

    public void setJokeSetup(String jokeSetup) {
        this.jokeSetup = jokeSetup;
    }

    public String getJokePunchline() {
        return jokePunchline;
    }

    public void setJokePunchline(String jokePunchline) {
        this.jokePunchline = jokePunchline;
    }
}
