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

public class MainActivity extends AppCompatActivity {

    private static final String LOGIN_URL
            = "http://cssgate.insttech.washington.edu/~_450bteam3/login.php?";
    private static final String REGIS_URL
            = "http://cssgate.insttech.washington.edu/~_450bteam3/adduser.php?";

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
                DownloadUsersTask task = new DownloadUsersTask();
                task.execute(new String[]{LOGIN_URL});
            }
        });

        Button userRegisButton = (Button) findViewById(R.id.registerButton);
        userRegisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Registering...");
                String url = buildRegisURL(v);
                register(url);
            }
        });


    }

    public void login(View view) {

        DownloadUsersTask task = new DownloadUsersTask();
        task.execute(new String[]{LOGIN_URL});
    }

    public void register(String url) {
        RegisUserTask task = new RegisUserTask();
        task.execute(new String[]{url.toString()});
    }

    public void openJokesPage(View view) {
        Intent intent = new Intent(this, JokesPage.class);
        startActivity(intent);
    }

    /**
     * Parses the json string, returns an error message if unsuccessful.
     * Returns course list if success.
     * @param userJSON
     * @return reason or null if successful.
     */
    public boolean parseUserJSON(String userJSON) {
        String reason = null;
        boolean authorized = false;
        if (userJSON != null) {
            try {
                JSONArray arr = new JSONArray(userJSON);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);

                    if ( obj.getString(USERNAME).equals(mUserUsernameEditText.getText().toString()) &&
                            obj.getString(PASSWORD).equals(mUserPasswordEditText.getText().toString())) {
//                        Intent intent = new Intent(this, JokesPage.class);
//                        startActivity(intent);
                        authorized = true;
                        break;
                    }
                }
            } catch (JSONException e) {
                Log.d("userParser", "Unable to parse data, Reason: " + e.getMessage());
            }

        }
        return authorized;
    }

    private String buildRegisURL(View v) {

        StringBuilder sb = new StringBuilder(REGIS_URL);

        try {

            String userUsername = mUserUsernameEditText.getText().toString();
            sb.append("userName=");
            sb.append(userUsername);


            String userPassword = mUserPasswordEditText.getText().toString();
            sb.append("&passWord=");
            sb.append(URLEncoder.encode(userPassword, "UTF-8"));


            Log.i("userRegistration", sb.toString());

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
            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            boolean isAuthorized = parseUserJSON(result);
            if (isAuthorized) {
                Intent intent = new Intent(getApplicationContext(), JokesPage.class);
                startActivity(intent);
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
