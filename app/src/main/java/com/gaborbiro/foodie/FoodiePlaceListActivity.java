package com.gaborbiro.foodie;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;

import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Place;

/**
 * An activity representing a list of FoodiePlaces. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link FoodiePlaceDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class FoodiePlaceListActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    private static final String GOOGLE_PLACES_API_KEY =
            "AIzaSyBDwqC3Emv86CJOYxhfC_LYps9caIfucEs";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private RecyclerView mList;

    private GooglePlaces mGooglePlaces;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodieplace_list);

        mGooglePlaces = new GooglePlaces(GOOGLE_PLACES_API_KEY);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        assert toolbar != null;
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                new PlacesTask().execute();
            }
        });

        mList = (RecyclerView) findViewById(R.id.foodieplace_list);
        assert mList != null;

        if (findViewById(R.id.foodieplace_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(FoodiePlaceListActivity.this, connectionResult.getErrorMessage(),
                Toast.LENGTH_SHORT)
                .show();
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Place> mValues;

        public SimpleItemRecyclerViewAdapter(List<Place> items) {
            mValues = items;
        }

        @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.foodieplace_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override public void onBindViewHolder(final ViewHolder holder, int position) {
            Place place = mValues.get(position);
            holder.mItem = place;
            holder.mIdView.setText(place.getName());
            holder.mContentView.setText(place.getAddress());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
//                    if (mTwoPane) {
//                        Bundle arguments = new Bundle();
//                        arguments.putString(FoodiePlaceDetailFragment.ARG_ITEM_ID,
//                                holder.mItem.id);
//                        FoodiePlaceDetailFragment fragment =
//                                new FoodiePlaceDetailFragment();
//                        fragment.setArguments(arguments);
//                        getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.foodieplace_detail_container, fragment)
//                                .commit();
//                    } else {
//                        Context context = v.getContext();
//                        Intent intent =
//                                new Intent(context, FoodiePlaceDetailActivity.class);
//                        intent.putExtra(FoodiePlaceDetailFragment.ARG_ITEM_ID,
//                                holder.mItem.id);
//
//                        context.startActivity(intent);
//                    }
                }
            });
        }

        @Override public int getItemCount() {
            return mValues != null ? mValues.size() : 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public Place mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    private class PlacesTask extends AsyncTask<Void, Void, List<Place>> {

        @Override protected List<Place> doInBackground(Void... params) {
            return mGooglePlaces.getNearbyPlaces(51.5223982, -0.1654882, 1610,
                    GooglePlaces.MAXIMUM_RESULTS);
        }

        @Override protected void onPostExecute(List<Place> places) {
            mList.setAdapter(new SimpleItemRecyclerViewAdapter(places));
        }
    }
}
