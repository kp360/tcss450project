package tcss450team3.uw.tacoma.edu.justjokes;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 *  The purpose of this activity is for the user to log in to an existing
 *  account in our database, or to register and add a user to the database.
 *
 *  @author Kyle Phan 3/1/17
 */
public class LoginActivity extends AppCompatActivity {

    /** The URL of the php file that handles user logins. */
    private static final String LOGIN_URL
            = "http://cssgate.insttech.washington.edu/~_450bteam3/login.php?";

    /** The URL of the php file that handles user registration. */
    private static final String REGIS_URL
            = "http://cssgate.insttech.washington.edu/~_450bteam3/adduser.php?";

    /** The amount of jokes that are displayed on each page. */
    private static final int NUM_JOKES_PER_PAGE = 20;

    /** The EditText where users type their username. */
    private EditText mUserUsernameEditText;

    /** The EditText where users type their password. */
    private EditText mUserPasswordEditText;

    /**
     * Method called when this Activity is created.
     *
     * Used to add OnClickListeners to the Login and Register buttons mapped to their
     * respective Async Tasks to retrieve/send information to the database.
     *
     * @param savedInstanceState Stores data that was sent from the caller.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUserUsernameEditText = (EditText) findViewById(R.id.usernameEditText);
        mUserPasswordEditText = (EditText) findViewById(R.id.passwordEditText);

        Button userLoginButton = (Button) findViewById(R.id.loginButton);
        userLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = buildURL(v, LOGIN_URL);
                login(url);
            }
        });

        Button userRegisButton = (Button) findViewById(R.id.registerButton);
        userRegisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = buildURL(v, REGIS_URL);
                register(url);
            }
        });
    }

    /**
     * Method called when the Submit button is pressed.
     *
     * Initiates the AsyncTask that checks if the username and password entered
     * by the user is in our database, logs in if it exists.
     *
     * @param url a String containing a url query for our database
     */
    public void login(String url) {
        DownloadUsersTask task = new DownloadUsersTask();
        task.execute(new String[]{url.toString()});
    }

    /**
     * Method called when the Register button is pressed.
     *
     * Initiates the AsyncTask that checks if the username and password entered
     * by the user exists in our database, creates new entry if it doesn't.
     *
     * @param url a String containing a url query for our database
     */
    public void register(String url) {
        RegisUserTask task = new RegisUserTask();
        task.execute(new String[]{url.toString()});
    }

    /**
     * Method used to build a string used to query our database.
     *
     * This method concatenates and encodes the user entered information with the stored
     * base URL into a string.
     * @param v the current app view, used to display a Toast
     * @param url String url containing the base URL
     * @return the concatenated String to be used to query the database.
     */
    private String buildURL(View v, String url) {

        StringBuilder sb = new StringBuilder(url);

        try {

            String userUsername = mUserUsernameEditText.getText().toString();
            sb.append("userName=");
            sb.append(userUsername);


            String userPassword = mUserPasswordEditText.getText().toString();
            sb.append("&passWord=");
            sb.append(URLEncoder.encode(userPassword, "UTF-8"));


            Log.i("buildURL", sb.toString());

        }
        catch(Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }

    /**
     * This inner class is used to connect to the database and check if the user entered
     * username and password exist in our database, if they exist they're logged in
     * if they don't exist, the login fails.
     */
    private class DownloadUsersTask extends AsyncTask<String, Void, String> {

        /**
         * Accesses the server via the passed URL and performs the query
         * if there is an issue, throws exception, if not passes on to onPostExecute.
         *
         * @param urls string URLs used to query the database
         * @return String response from server
         */
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to download users, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        /**
         * Method to respond after doInBackground.
         *
         * If server returns success then Jokes List is displayed and number of pages is
         * calculated.
         *
         * If server returns anything else then toast displays what error the server returned.
         *
         * @param result String response from doInBackground containing the server's response
         */
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "User logged in!"
                            , Toast.LENGTH_LONG)
                            .show();

                    double totalNumOfJokes = Double.parseDouble((String)jsonObject.get("numJokes"));
                    int numPagesOfJokes = (int) Math.ceil(totalNumOfJokes/NUM_JOKES_PER_PAGE);

                    Intent intent = new Intent(getApplicationContext(), JokesPage.class);
                    intent.putExtra("numPages", numPagesOfJokes);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to login: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }

    /**
     * This inner class is used to connect to the database and check if the user entered
     * username and password exist in our database, if they don't exist they're registered
     * if they do exist, the registration fails.
     */
    private class RegisUserTask extends AsyncTask<String, Void, String> {

        /**
         * Accesses the server via the passed URL and performs the query
         * if there is an issue, throws exception, if not passes on to onPostExecute.
         *
         * @param urls string URLs used to query the database
         * @return String response from server
         */
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    System.out.println("register doinbackground");
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to add user, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }


        /**
         * Method to respond after doInBackground.
         *
         * If server returns success then user is registered and they can now sign in.
         *
         * If server returns anything else then toast displays what error the server returned.
         *
         * @param result String response from doInBackground containing the server's response
         */
        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "User successfully added!"
                            , Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to add: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

}