package tcss450team3.uw.tacoma.edu.justjokes;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tcss450team3.uw.tacoma.edu.justjokes.joke.Joke;

import static org.junit.Assert.*;

/**
 * This class is intended to test methods in Joke.class for proper functionality
 * and to cover all possible cases in those methods.
 *
 * Created by Kyle on 3/5/2017.
 */

public class JokeTest {

    /**
     * This method tests the Joke constructor with valid input.
     * Should result in a non-null Joke object.
     */
    @Test
    public void testJokeConstructorValidInput() {
        assertNotNull(new Joke(1, "Test Joke", "What did the Test say to the Joke?",
                "You better work", 2, 10));
    }

    /**
     * This method tests the Joke constructor with valid input and checks
     * if the fields are set properly via the getter methods.
     */
    @Test
    public void testJokeConstructorCheckFieldsCorrect() {
        Joke testJoke = new Joke(1, "Test Joke", "What did the Test say to the Joke?",
                "You better work", 2, 4);
        assertEquals(testJoke.getJokeID(),1);
        assertEquals(testJoke.getJokeTitle(), "Test Joke");
        assertEquals(testJoke.getJokeSetup(), "What did the Test say to the Joke?");
        assertEquals(testJoke.getJokePunchline(), "You better work");
        assertEquals(testJoke.getmNumUpvotes(), 2);
        assertEquals(testJoke.getmNumDownvotes(), 4);
    }

    /**
     * This method tests the JSON parsing method.
     * Passes in a complete JSON string and arraylist, if Jokes are parsed
     * then method returns null.
     */
    @Test
    public void testParseCourseJSONJokeListParseValidString() {
        List<Joke> jokeList = new ArrayList<Joke>();
        Joke testJoke = new Joke(1, "Test Joke", "What did the Test say to the Joke?",
                "You better work", 2, 10);
        String testJSON = "[{\"jokeID\":\"4\",\"numUVotes\":\"46\"," +
                            "\"numDVotes\":\"2\",\"jokeTitle\":\"Holy Water Joke\"," +
                            "\"jokeSetup\":\"How do you make holy water?\",\"jokePunchline\":" +
                            "\"Boil the hell out of it!\"}]";
        assertNull(testJoke.parseCourseJSON(testJSON, jokeList));
    }
    /**
     * This method tests the JSON parsing method.
     * Passes in a complete JSON string and Hashmap, if Jokes are parsed
     * then method returns null.
     */
    @Test
    public void testParseCourseJSONFavoritesParseValidString() {
        Map<Integer, Joke> jokeList = new HashMap<Integer, Joke>();
        Joke testJoke = new Joke(1, "Test Joke", "What did the Test say to the Joke?",
                "You better work", 2, 10);
        String testJSON = "[{\"jokeID\":\"4\",\"numUVotes\":\"46\"," +
                "\"numDVotes\":\"2\",\"jokeTitle\":\"Holy Water Joke\"," +
                "\"jokeSetup\":\"How do you make holy water?\",\"jokePunchline\":" +
                "\"Boil the hell out of it!\"}]";
        assertNull(testJoke.parseCourseJSON(testJSON, jokeList));
    }

    /**
     * Tests if the number of upvotes is incremented.
     */
    @Test
    public void testIncrementNumUpvotes() {
        Joke testJoke = new Joke(1, "Test Joke", "What did the Test say to the Joke?",
                "You better work", 1, 1);
        testJoke.incrementNumUpvotes();
        assertEquals(testJoke.getmNumUpvotes(), 2);
    }

    /**
     * Tests if the number of upvotes is decremented,
     * upvotes is more than 0.
     */
    @Test
    public void testDecrementNumUpvotesAboveZero() {
        Joke testJoke = new Joke(1, "Test Joke", "What did the Test say to the Joke?",
                "You better work", 1, 1);
        testJoke.decrementNumUpvotes();
        assertEquals(testJoke.getmNumUpvotes(), 0);
    }

    /**
     * Tests if the number of upvotes is decremented,
     * if number of upvotes is zero, the number of upvotes remains zero.
     */
    @Test
    public void testDecrementNumUpvotesAtZero() {
        Joke testJoke = new Joke(1, "Test Joke", "What did the Test say to the Joke?",
                "You better work", 0, 1);
        testJoke.decrementNumUpvotes();
        assertEquals(testJoke.getmNumUpvotes(), 0);
    }

    /**
     * Tests if the number of upvotes is decremented,
     * if number of upvotes is below zero, the number of upvotes is set to zero.
     */
    @Test
    public void testDecrementNumUpvotesBelowZero() {
        Joke testJoke = new Joke(1, "Test Joke", "What did the Test say to the Joke?",
                "You better work", -1, 1);
        testJoke.decrementNumUpvotes();
        assertEquals(testJoke.getmNumUpvotes(), 0);
    }
    /**
     * Tests if the number of downvotes is incremented.
     */
    @Test
    public void testIncrementNumDownvotes() {
        Joke testJoke = new Joke(1, "Test Joke", "What did the Test say to the Joke?",
                "You better work", 1, 1);
        testJoke.incrementNumDownvotes();
        assertEquals(testJoke.getmNumDownvotes(), 2);
    }
    /**
     * Tests if the number of downvotes is decremented,
     * downvotes is more than 0.
     */
    @Test
    public void testDecrementNumDownvotesAboveZero() {
        Joke testJoke = new Joke(1, "Test Joke", "What did the Test say to the Joke?",
                "You better work", 1, 1);
        testJoke.decrementNumDownvotes();
        assertEquals(testJoke.getmNumDownvotes(), 0);
    }
    /**
     * Tests if the number of downvotes is decremented,
     * if number of downvotes is zero, the number of downvotes remains zero.
     */
    @Test
    public void testDecrementNumDownvotesAtZero() {
        Joke testJoke = new Joke(1, "Test Joke", "What did the Test say to the Joke?",
                "You better work", 0, 0);
        testJoke.decrementNumDownvotes();
        assertEquals(testJoke.getmNumDownvotes(), 0);
    }
    /**
     * Tests if the number of downvotes is decremented,
     * if number of downvotes is below zero, the number of downvotes is set to zero.
     */
    @Test
    public void testDecrementNumDownvotesBelowZero() {
        Joke testJoke = new Joke(1, "Test Joke", "What did the Test say to the Joke?",
                "You better work", 0, -1);
        testJoke.decrementNumDownvotes();
        assertEquals(testJoke.getmNumDownvotes(), 0);
    }

    /**
     * Tests compare to method,
     * if both objects compared are the same or have the same name,
     * result is 0.
     */
    @Test
    public void testCompareToSameJoke() {
        Joke testJoke = new Joke(1, "Test Joke", "What did the Test say to the Joke?",
                "You better work", 0, 0);
        assertEquals(testJoke.compareTo(testJoke), 0);
    }

    /**
     * Tests compare to method,
     * if this joke has a greater name than the other joke,
     * result is positive.
     */
    @Test
    public void testCompareToSameVotesGreaterName() {
        Joke testJoke = new Joke(1, "Test Joke", "What did the Test say to the Joke?",
                "You better work", 0, 0);
        Joke testJoke2 = new Joke(1, "Test Joke2", "What did the Test say to the Joke?",
                "You better work", 0, 0);
        assertEquals(testJoke.compareTo(testJoke2), 1);
    }

    /**
     * Tests compare to method,
     * if this joke has a lesser name than the other joke,
     * result is negative
     */
    @Test
    public void testCompareToSameVotesLesserName() {
        Joke testJoke = new Joke(1, "Test Joke1", "What did the Test say to the Joke?",
                "You better work", 0, 0);
        Joke testJoke2 = new Joke(1, "Test Joke", "What did the Test say to the Joke?",
                "You better work", 0, 0);
        assertEquals(testJoke.compareTo(testJoke2), -1);
    }

    /**
     * Tests compare to method,
     * if this joke has a greater upvote count,
     * result is positive.
     */
    @Test
    public void testCompareToGreater() {
        Joke testJoke = new Joke(1, "Test Joke", "What did the Test say to the Joke?",
                "You better work", 5, 0);
        Joke testJoke2 = new Joke(1, "Test Joke", "What did the Test say to the Joke?",
                "You better work", 3, 0);
        assertEquals(testJoke.compareTo(testJoke2), 2);
    }

    /**
     * Tests compare to method,
     * if this joke has a lower upvote count,
     * result is negative.
     */
    @Test
    public void testCompareToLess() {
        Joke testJoke = new Joke(1, "Test Joke", "What did the Test say to the Joke?",
                "You better work", 3, 0);
        Joke testJoke2 = new Joke(1, "Test Joke", "What did the Test say to the Joke?",
                "You better work", 5, 0);
        assertEquals(testJoke.compareTo(testJoke2), -2);
    }

    /**
     * Tests compare to method,
     * if this joke has a higher downvote count and the same upvotes,
     * result is negative.
     */
    @Test
    public void testCompareToMoreDownvotes() {
        Joke testJoke = new Joke(1, "Test Joke", "What did the Test say to the Joke?",
                "You better work", 5, 3);
        Joke testJoke2 = new Joke(1, "Test Joke", "What did the Test say to the Joke?",
                "You better work", 5, 2);
        assertEquals(testJoke.compareTo(testJoke2), -1);
    }

    /**
     * Tests compare to method,
     * if this joke has a lower downvote count and the same upvotes,
     * result is positive.
     */
    @Test
    public void testCompareToLessDownvotes() {
        Joke testJoke = new Joke(1, "Test Joke", "What did the Test say to the Joke?",
                "You better work", 5, 2);
        Joke testJoke2 = new Joke(1, "Test Joke", "What did the Test say to the Joke?",
                "You better work", 5, 3);
        assertEquals(testJoke.compareTo(testJoke2), 1);
    }
}
