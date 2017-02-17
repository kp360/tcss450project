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

import org.json.JSONArray;
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
 */
public class MainActivity extends AppCompatActivity {

    private static final String LOGIN_URL
            = "http://cssgate.insttech.washington.edu/~_450bteam3/login.php?";
    private static final String REGIS_URL
            = "http://cssgate.insttech.washington.edu/~_450bteam3/adduser.php?";
    private static final int NUM_JOKES_PER_PAGE = 20;

    private EditText mUserUsernameEditText;
    private EditText mUserPasswordEditText;

    private String USERNAME = "userName";
    private String PASSWORD = "passWord";

    private boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserUsernameEditText = (EditText) findViewById(R.id.usernameEditText);
        mUserPasswordEditText = (EditText) findViewById(R.id.passwordEditText);

        Button userLoginButton = (Button) findViewById(R.id.loginButton);
        userLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String url = buildLoginURL(v);
                String url = buildURL(v, LOGIN_URL);
                login(url);
            }
        });

        Button userRegisButton = (Button) findViewById(R.id.registerButton);
        userRegisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String url = buildRegisURL(v);
                String url = buildURL(v, REGIS_URL);
                register(url);
            }
        });
    }

    public void login(String url) {

        DownloadUsersTask task = new DownloadUsersTask();
        task.execute(new String[]{url.toString()});
    }

    public void register(String url) {
        RegisUserTask task = new RegisUserTask();
        task.execute(new String[]{url.toString()});
    }

    public void openJokesPage(View view) {
        Intent intent = new Intent(this, JokesPage.class);
        startActivity(intent);
    }

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

    private class DownloadUsersTask extends AsyncTask<String, Void, String> {

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
                    response = "Unable to download the list of users, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

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

    private class RegisUserTask extends AsyncTask<String, Void, String> {

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
         * It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. It tries to call the parse Method and checks to see if it was successful.
         * If not, it displays the exception.
         *
         * @param result
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
