package tcss450team3.uw.tacoma.edu.justjokes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import tcss450team3.uw.tacoma.edu.justjokes.joke.Joke;

public class JokesPage extends AppCompatActivity implements JokeFragment.OnListFragmentInteractionListener  {
    @Override
    public void onListFragmentInteraction(Joke course) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jokes_page);

        if (savedInstanceState == null || getSupportFragmentManager().findFragmentById(R.id.list) == null) {
            JokeFragment courseFragment = new JokeFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, courseFragment)
                    .commit();
        }
    }
}
