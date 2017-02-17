package tcss450team3.uw.tacoma.edu.justjokes;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import tcss450team3.uw.tacoma.edu.justjokes.joke.Joke;

import static android.R.attr.button;


/**
 * A Custom Dialog Fragment that is used to display a joke's setup and punchline, it allows the user
 * show/hide the joke's punchline.
 *
 * @author Vlad (2.15.17)
 */
public class CustomJokeDialogFragment extends DialogFragment {
    public final static String COURSE_ITEM_SELECTED = "course_selected";

    /** A TextView variable that allows us to modify the setup TextView seen by the user. */
    private TextView mJokeSetupTextView;

    /** A TextView variable that allows us to modify the punchline TextView seen by the user. */
    private TextView mJokePunchlineTextView;

    /** The Button that hides/shows a joke's punchline. */
    private Button mShowButton;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CustomJokeDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * Auto-generated method, not modified by us.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomJokeDialogFragment.
     */
    public static CustomJokeDialogFragment newInstance(String param1, String param2) {
        CustomJokeDialogFragment fragment = new CustomJokeDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Auto-generated method, not modified by us.
     *
     * @param savedInstanceState Stores data that was sent from the caller.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * This method is used to setup how the dialog popup will look and behave.
     *
     * @param savedInstanceState Stores data that was sent from the caller.
     * @return The Dialog to display.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view =  inflater.inflate(R.layout.fragment_custom_joke_dialog, null);
        mJokeSetupTextView = (TextView) view.findViewById(R.id.setupText);
        mJokePunchlineTextView = (TextView) view.findViewById(R.id.punchlineText);

        Bundle args = getArguments();
        if (args != null) {
            // Set article based on argument passed in
            updateView((Joke) args.getSerializable(COURSE_ITEM_SELECTED));
        }
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view);
        mShowButton = (Button) view.findViewById(R.id.showButton);

        //This inner method provides the functionality for the show/hide punchline button.
        mShowButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mJokePunchlineTextView.getVisibility() == View.INVISIBLE) {
                    mJokePunchlineTextView.setVisibility(view.VISIBLE);
                    mShowButton.setText("Hide Punchline");
                } else {
                    mJokePunchlineTextView.setVisibility(view.INVISIBLE);
                    mShowButton.setText("Show Punchline");
                }
            }
        });
        return builder.create();
    }

    /**
     * This method updates the proper text views to display the joke's setup and punchline.
     *
     * @param joke A Joke object to retrieve data from.
     */
    public void updateView(Joke joke) {
        if (joke != null) {
            mJokeSetupTextView.setText(joke.getJokeSetup());
            mJokePunchlineTextView.setText(joke.getJokePunchline());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
