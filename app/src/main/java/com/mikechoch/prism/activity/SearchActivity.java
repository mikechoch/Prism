package com.mikechoch.prism.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikechoch.prism.R;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.helper.Helper;

import java.util.ArrayList;

/**
 * Created by mikechoch on 2/6/18.
 */

public class SearchActivity  extends AppCompatActivity {

    /*
     * Global variables
     */
    private Typeface sourceSansProLight;
    private Typeface sourceSansProBold;

    private Toolbar toolbar;
    private EditText searchBarEditText;

    private DatabaseReference allPostReference;
    private DatabaseReference usersReference;
    private DatabaseReference tagsReference;
    private ArrayList<String> hashTagsCollection;
    private ArrayList<PrismUser> prismUsersCollection;


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

        // Create two typefaces
        sourceSansProLight = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Light.ttf");
        sourceSansProBold = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Black.ttf");

        // Initialize all UI elements
        toolbar = findViewById(R.id.toolbar);
        searchBarEditText = findViewById(R.id.search_bar_edit_text);

        populateCollection();
        setupUIElements();
    }

    private void populateCollection() {
        prismUsersCollection = new ArrayList<>();
        hashTagsCollection = new ArrayList<>();

//        Query query = tagsReference.orderByValue().limitToFirst(100);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot postIdSnapshot : dataSnapshot.getChildren()) {
//                        hashTagsCollection.add(postIdSnapshot.getKey());
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) { }
//        });

        usersReference.limitToFirst(100)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                PrismUser prismUser = Helper.constructPrismUserObject(userSnapshot);
                                prismUsersCollection.add(prismUser);
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
        searchBarEditText.requestFocus();
        prismUsersCollection = new ArrayList<>();
        searchBarEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            Handler handler = new Handler(Looper.getMainLooper());
            Runnable runnable;
            Query query = tagsReference.orderByKey();
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) { }

                @Override
                public void onCancelled(DatabaseError databaseError) { }
            };

            @Override
            public void afterTextChanged(Editable s) {
                hashTagsCollection.clear();
                handler.removeCallbacks(runnable);
                query.removeEventListener(listener);
                query = tagsReference.orderByKey().startAt(s.toString()).endAt(s.toString()+"\uf8ff").limitToFirst(50);
                // query = query.startAt(s.toString()).endAt(s.toString()+"\uf8ff").limitToLast(10);
                listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot tagSnapshot : dataSnapshot.getChildren()) {
                            hashTagsCollection.add(tagSnapshot.getKey());
                        }
                        printHashTagCollections();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                };
                // query.addListenerForSingleValueEvent(listener);
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        performSearchForUser(s.toString());
                        query.addListenerForSingleValueEvent(listener);
                    }
                };
                handler.postDelayed(runnable, 700);
            }
        });
    }

    private void printHashTagCollections() {
        System.out.println("\n\n\n");
        for (String hashTag : hashTagsCollection) {
            System.out.println(hashTag);
        }
        System.out.println("\n\n\n");
    }

    private void performSearchForUser(String query) {
        ArrayList<PrismUser> results = new ArrayList<>();
        ArrayList<PrismUser> highRelevance = new ArrayList<>();
        ArrayList<PrismUser> mediumRelevance = new ArrayList<>();
        ArrayList<PrismUser> lowRelevance = new ArrayList<>();

        for (PrismUser prismUser : prismUsersCollection) {
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

        results.addAll(highRelevance);
        results.addAll(mediumRelevance);
        results.addAll(lowRelevance);

        // System print
        System.out.println("\n\n\n");
        for (PrismUser user : results) {
            System.out.println(user.getFullName() + " - " + user.getUsername());
        }
        System.out.println("\n\n\n");
    }

    /**
     * Setup all UI elements
     */
    private void setupUIElements() {
        setupToolbar();

        // Setup Typefaces for all text based UI elements
        searchBarEditText.setTypeface(sourceSansProLight);

        setupSearchBarEditText();
    }

    private void toast(String bread) {
        Toast.makeText(this, bread, Toast.LENGTH_SHORT).show();
    }
}
