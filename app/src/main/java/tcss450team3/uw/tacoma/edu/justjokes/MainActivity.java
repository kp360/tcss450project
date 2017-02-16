package tcss450team3.uw.tacoma.edu.justjokes;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {

    private static final String COURSE_URL
            = "http://cssgate.insttech.washington.edu/~_450bteam3/list.php?cmd=users";

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

//        DownloadUsersTask task = new DownloadUsersTask();
//        task.execute(new String[]{COURSE_URL});

    }

    public void login(View view) {

        DownloadUsersTask task = new DownloadUsersTask();
        task.execute(new String[]{COURSE_URL});
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
}
