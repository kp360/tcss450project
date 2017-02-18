package tcss450team3.uw.tacoma.edu.justjokes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import tcss450team3.uw.tacoma.edu.justjokes.joke.Joke;

/**
 * This class provides the backend for the Activity where a list of jokes is displayed.
 *
 * @author Vlad (2.16.17)
 */

public class JokesPage extends AppCompatActivity implements JokeFragment.OnListFragmentInteractionListener  {
    /**
     * This method handles opening a CustomJokeDialogFragment when a joke in the list is tapped on.
     *
     * @param joke The Joke whose values (setup, punchline) will be displayed in the
     *             CustomJokeDialogFragment.
     */
    @Override
    public void onListFragmentInteraction(Joke joke) {
        CustomJokeDialogFragment jokeDetailFragment = new CustomJokeDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(jokeDetailFragment.COURSE_ITEM_SELECTED, joke);
        jokeDetailFragment.setArguments(args);

        jokeDetailFragment.show(getSupportFragmentManager(), "launch");
    }

    /**
     * This method is called when the JokesPage Activity is created. It opens a JokeFragment object,
     * and passes it the number of pages of jokes that are currently in our database.
     *
     * @param savedInstanceState Stores data that was sent from the caller.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jokes_page);

        if (savedInstanceState == null || getSupportFragmentManager().findFragmentById(R.id.list) == null) {
            JokeFragment courseFragment = new JokeFragment();
            Bundle args = new Bundle();
            int numPagesOfJokes = getIntent().getIntExtra("numPages", 0);
            args.putInt("numPages", numPagesOfJokes);
            courseFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, courseFragment)
                    .commit();
        }
    }
}
