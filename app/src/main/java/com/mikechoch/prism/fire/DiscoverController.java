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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class DiscoverController {


    private static DatabaseReference allPostsReference;
    private static DatabaseReference tagsReference;
    private static DatabaseReference usersReference;

    private static ArrayList<PrismPost> listOfPrismPosts;
    private static ArrayList<PrismPost> listofPrismPostsForRandomTag;

    public static void setupDiscoverContent(Context context) {
        allPostsReference = Default.ALL_POSTS_REFERENCE;
        usersReference = Default.USERS_REFERENCE;
        tagsReference = Default.TAGS_REFERENCE;

        listOfPrismPosts = new ArrayList<>();
        listofPrismPostsForRandomTag = new ArrayList<>();

        fetchAllPosts(context);
        fetchPostsForRandomTag(context);
    }

    private static void fetchPostsForRandomTag(Context context) {
        tagsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int tagCount = (int) dataSnapshot.getChildrenCount();
                    int rand = new Random().nextInt(tagCount);
                    Iterator itr = dataSnapshot.getChildren().iterator();

                    for (int i = 0; i < rand; i++) { itr.next(); }
                    DataSnapshot tagSnapshot = (DataSnapshot) itr.next();

                    if (tagSnapshot.exists()) {
                        HashMap<String, Long> listOfPosts = new HashMap<>((Map) tagSnapshot.getValue());
                        allPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot allPostsSnapshot) {
                                for (String postId : listOfPosts.keySet()) {
                                    DataSnapshot postSnapshot = allPostsSnapshot.child(postId);
                                    if (postSnapshot.exists()) {
                                        PrismPost prismPost = Helper.constructPrismPostObject(postSnapshot);
                                        listofPrismPostsForRandomTag.add(prismPost);
                                    }
                                }

                                usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (PrismPost prismPost : listofPrismPostsForRandomTag) {
                                            DataSnapshot postAuthorUserSnapshot = dataSnapshot.child(prismPost.getUid());
                                            if (postAuthorUserSnapshot.exists()) {
                                                PrismUser prismUser = Helper.constructPrismUserObject(postAuthorUserSnapshot);
                                                prismPost.setPrismUser(prismUser);
                                            }
                                        }

                                        SearchFragment.createAllDiscoveryRecyclerViews(context);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) { }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) { }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    /**
     * TODO
     * In future, when we have a lot of posts, fetching all the posts
     * will be expensive. So at that point, we should only pull posts
     * from last 1 week or last few days to show on discover page
     */
    private static void fetchAllPosts(Context context) {
        ArrayList<PrismPost> listOfPrismPosts= new ArrayList<>();
        allPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PrismPost prismPost = Helper.constructPrismPostObject(postSnapshot);
                    listOfPrismPosts.add(prismPost);
                }

                usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (PrismPost prismPost : listOfPrismPosts) {
                            DataSnapshot postAuthorUserSnapshot = dataSnapshot.child(prismPost.getUid());
                            if (postAuthorUserSnapshot.exists()) {
                                PrismUser prismUser = Helper.constructPrismUserObject(postAuthorUserSnapshot);
                                prismPost.setPrismUser(prismUser);
                            }
                        }

                        SearchFragment.createAllDiscoveryRecyclerViews(context);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
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
        return highestLikedPosts;
    }

    public static ArrayList<PrismPost> getListofPrismPostsForRandomTag() {
        return listofPrismPostsForRandomTag;
    }


}