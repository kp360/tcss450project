package tcss450team3.uw.tacoma.edu.justjokes;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

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
 * This class is designed to run espresso UI tests on our login form.
 * Checks if user login validation works properly as well as user
 * registration validation and "Remember Me" SharedPreferences.
 *
 * Tests are designed to run in the order listed in this class.
 *
 * Created by Kyle on 3/5/2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginTest {

    /**
     * A JUnit {@link Rule @Rule} to launch your activity under test.
     * Rules are interceptors which are executed for each test method and will run before
     * any of your setup code in the {@link @Before} method.
     * <p>
     * {@link ActivityTestRule} will create and launch of the activity for you and also expose
     * the activity under test. To get a reference to the activity you can use
     * the {@link ActivityTestRule#getActivity()} method.
     */
    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);

    /** Method tests user login with a valid username but incorrect password. */
    @Test
    public void testLoginInvalidPasswordIncorrect() {
        // Type text and then press the button.
        onView(withId(R.id.usernameEditText))
                .perform(typeText("RememberMeTestAccount"));
        onView(withId(R.id.passwordEditText))
                .perform(typeText("123456789"));
        onView(withId(R.id.loginButton))
                .perform(click());

        onView(withText("Failed to login: Incorrect password."))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    /** Method tests user login with a valid username but password that is too short. */
    @Test
    public void testLoginInvalidPasswordTooShort() {
        try{
            Thread.sleep(2500);
        } catch (InterruptedException e){

        }
        // Type text and then press the button.
        onView(withId(R.id.usernameEditText))
                .perform(typeText("RememberMeTestAccount"));
        onView(withId(R.id.passwordEditText))
                .perform(typeText(""));
        onView(withId(R.id.loginButton))
                .perform(click());

        onView(withText("Failed to login: Please enter a valid password (longer than five characters)."))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
    /** Method tests user login with an invalid username but a valid password. */
    @Test
    public void testLoginInvalidUsername() {
        try{
            Thread.sleep(2500);
        } catch (InterruptedException e){

        }
        // Type text and then press the button.
        onView(withId(R.id.usernameEditText))
                .perform(typeText(""));
        onView(withId(R.id.passwordEditText))
                .perform(typeText("test1@#"));
        onView(withId(R.id.loginButton))
                .perform(click());

        onView(withText("Failed to login: Incorrect username."))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    /**
     * Method tests the remember me function, user signs in and checks "Remember Me",
     * then signs in, data is stored in SharedPreferences.
     */

    @Test
    public void testLoginRememberMeCheck() {
        try{
            Thread.sleep(2500);
        } catch (InterruptedException e){

        }
        // Type text and then press the button.
        onView(withId(R.id.usernameEditText))
                .perform(typeText("RememberMeTestAccount"));
        onView(withId(R.id.passwordEditText))
                .perform(typeText("test1@#"));
        onView(withId(R.id.saveLoginCheckBox))
                .perform(click());
        onView(withId(R.id.loginButton))
                .perform(click());

        onView(withText("Welcome back, RememberMeTestAccount!"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    /** Method tests the rememeber me function, signs in without typing info.*/
    @Test
    public void testLoginRememberMeLogin() {
        try{
            Thread.sleep(2500);
        } catch (InterruptedException e){

        }
        // Type text and then press the button.
        onView(withId(R.id.loginButton))
                .perform(click());

        onView(withText("Welcome back, RememberMeTestAccount!"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    /**
     * Method tests the remember me function, unchecks remember me and signs in,
     * data is removed from SharedPreferences.
     */

    @Test
    public void testLoginRememberMeUncheck() {
        try{
            Thread.sleep(2500);
        } catch (InterruptedException e){

        }
        // Type text and then press the button.
        onView(withId(R.id.saveLoginCheckBox))
                .perform(click());
        onView(withId(R.id.loginButton))
                .perform(click());

        onView(withText("Welcome back, RememberMeTestAccount!"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
    /** Method tests user login with valid username and password. */
    @Test
    public void testLoginValidInput() {
        try{
            Thread.sleep(2500);
        } catch (InterruptedException e){

        }
        // Type text and then press the button.
        onView(withId(R.id.usernameEditText))
                .perform(typeText("RememberMeTestAccount"));
        onView(withId(R.id.passwordEditText))
                .perform(typeText("test1@#"));
        onView(withId(R.id.loginButton))
                .perform(click());

        onView(withText("Welcome back, RememberMeTestAccount!"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
    /** Method tests user registration with valid username and password that is too short. */
    @Test
    public void testRegisterInvalidPassword() {
        try{
            Thread.sleep(2500);
        } catch (InterruptedException e){

        }
        // Type text and then press the button.
        onView(withId(R.id.usernameEditText))
                .perform(typeText("AutomatedTestAccount"));
        onView(withId(R.id.passwordEditText))
                .perform(typeText("a"));
        onView(withId(R.id.registerButton))
                .perform(click());

        onView(withText("Failed to register: Please enter a valid password (longer than five characters)."))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
    /** Method tests user registration with a valid password but invalid username. */
    @Test
    public void testRegisterInvalidUsername() {
        try{
            Thread.sleep(2500);
        } catch (InterruptedException e){

        }
        // Type text and then press the button.
        onView(withId(R.id.usernameEditText))
                .perform(typeText(""));
        onView(withId(R.id.passwordEditText))
                .perform(typeText("test1@#"));
        onView(withId(R.id.registerButton))
                .perform(click());

        onView(withText("Failed to register: Please enter a valid username (longer than one character)."))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
    /** Method tests user registration with a valid username and password. */
    @Test
    public void testRegisterValidInput() {
        try{
            Thread.sleep(2500);
        } catch (InterruptedException e){

        }

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
}
