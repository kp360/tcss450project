package tcss450team3.uw.tacoma.edu.justjokes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void login(View view) {
        openJokesPage(view);
    }

    public void openJokesPage(View view) {
        Intent intent = new Intent(this, JokesPage.class);
        startActivity(intent);
    }
}
