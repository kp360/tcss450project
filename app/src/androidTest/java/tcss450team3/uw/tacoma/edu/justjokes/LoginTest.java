package tcss450team3.uw.tacoma.edu.justjokes;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by Kyle on 3/5/2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);

    @Test
    public void testRegister() {

        Random random = new Random();
        //Generate an email address
        String account = "Account" + (random.nextInt(400) + 1)
                + (random.nextInt(900) + 1) + (random.nextInt(700) + 1)
                + (random.nextInt(400) + 1) + (random.nextInt(100) + 1);

        // Type text and then press the button.
        onView(withId(R.id.usernameEditText))
                .perform(typeText(account));
        onView(withId(R.id.passwordEditText))
                .perform(typeText("111111"));
        onView(withId(R.id.registerButton))
                .perform(click());

        onView(withText("User successfully registered!"))
                .inRoot(withDecorView(not(is(
                        mActivityRule.getActivity()
                                .getWindow()
                                .getDecorView()))))
                .check(matches(isDisplayed()));

    }

    @Test
    public void testRegisterInvalidUsername() {
        // Type text and then press the button.
        onView(withId(R.id.usernameEditText))
                .perform(typeText(""));
        onView(withId(R.id.passwordEditText))
                .perform(typeText("test1@#"));
        onView(withId(R.id.registerButton))
                .perform(click());

        onView(withText("Failed to register: Please enter a valid username (longer than one" +
                " character)."))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
    @Test
    public void testRegisterInvalidPassword() {
        // Type text and then press the button.
        onView(withId(R.id.usernameEditText))
                .perform(typeText("AutomatedTestAccount"));
        onView(withId(R.id.passwordEditText))
                .perform(typeText("a"));
        onView(withId(R.id.registerButton))
                .perform(click());

        onView(withText("Failed to register: Please enter a valid password (longer than 5" +
                " characters)."))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

}
