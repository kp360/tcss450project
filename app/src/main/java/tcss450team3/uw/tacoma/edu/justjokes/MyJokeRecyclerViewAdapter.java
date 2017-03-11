package tcss450team3.uw.tacoma.edu.justjokes;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tcss450team3.uw.tacoma.edu.justjokes.JokeFragment.OnListFragmentInteractionListener;
import tcss450team3.uw.tacoma.edu.justjokes.joke.Joke;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class assists the JokeFragment in displaying a smooth, scrolling list of jokes.
 *
 * @author Vlad 3/4/2017
 */
public class MyJokeRecyclerViewAdapter extends RecyclerView.Adapter<MyJokeRecyclerViewAdapter.ViewHolder> {

    /** A list of Joke objects to display in the list fragment. */
    private final List<Joke> mValues;

    /** Listener to detect interactions with the list items. */
    private final OnListFragmentInteractionListener mListener;

    /** A boolean variable that tells us whether or not to number the elements in our list (only
     * necessary for the high scores tab). */
    private boolean mNumbered;

    /** A Map of the user's favorite jokes, jokeIds are mapped to the Joke objects, this was done
     * to ensure quick searches. */
    private Map<Integer, Joke> mFavorites;

    /** A Set of JokeIds, of the Jokes that the user has upvoted. */
    private Set<Integer> mUpvotes;

    /** A Set of JokeIds, of the Jokes that the user has downvoted. */
    private Set<Integer> mDownvotes;

    /**
     * Auto-generated method, not modified by us.
     *
     * @param items Items to display in the list.
     * @param listener An OnListFragmentInteractionListener object.
     */
    public MyJokeRecyclerViewAdapter(List<Joke> items, OnListFragmentInteractionListener listener, boolean numbered, Bundle args) {
        mValues = items;
        mListener = listener;
        mNumbered = numbered;
        mFavorites = (HashMap<Integer, Joke>) args.getSerializable("favorites");
        mUpvotes = (Set<Integer>) args.getSerializable("upvotes");
        mDownvotes = (Set<Integer>) args.getSerializable("downvotes");
    }

    /**
     * Auto-generated method, not modified by us.
     *
     * @param parent A ViewGroup object.
     * @param viewType An integer to specify the view type.
     * @return Returns a ViewHolder object.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_joke, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Auto-generated method, slightly modified by us.
     *
     * @param holder A ViewHolder object.
     * @param position An integer to specify the item's position.
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        if (mNumbered)
            holder.mJokeTitleView.setText((position + 1) + ". " + mValues.get(position).getJokeTitle());
        else
            holder.mJokeTitleView.setText(mValues.get(position).getJokeTitle());

        if (mFavorites != null && mFavorites.keySet().contains(holder.mItem.getJokeID()))
            holder.mFavoriteBox.setVisibility(View.VISIBLE);
        else
            holder.mFavoriteBox.setVisibility(View.GONE);
        if (mUpvotes != null && mUpvotes.contains(holder.mItem.getJokeID())) {
            holder.mVoteBox.setBackgroundColor(Color.parseColor("#1ABDD4"));
            holder.mVoteBox.setVisibility(View.VISIBLE);
        } else if (mDownvotes != null && mDownvotes.contains(holder.mItem.getJokeID())) {
            holder.mVoteBox.setBackgroundColor(Color.parseColor("#FFAE00"));
            holder.mVoteBox.setVisibility(View.VISIBLE);
        } else
            holder.mVoteBox.setVisibility(View.GONE);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    /**
     * Clears the favorites list, and gets an updated copy of the user's favorite jokes. Also sorts
     * them alphabetically.
     */
    public void checkFavorites() {
        mValues.clear();
        for (Joke currentJoke: mFavorites.values()) {
            mValues.add(currentJoke);
        }
        Collections.sort(mValues, new Comparator<Joke>() {
            @Override
            public int compare(Joke o1, Joke o2) {
                return o1.getJokeTitle().compareTo(o2.getJokeTitle());
            }
        });
    }

    /**
     * Sorts the list values, to ensure that they are in the correct order.
     */
    public void checkHighScores() {
        Collections.sort(mValues, Collections.<Joke>reverseOrder());
    }

    /**
     * Returns how many items are in the mValues list.
     *
     * @return Returns the amount of items in the mValues list.
     */
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * Auto-generated method, slightly modified by us.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        /** The View object that our elements lie in. */
        public final View mView;

        /** The TextView that displays the Joke's title. */
        public final TextView mJokeTitleView;

        /** The View that displays a blue or orange box to denote up/downvoting. */
        public final View mVoteBox;

        /** The View that displays a pinkish box to denote that a joke has been favorited. */
        public final View mFavoriteBox;

        /** The Joke object that we are listing. */
        public Joke mItem;

        /**
         * Constructor to initialize all the fields.
         * @param view The View object that our elements lie in.
         */
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mJokeTitleView = (TextView) view.findViewById(R.id.id);
            mVoteBox = view.findViewById(R.id.voteBox);
            mFavoriteBox = view.findViewById(R.id.favoriteBox);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mJokeTitleView.getText() + "'";
        }
    }
}
