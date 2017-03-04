package tcss450team3.uw.tacoma.edu.justjokes;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import tcss450team3.uw.tacoma.edu.justjokes.JokeFragment.OnListFragmentInteractionListener;
import tcss450team3.uw.tacoma.edu.justjokes.joke.Joke;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class assists the JokeFragment in displaying a smooth, scrolling list of jokes.
 *
 * @author Vlad (2.15.17)
 */
public class MyJokeRecyclerViewAdapter extends RecyclerView.Adapter<MyJokeRecyclerViewAdapter.ViewHolder> {

    private final List<Joke> mValues;
    private final OnListFragmentInteractionListener mListener;
    private boolean mNumbered;
    private Map<Integer, Joke> mFavorites;
    private Set<Integer> mUpvotes;
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
            holder.mIdView.setText((position + 1) + ". " + mValues.get(position).getJokeTitle());
        else
            holder.mIdView.setText(mValues.get(position).getJokeTitle());

        if (mFavorites.keySet().contains(holder.mItem.getJokeID()))
            holder.mFavoriteBox.setVisibility(View.VISIBLE);
        else
            holder.mFavoriteBox.setVisibility(View.GONE);
        if (mUpvotes.contains(holder.mItem.getJokeID())) {
            holder.mVoteBox.setBackgroundColor(Color.parseColor("#1ABDD4"));
            holder.mVoteBox.setVisibility(View.VISIBLE);
        } else if (mDownvotes.contains(holder.mItem.getJokeID())) {
            holder.mVoteBox.setBackgroundColor(Color.parseColor("#FFAE00"));
            holder.mVoteBox.setVisibility(View.VISIBLE);
        } else
            holder.mVoteBox.setVisibility(View.GONE);
        //holder.mContentView.setText(mValues.get(position).getJokeTitle());

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

    public void checkFavorites() {
        Iterator<Joke> iter = mValues.iterator();
        while (iter.hasNext()) {
            int currJokeID = iter.next().getJokeID();
            if (!mFavorites.keySet().contains(currJokeID)) {
                iter.remove();
            }
        }
    }

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
        public final View mView;
        public final TextView mIdView;
        public final View mVoteBox;
        public final View mFavoriteBox;
        public Joke mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mVoteBox = view.findViewById(R.id.voteBox);
            mFavoriteBox = view.findViewById(R.id.favoriteBox);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mIdView.getText() + "'";
        }
    }
}
