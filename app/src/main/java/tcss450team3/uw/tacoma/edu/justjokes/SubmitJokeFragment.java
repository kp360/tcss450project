package tcss450team3.uw.tacoma.edu.justjokes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"JustJokesReview@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Joke Submission: " + mJokeTitleEdit.getText().toString());
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailMessage);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
