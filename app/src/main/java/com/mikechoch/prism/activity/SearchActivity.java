package com.mikechoch.prism.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikechoch.prism.R;
import com.mikechoch.prism.adapter.SearchTypeViewPagerAdapter;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fragment.PeopleSearchFragment;
import com.mikechoch.prism.fragment.TagSearchFragment;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.user_interface.InterfaceAction;

import java.util.ArrayList;

/**
 * Created by mikechoch on 2/6/18.
 */

public class SearchActivity  extends AppCompatActivity {

    /*
     * Global variables
     */
    private Toolbar toolbar;
    private EditText searchBarEditText;
    private ImageView searchBarClearButton;
    private TabLayout searchTypeTabLayout;
    private ViewPager searchTypeViewPager;

    private DatabaseReference allPostReference;
    private DatabaseReference usersReference;
    private DatabaseReference tagsReference;

    public static ArrayList<PrismUser> prismUserArrayList;

    public static ArrayList<Object> prismUserCollection;
    public static ArrayList<Object> hashTagsCollection;


    /**
     * Search for hashTags: search happens directly in Firebase because
     * hashTags are stored as strings and firebase Query can be easily
     * used for searching.
     * Search for users: All user's info is pulled and stored locally
     * in an arrayList which is then searched. It's inefficient but
     * search cannot happen inside of Firebase because the search query
     * needs to look for 'username' and 'full name'.
     */



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity_layout);

        allPostReference = Default.ALL_POSTS_REFERENCE;
        usersReference = Default.USERS_REFERENCE;
        tagsReference = Default.TAGS_REFERENCE;

        // Initialize all UI elements
        toolbar = findViewById(R.id.toolbar);
        searchBarEditText = findViewById(R.id.search_bar_edit_text);
        searchBarClearButton = findViewById(R.id.search_bar_clear_button);
        searchTypeTabLayout = findViewById(R.id.search_type_tab_layout);
        searchTypeViewPager = findViewById(R.id.search_type_view_pager);

        prismUserArrayList = new ArrayList<>();

        prismUserCollection = new ArrayList<>();
        hashTagsCollection = new ArrayList<>();

        searchTypeViewPager.setOffscreenPageLimit(2);
        searchTypeViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(searchTypeTabLayout));
        SearchTypeViewPagerAdapter searchTypeViewPagerAdapter = new SearchTypeViewPagerAdapter(getSupportFragmentManager());
        searchTypeViewPager.setAdapter(searchTypeViewPagerAdapter);
        searchTypeTabLayout.setupWithViewPager(searchTypeViewPager);

        searchTypeTabLayout.getTabAt(Default.SEARCH_TYPE_VIEW_PAGER_PEOPLE).setCustomView(Helper.createTabTextView(this, "PEOPLE"));
        searchTypeTabLayout.getTabAt(Default.SEARCH_TYPE_VIEW_PAGER_TAG).setCustomView(Helper.createTabTextView(this, "TAG"));

        int selectedTabColor = getResources().getColor(R.color.colorAccent);
        int unselectedTabColor = Color.WHITE;
        ((TextView) searchTypeTabLayout.getTabAt(searchTypeTabLayout.getSelectedTabPosition()).getCustomView())
                .setTextColor(selectedTabColor);

        // Setup the tab selected, unselected, and reselected listener
        searchTypeTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView()).setTextColor(selectedTabColor);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView()).setTextColor(unselectedTabColor);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Switch statement handing reselected tabs
                int tabPosition = tab.getPosition();
                switch (tabPosition) {
                    case 0:
                        break;
                    case 1:
                        break;
                    default:
                        break;
                }
            }
        });

        populateCollection();
        setupUIElements();
    }

    private void populateCollection() {
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                PrismUser prismUser = Helper.constructPrismUserObject(userSnapshot);
                                prismUserArrayList.add(prismUser);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Setup the toolbar and back button to return to MainActivity
     */
    private void setupToolbar() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     *
     */
    private void setupSearchBarEditText() {
        searchBarClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InterfaceAction.toggleKeyboard(SearchActivity.this, getCurrentFocus(), true);
                searchBarEditText.setText("");
            }
        });

        searchBarEditText.requestFocus();
        searchBarEditText.addTextChangedListener(new TextWatcher() {

            Handler handler = new Handler(Looper.getMainLooper());
            Runnable runnable;
            Query tagsReferenceQuery = tagsReference.orderByKey();
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) { }

                @Override
                public void onCancelled(DatabaseError databaseError) { }
            };

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int clearButtonVisibility = s.toString().length() > 0 ? View.VISIBLE : View.GONE;
                searchBarClearButton.setVisibility(clearButtonVisibility);

                hashTagsCollection.clear();
                handler.removeCallbacks(runnable);
                tagsReferenceQuery.removeEventListener(listener);
                tagsReferenceQuery = tagsReference.orderByKey().startAt(s.toString()).endAt(s.toString()+"\uf8ff").limitToFirst(50);
                // tagsReferenceQuery = tagsReferenceQuery.startAt(s.toString()).endAt(s.toString()+"\uf8ff").limitToLast(10);
                listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot tagSnapshot : dataSnapshot.getChildren()) {
                            hashTagsCollection.add(tagSnapshot.getKey());
                        }
                        if (TagSearchFragment.tagSearchRecyclerViewAdapter != null) {
                            TagSearchFragment.tagSearchRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                };

                // tagsReferenceQuery.addListenerForSingleValueEvent(listener);
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        performSearchForUser(s.toString());
                        tagsReferenceQuery.addListenerForSingleValueEvent(listener);
                    }
                };
                handler.postDelayed(runnable, 700);
            }
        });

        if (getIntent().getStringExtra("ClickedTag") != null) {
            String clickedTag = getIntent().getStringExtra("ClickedTag");
            searchBarEditText.setText(clickedTag);
            searchBarEditText.setSelection(clickedTag.length());
            searchTypeTabLayout.getTabAt(Default.SEARCH_TYPE_VIEW_PAGER_TAG).select();
        }
    }

    private void performSearchForUser(String query) {
        ArrayList<PrismUser> highRelevance = new ArrayList<>();
        ArrayList<PrismUser> mediumRelevance = new ArrayList<>();
        ArrayList<PrismUser> lowRelevance = new ArrayList<>();

        for (PrismUser prismUser : prismUserArrayList) {
            String fullName = prismUser.getFullName().toLowerCase();
            String username = prismUser.getUsername().toLowerCase();

            if (fullName.startsWith(query) || username.startsWith(query)) {
                highRelevance.add(prismUser);
            } else if (fullName.endsWith(query) || username.endsWith(query)) {
                mediumRelevance.add(prismUser);
            } else if (fullName.contains(query) || username.contains(query)) {
                lowRelevance.add(prismUser);
            }

        }

        prismUserCollection.clear();
        prismUserCollection.addAll(highRelevance);
        prismUserCollection.addAll(mediumRelevance);
        prismUserCollection.addAll(lowRelevance);

        if (PeopleSearchFragment.peopleSearchRecyclerViewAdapter != null) {
            PeopleSearchFragment.peopleSearchRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Setup all UI elements
     */
    private void setupUIElements() {
        setupToolbar();

        // Setup Typefaces for all text based UI elements
        searchBarEditText.setTypeface(Default.sourceSansProLight);

        setupSearchBarEditText();
    }
}
