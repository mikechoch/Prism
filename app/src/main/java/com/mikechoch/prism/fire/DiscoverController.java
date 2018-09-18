package com.mikechoch.prism.fire;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikechoch.prism.constant.Key;
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

    private static HashMap<String, PrismPost> mapOfPrismPosts;
    private static HashMap<String, PrismUser> mapOfPrismUsers;
    private static ArrayList<PrismPost> listOfPostsForRandomHashTag;

    public static String randomTag = "";

    public static void setupDiscoverContent(OnInitializeDiscoveryCallback callback) {
        allPostsReference = Default.ALL_POSTS_REFERENCE;
        usersReference = Default.USERS_REFERENCE;
        tagsReference = Default.TAGS_REFERENCE;

        mapOfPrismPosts = new HashMap<>();
        mapOfPrismUsers = new HashMap<>();
        listOfPostsForRandomHashTag = new ArrayList<>();

        fetchEverything(callback);
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
                            //listOfRandomPrismUsers.add(prismUser);
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


    private static void fetchUserDetails(OnInitializeDiscoveryCallback callback, ArrayList<PrismPost> prismPosts) {
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (PrismPost prismPost : prismPosts) {
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
                                        //listOfPrismPostsForRandomTag.add(prismPost);
                                    }
                                }

//                                ArrayList<DiscoveryRecyclerView> recyclerViews = new ArrayList<DiscoveryRecyclerView>() {{
//                                    add(new DiscoveryRecyclerView(Discovery.TAG, R.drawable.ic_pound_white_48dp, randomTag));
//                                }};
//
//                                fetchUserDetails(context, listOfPrismPostsForRandomTag, recyclerViews);

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
    private static void fetchEverything(OnInitializeDiscoveryCallback callback) {

        DatabaseReference rootReference = Default.ROOT_REFERENCE;

        rootReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot allPostsSnapshot = dataSnapshot.child(Key.DB_REF_ALL_POSTS);
                DataSnapshot usersSnapshot = dataSnapshot.child(Key.DB_REF_USER_PROFILES);
                DataSnapshot tagsSnapshot = dataSnapshot.child(Key.DB_REF_TAGS);

                // USERS
                for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
                    PrismUser prismUser = Helper.constructPrismUserObject(userSnapshot);
                    mapOfPrismUsers.put(prismUser.getUid(), prismUser);
                }

                // POSTS
                for (DataSnapshot postSnapshot : allPostsSnapshot.getChildren()) {
                    PrismPost prismPost = Helper.constructPrismPostObject(postSnapshot);
                    String uid = prismPost.getUid();
                    if (mapOfPrismUsers.containsKey(uid)) {
                        prismPost.setPrismUser(mapOfPrismUsers.get(uid));
                        mapOfPrismPosts.put(prismPost.getPostId(), prismPost);
                    }
                }

                // TAGS
                int tagCount = (int) tagsSnapshot.getChildrenCount();
                int rand = new Random().nextInt(tagCount);
                Iterator itr = tagsSnapshot.getChildren().iterator();
                for (int i = 0; i < rand; i++) { itr.next(); }
                DataSnapshot tagSnapshot = (DataSnapshot) itr.next();
                if (tagSnapshot.exists()) {
                    randomTag = tagSnapshot.getKey();
                    for (DataSnapshot postIdSnapshot : tagSnapshot.getChildren()) {
                        String postId = postIdSnapshot.getKey();
                        if (mapOfPrismPosts.containsKey(postId)) {
                            listOfPostsForRandomHashTag.add(mapOfPrismPosts.get(postId));
                        }
                    }
                }

                // RETURN
                callback.onSuccess();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });

    }



    public static void generateHighestRepostedPosts(OnFetchCallback onFetchCallback) {
        ArrayList<Object> highestRepostedPosts = new ArrayList<>(mapOfPrismPosts.values());
        Collections.sort(highestRepostedPosts, new Comparator<Object>() {
            @Override
            public int compare(Object p1, Object p2) {
                return ((PrismPost) p2).getReposts().compareTo(((PrismPost) p1).getReposts());
            }
        });
        onFetchCallback.onSuccess(highestRepostedPosts);
    }

    public static void generateHighestLikedPosts(OnFetchCallback onFetchCallback) {
        ArrayList<Object> highestLikedPosts = new ArrayList<>(mapOfPrismPosts.values());
        Collections.sort(highestLikedPosts, new Comparator<Object>() {
            @Override
            public int compare(Object p1, Object p2) {
                return ((PrismPost) p2).getLikes().compareTo(((PrismPost) p1).getLikes());
            }
        });
        onFetchCallback.onSuccess(highestLikedPosts);
    }

    public static void generateRandomPostsForHashTag(OnFetchCallback onFetchCallback) {
        Collections.shuffle(listOfPostsForRandomHashTag);
        onFetchCallback.onSuccess(new ArrayList<>(listOfPostsForRandomHashTag));
    }

//    public static ArrayList<PrismPost> getListOfPrismPostsForRandomTag(OnFetchCallback onFetchListener) {
//        return listOfPrismPostsForRandomTag;
//    }
//
//    public static ArrayList<PrismUser> getListOfRandomPrismUsers(OnFetchCallback onFetchListener) {
//        return listOfRandomPrismUsers;
//    }

}
