package com.example.android.popularmovies;

/**
 * Created by JC on 2017/3/9.
 * This is the adapter for RecyclerView
 */

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.FavoriteMovieUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private ArrayList<FeedItem> feedItemList = new ArrayList<>();
    private final Context mContext;
    private final ListItemClickListener mOnClickListener;
    private FavoriteMovieUtils mFavoriteMovieUtils = new FavoriteMovieUtils();
    private Cursor mCursor;
    private int number;


    protected  MovieAdapter(Context context, ArrayList<FeedItem> feedItemList, int number, ListItemClickListener listener) {
        this.mContext = context;
        this.feedItemList = feedItemList;
        this.number = number;
        mOnClickListener = listener;

    }


    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutIdForListItem = R.layout.recycleview_item;

        boolean shouldAttachToParentImmediately = false;

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        lp.height = parent.getMeasuredHeight() / 2;
        view.setLayoutParams(lp);

        MovieAdapterViewHolder viewHolder = new MovieAdapterViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final MovieAdapterViewHolder movieAdapterViewHolder, final int position) {

        final FeedItem feedItem = feedItemList.get(position);

        final String imageUrl = "http://image.tmdb.org/t/p/w185/" +
                feedItem.getPoster_path();

        movieAdapterViewHolder.mMovieNameTextView.setText(String.valueOf(number));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if(mFavoriteMovieUtils.hasObject(this.mContext,String.valueOf(feedItem.getId()))){

                movieAdapterViewHolder.mFavoriteCheckButton.setChecked(true);

            }
        }

        if (!TextUtils.isEmpty(feedItem.getPoster_path())) {

            Picasso.with(mContext)
                    .load(imageUrl)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(movieAdapterViewHolder.mImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.v("Picasso", "fetch image success in first time.");
                        }

                        @Override
                        public void onError() {
                            //Try again online if cache failed
                            Log.v("Picasso", "Could not fetch image in first time...");
                            Picasso.with(mContext).load(imageUrl).networkPolicy(NetworkPolicy.NO_CACHE)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).error(R.drawable.error)
                                    .into(movieAdapterViewHolder.mImageView, new Callback() {

                                        @Override
                                        public void onSuccess() {
                                            Log.v("Picasso", "fetch image success in try again.");
                                        }

                                        @Override
                                        public void onError() {
                                            Log.v("Picasso", "Could not fetch image again...");
                                        }

                                    });
                        }
                    });

        }
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder {

        final ImageView mImageView;
        final TextView mMovieNameTextView;
        final CheckBox mFavoriteCheckButton;

        public MovieAdapterViewHolder(View view) {
            super(view);

            this.mImageView = (ImageView) view.findViewById(R.id.iv_Poster);
            this.mMovieNameTextView = (TextView) view.findViewById(R.id.tv_movieNameInMainPage);
            this.mFavoriteCheckButton = (CheckBox) view.findViewById(R.id.favorite_checkBoxInMainPage);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickedPosition = getAdapterPosition();
                    mOnClickListener.onClick(clickedPosition);
                }
            });
        }
    }

    public interface ListItemClickListener {
        void onClick(int clickedItemIndex);
    }

    public void setMovieData(ArrayList<FeedItem> feedItems) {
        this.feedItemList = feedItems;
        notifyDataSetChanged();
    }
}


