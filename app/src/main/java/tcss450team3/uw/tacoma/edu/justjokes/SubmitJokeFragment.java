package tcss450team3.uw.tacoma.edu.justjokes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * A simple {@link Fragment} subclass.
 * Use the {@link SubmitJokeFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author Vlad
 * @author Kyle
 *
 * 3/1/17
 */
public class SubmitJokeFragment extends Fragment {
    private static final String JOKE_SUB_URL
            = "http://cssgate.insttech.washington.edu/~_450bteam3/submitJoke.php?";

    private EditText mJokeTitleEdit;
    private EditText mJokeSetupEdit;
    private EditText mJokePunchlineEdit;

    private String mUsername;

    public SubmitJokeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SubmitJokeFragment.
     */
    public static SubmitJokeFragment newInstance(String param1, String param2) {
        return new SubmitJokeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUsername = getArguments().getString("username");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_submit_joke, container, false);

        mJokeTitleEdit = (EditText) v.findViewById(R.id.sub_joke_title_field);
        mJokeSetupEdit = (EditText) v.findViewById(R.id.sub_joke_setup_field);
        mJokePunchlineEdit = (EditText) v.findViewById(R.id.sub_joke_punchline_field);

        Button addCourseButton = (Button) v.findViewById(R.id.sub_joke_button);
        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitJoke(buildEmailMessage(v), v);
            }
        });

        Button clearButton = (Button) v.findViewById(R.id.clear_fields_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mJokeTitleEdit.setText("");
                mJokeSetupEdit.setText("");
                mJokePunchlineEdit.setText("");
            }
        });

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private String buildEmailMessage(View v) {
        StringBuilder sb = new StringBuilder("Joke Title: ");

        try {
            String jokeTitle = mJokeTitleEdit.getText().toString();
            sb.append(jokeTitle);

            String jokeSetup = mJokeSetupEdit.getText().toString();
            sb.append("\nJoke Setup: ");
            sb.append(jokeSetup);

            String jokePunchline = mJokePunchlineEdit.getText().toString();
            sb.append("\nJoke Punchline: ");
            sb.append(jokePunchline);

            sb.append("\nSubmitted by: ");
            sb.append(mUsername);
        }
        catch(Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }

    public void submitJoke(String emailMessage, View v) {
        new SubmitJokeTask().execute(buildJokeURL(v));
    }

    private String buildJokeURL(View v) {

        StringBuilder sb = new StringBuilder(JOKE_SUB_URL);

        try {
            String courseId = mJokeTitleEdit.getText().toString();
            sb.append("jokeTitle=");
            sb.append(courseId);


            String courseShortDesc = mJokeSetupEdit.getText().toString();
            sb.append("&jokeSetup=");
            sb.append(URLEncoder.encode(courseShortDesc, "UTF-8"));


            String courseLongDesc = mJokePunchlineEdit.getText().toString();
            sb.append("&jokePunchline=");
            sb.append(URLEncoder.encode(courseLongDesc, "UTF-8"));

            sb.append("&user=");
            sb.append(URLEncoder.encode(mUsername, "UTF-8"));

            Log.i("SubmitJokeURL", sb.toString());

        }
        catch(Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }

    private class SubmitJokeTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    System.out.println("SubmitJokeTask trying");
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    System.out.println("SubmitJokeTask failed");
                    response = "Unable to submit joke, Reason: "
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
                    mJokeTitleEdit.setText("");
                    mJokeSetupEdit.setText("");
                    mJokePunchlineEdit.setText("");

                    Toast.makeText(getActivity().getApplicationContext(), "Joke successfully submitted for review!"
                            , Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Failed to add: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getActivity().getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
