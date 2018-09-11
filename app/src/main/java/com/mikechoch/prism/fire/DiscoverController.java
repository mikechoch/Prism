package com.mikechoch.prism.fire;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mikechoch.prism.fire.callback.OnFetchCallback;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.fire.callback.OnInitializeDiscoveryCallback;
import com.mikechoch.prism.helper.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class DiscoverController {


    /**
     * TODO
     * ALL THE FIREBASE FETCH REQUESTS MADE IN THIS CLASS
     * WILL NEED TO BE CHANGED ONCE PRISM HAS 500-1000 POSTS
     * THE CURRENT IMPLEMENTATION IS INEFFICIENT BUT IT'S THE
     * ONLY OPTION AS THERE IS NOT ENOUGH CONTENT TO PLAY AROUND WITH
     */

    private static DatabaseReference allPostsReference;
    private static DatabaseReference tagsReference;
    private static DatabaseReference usersReference;

    private static ArrayList<Object> listOfAllPrismPosts;
    private static ArrayList<Object> listOfPrismPostsForRandomTag;
    private static ArrayList<Object> listOfRandomPrismUsers;

    private static String randomTag;

    public static void setupDiscoverContent(OnInitializeDiscoveryCallback callback) {
        allPostsReference = Default.ALL_POSTS_REFERENCE;
        usersReference = Default.USERS_REFERENCE;
        tagsReference = Default.TAGS_REFERENCE;

        listOfAllPrismPosts = new ArrayList<>();
        listOfPrismPostsForRandomTag = new ArrayList<>();
        listOfRandomPrismUsers = new ArrayList<>();

        fetchAllPosts(callback);
//        fetchPostsForRandomTag();
//        fetchRandomUsers();

    }

    private static void fetchRandomUsers(Context context) {
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> users = new HashMap<>((Map)dataSnapshot.getValue());
                ArrayList<String> userIds = new ArrayList<>(users.keySet());
                Collections.shuffle(userIds);

                for (int i = 0; i < 10; i++) {
                    if (i >= userIds.size()) break;
                    DataSnapshot userSnapshot = dataSnapshot.child(userIds.get(i));
                    if (userSnapshot.exists()) {
                        PrismUser prismUser = Helper.constructPrismUserObject(userSnapshot);
                        if (!Helper.isPrismUserCurrentUser(prismUser) && !CurrentUser.isFollowingPrismUser(prismUser)) {
                            listOfRandomPrismUsers.add(prismUser);
                        }
                    }
                }

//                DiscoveryRecyclerView recyclerView = new DiscoveryRecyclerView(Discovery.USER, R.drawable.ic_account_white_36dp, "Users");
//                SearchFragment.addDiscoveryRecyclerView(context, recyclerView);
            }



            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }


    private static void fetchUserDetailsAndGenerateRecyclerView(OnInitializeDiscoveryCallback callback, ArrayList<Object> prismPosts) {
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (Object post : prismPosts) {
                    PrismPost prismPost = (PrismPost) post;
                    DataSnapshot postAuthorUserSnapshot = dataSnapshot.child(prismPost.getUid());
                    if (postAuthorUserSnapshot.exists()) {
                        PrismUser prismUser = Helper.constructPrismUserObject(postAuthorUserSnapshot);
                        prismPost.setPrismUser(prismUser);
                    }
                }

                callback.onSuccess();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
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
                        randomTag = tagSnapshot.getKey();
                        HashMap<String, Long> listOfPosts = new HashMap<>((Map) tagSnapshot.getValue());
                        allPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot allPostsSnapshot) {
                                for (String postId : listOfPosts.keySet()) {
                                    DataSnapshot postSnapshot = allPostsSnapshot.child(postId);
                                    if (postSnapshot.exists()) {
                                        PrismPost prismPost = Helper.constructPrismPostObject(postSnapshot);
                                        listOfPrismPostsForRandomTag.add(prismPost);
                                    }
                                }

//                                ArrayList<DiscoveryRecyclerView> recyclerViews = new ArrayList<DiscoveryRecyclerView>() {{
//                                    add(new DiscoveryRecyclerView(Discovery.TAG, R.drawable.ic_pound_white_48dp, randomTag));
//                                }};
//
//                                fetchUserDetailsAndGenerateRecyclerView(context, listOfPrismPostsForRandomTag, recyclerViews);

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
    private static void fetchAllPosts(OnInitializeDiscoveryCallback callback) {
        allPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PrismPost prismPost = Helper.constructPrismPostObject(postSnapshot);
                    listOfAllPrismPosts.add(prismPost);
                }

                fetchUserDetailsAndGenerateRecyclerView(callback, listOfAllPrismPosts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure();
            }
        });


    }



    public static void generateHighestRepostedPosts(OnFetchCallback onFetchCallback) {
        ArrayList<Object> highestRepostedPosts = new ArrayList<>(listOfAllPrismPosts);
        Collections.sort(highestRepostedPosts, new Comparator<Object>() {
            @Override
            public int compare(Object p1, Object p2) {
                return ((PrismPost) p2).getReposts().compareTo(((PrismPost) p1).getReposts());
            }
        });
        onFetchCallback.onSuccess(highestRepostedPosts);
    }

    public static void generateHighestLikedPosts(OnFetchCallback onFetchCallback) {
        ArrayList<Object> highestLikedPosts = new ArrayList<>(listOfAllPrismPosts);
        Collections.sort(highestLikedPosts, new Comparator<Object>() {
            @Override
            public int compare(Object p1, Object p2) {
                return ((PrismPost) p2).getLikes().compareTo(((PrismPost) p1).getLikes());
            }
        });
        onFetchCallback.onSuccess(highestLikedPosts);
    }

//    public static ArrayList<PrismPost> getListOfPrismPostsForRandomTag(OnFetchCallback onFetchListener) {
//        return listOfPrismPostsForRandomTag;
//    }
//
//    public static ArrayList<PrismUser> getListOfRandomPrismUsers(OnFetchCallback onFetchListener) {
//        return listOfRandomPrismUsers;
//    }

}
