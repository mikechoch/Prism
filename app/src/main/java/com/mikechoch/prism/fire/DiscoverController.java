package com.mikechoch.prism.fire;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.callback.action.OnInitializeDiscoveryCallback;
import com.mikechoch.prism.callback.fetch.OnFetchCallback;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;
import com.mikechoch.prism.helper.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class DiscoverController {


    /**
     * TODO
     * ALL THE FIREBASE FETCH REQUESTS MADE IN THIS CLASS
     * WILL NEED TO BE CHANGED ONCE PRISM HAS 500-1000 POSTS
     * THE CURRENT IMPLEMENTATION IS INEFFICIENT BUT IT'S THE
     * ONLY OPTION AS THERE IS NOT ENOUGH CONTENT TO PLAY AROUND WITH
     */


    private static HashMap<String, PrismPost> mapOfPrismPosts;
    private static HashMap<String, PrismUser> mapOfPrismUsers;
    private static HashMap<String, ArrayList<PrismPost>> mapOfRandomHashtags;


    public static void setupDiscoverContent(OnInitializeDiscoveryCallback callback) {
        mapOfPrismPosts = new HashMap<>();
        mapOfPrismUsers = new HashMap<>();
        mapOfRandomHashtags = new HashMap<>();

        fetchEverything(callback);
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
                if (dataSnapshot.exists()) {
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
                    for (int tagCtr = 0; tagCtr < Default.DISCOVER_PAGE_HASHTAGS; tagCtr++) {
                        int rand = new Random().nextInt(tagCount);
                        Iterator itr = tagsSnapshot.getChildren().iterator();
                        for (int i = 0; i < rand; i++) itr.next();
                        DataSnapshot tagSnapshot = (DataSnapshot) itr.next();
                        if (tagSnapshot.exists()) {
                            String hashTag = tagSnapshot.getKey();
                            ArrayList<PrismPost> postsForHashTag = new ArrayList<>();
                            for (DataSnapshot postIdSnapshot : tagSnapshot.getChildren()) {
                                String postId = postIdSnapshot.getKey();
                                if (mapOfPrismPosts.containsKey(postId)) {
                                    postsForHashTag.add(mapOfPrismPosts.get(postId));
                                }
                            }
                            mapOfRandomHashtags.put(hashTag, postsForHashTag);
                        }
                    }

                    // RETURN
                    callback.onSuccess();

                }
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

    public static ArrayList<String> getRandomHashtags() {
        return new ArrayList<>(mapOfRandomHashtags.keySet());
    }

    public static ArrayList<PrismPost> getPrismPostsForHashtag(String hashTag) {
        return mapOfRandomHashtags.containsKey(hashTag) ?
                mapOfRandomHashtags.get(hashTag) : new ArrayList<>();
    }

    public static void generateRandomListOfUsers(OnFetchCallback onFetchCallback) {
        ArrayList<Object> randomUsers = new ArrayList<>();
        for (PrismUser prismUser : mapOfPrismUsers.values()) {
            if (!CurrentUser.isFollowingPrismUser(prismUser) && !Helper.isPrismUserCurrentUser(prismUser)) {
                randomUsers.add(prismUser);
            }
        }
        onFetchCallback.onSuccess(randomUsers);
    }

}
