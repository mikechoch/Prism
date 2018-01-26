package com.mikechoch.prism;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by parth on 1/25/18.
 */

public class CurrentUser {

    private DatabaseReference userReference;
    private FirebaseAuth auth;
    private FirebaseUser user;

    public static HashMap userLikedPosts; // KEY: String postID   VALUE: long timestamp

    public CurrentUser() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userReference = Default.USERS_REFERENCE.child(user.getUid());
        userLikedPosts = new HashMap<String, Long>();
        userReference.child(Key.DB_REF_USER_LIKES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userLikedPosts.putAll((Map) dataSnapshot.getValue());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });



    }
}
