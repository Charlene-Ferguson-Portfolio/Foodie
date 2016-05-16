package com.gaborbiro.foodie.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gaborbiro.foodie.R;
import com.gaborbiro.foodie.di.AppModule;
import com.gaborbiro.foodie.provider.places.model.Place;
import com.gaborbiro.foodie.ui.di.DaggerUIComponent;
import com.gaborbiro.foodie.ui.di.PlacesPresenterModule;
import com.gaborbiro.foodie.ui.model.PlacesModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * An activity representing a list of FoodiePlaces. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PlaceDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class PlaceListActivity extends AppCompatActivity {

    @Inject PlacesPresenter mPresenter;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private RecyclerView mList;
    @InjectView(android.R.id.empty) public TextView mEmptyView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        DaggerUIComponent.builder()
                .appModule(new AppModule(getApplication()))
                .placesPresenterModule(new PlacesPresenterModule(this))
                .build()
                .inject(this);

        ButterKnife.inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        assert toolbar != null;
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                mPresenter.loadPlaces();
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

    @Override protected void onStart() {
        super.onStart();
        EventBus.getDefault()
                .register(this);
        mPresenter.onScreenStarted();
    }

    @Override protected void onStop() {
        super.onStop();
        mPresenter.onScreenStopped();
        EventBus.getDefault()
                .unregister(this);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_map_mode:
                Intent i = new Intent(PlaceListActivity.this, PlaceMapActivity.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
            @NonNull int[] grantResults) {
        mPresenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Subscribe public void onEvent(PlacesModel.UpdateEvent event) {
        Place[] places = event.places;
        mList.setAdapter(new SimpleItemRecyclerViewAdapter(places));
        mEmptyView.setVisibility(
                places == null || places.length == 0 ? View.VISIBLE : View.GONE);
    }

    @Subscribe public void onEvent(PlacesModel.UpdateError event) {
        event.error.printStackTrace();
        Toast.makeText(PlaceListActivity.this, event.error.getMessage(),
                Toast.LENGTH_SHORT)
                .show();
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final Place[] mValues;

        public SimpleItemRecyclerViewAdapter(Place[] items) {
            mValues = items;
        }

        @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.place_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override public void onBindViewHolder(final ViewHolder holder, int position) {
            Place place = mValues[position];
            holder.mItem = place;
            holder.mIdView.setText(place.name);
            holder.mContentView.setText(place.vicinity);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putParcelable(PlaceDetailFragment.ARG_ITEM_ID,
                                holder.mItem);
                        PlaceDetailFragment fragment = new PlaceDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.foodieplace_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, PlaceDetailActivity.class);
                        intent.putExtra(PlaceDetailFragment.ARG_ITEM_ID, holder.mItem);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override public int getItemCount() {
            return mValues != null ? mValues.length : 0;
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
}
