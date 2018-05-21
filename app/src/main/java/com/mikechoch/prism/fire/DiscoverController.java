package com.mikechoch.prism.fire;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fragment.SearchFragment;
import com.mikechoch.prism.helper.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DiscoverController {


    private static DatabaseReference allPostsReference;
    private static DatabaseReference tagsReference;
    private static DatabaseReference usersReference;

    private static ArrayList<PrismPost> listOfPrismPosts;


    public static void setupDiscoverContent(Context context) {
        allPostsReference = Default.ALL_POSTS_REFERENCE;
        usersReference = Default.USERS_REFERENCE;
        listOfPrismPosts = new ArrayList<>();

        fetchAllPosts(context);
    }

    /**
     * TODO
     * In future, when we have a lot of posts, fetching all the posts
     * will be expensive. So at that point, we should only pull posts
     * from last 1 week or last few days to show on discover page
     */
    private static void fetchAllPosts(Context context) {
        allPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PrismPost prismPost = Helper.constructPrismPostObject(postSnapshot);
                    listOfPrismPosts.add(prismPost);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (PrismPost prismPost : listOfPrismPosts) {
                    DataSnapshot postAuthorUserSnapshot = dataSnapshot.child(prismPost.getUid());
                    if (postAuthorUserSnapshot.exists()) {
                        PrismUser prismUser = Helper.constructPrismUserObject(postAuthorUserSnapshot);
                        prismPost.setPrismUser(prismUser);
                    } else {
                        // TODO Should not happen. Log wtf exception if happens
                        listOfPrismPosts.remove(prismPost);
                    }
                }

                SearchFragment.createAllDiscoveryRecyclerViews(context);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public static ArrayList<PrismPost> generateHighestRepostedPosts() {
        ArrayList<PrismPost> highestRepostedPosts = new ArrayList<>(listOfPrismPosts);
        Collections.sort(highestRepostedPosts, new Comparator<PrismPost>() {
            @Override
            public int compare(PrismPost p1, PrismPost p2) {
                return p2.getReposts().compareTo(p1.getReposts());
            }
        });
        // TODO @MIKE: highestRepostedPosts arrayList is ready here. call notifyDatasetChanged here
        // @Mike: don't make these local arrayLists global or static. If you have to, let me know
        return highestRepostedPosts;
    }

    public static ArrayList<PrismPost> generateHighestLikedPosts() {
        ArrayList<PrismPost> highestLikedPosts = new ArrayList<>(listOfPrismPosts);
        Collections.sort(highestLikedPosts, new Comparator<PrismPost>() {
            @Override
            public int compare(PrismPost p1, PrismPost p2) {
                return p2.getLikes().compareTo(p1.getLikes());
            }
        });
        // TODO @MIKE: highestLikedPosts arrayList is ready here. call notifyDatasetChanged here
        // @Mike: don't make these local arrayLists global or static. If you have to, let me know
        return highestLikedPosts;
    }


}
