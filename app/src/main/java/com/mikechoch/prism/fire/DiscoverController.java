package com.mikechoch.prism.fire;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mikechoch.prism.R;
import com.mikechoch.prism.attribute.DiscoveryPost;
import com.mikechoch.prism.attribute.DiscoveryRecyclerView;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;
import com.mikechoch.prism.fragment.SearchFragment;
import com.mikechoch.prism.helper.Helper;
import com.mikechoch.prism.type.Discovery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

    private static ArrayList<PrismPost> listOfAllPrismPosts;
    private static ArrayList<PrismPost> listOfPrismPostsForRandomTag;
    private static ArrayList<PrismUser> listOfRandomPrismUsers;
    private static ArrayList<DiscoveryPost> listOfPostsLikedByUserFollowings;
    private static ArrayList<DiscoveryPost> listOfPostsRepostedByUserFollowings;

    private static String randomTag;

    public static void setupDiscoverContent(Context context) {
        allPostsReference = Default.ALL_POSTS_REFERENCE;
        usersReference = Default.USERS_REFERENCE;
        tagsReference = Default.TAGS_REFERENCE;

        listOfAllPrismPosts = new ArrayList<>();
        listOfPrismPostsForRandomTag = new ArrayList<>();
        listOfRandomPrismUsers = new ArrayList<>();
        listOfPostsLikedByUserFollowings = new ArrayList<>();
        listOfPostsRepostedByUserFollowings = new ArrayList<>();

        fetchAllPosts(context);
        fetchPostsForRandomTag(context);
        fetchRandomUsers(context);
        fetchRandomPostsLikedByUserFollowings(context);
//        fetchRandomPostsRepostedByUserFollowings(context);
//        fetchRandomPostsUploadByRandomFollowingUser(context);
    }

    private static void fetchRandomPostsLikedByUserFollowings(Context context) {
        HashMap<String, PrismUser> randomPostIdsForLike = new HashMap<>();
        HashMap<String, PrismUser> randomPostIdsForRepost = new HashMap<>();
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot allUsersSnapshot) {
                for (String followingUserId : CurrentUser.getFollowings()) {
                    DataSnapshot followingUserSnapshot = allUsersSnapshot.child(followingUserId);
                    if (followingUserSnapshot.exists()) {
                        DataSnapshot likesSnapshot = followingUserSnapshot.child(Key.DB_REF_USER_LIKES);
                        DataSnapshot repostSnapshot = followingUserSnapshot.child(Key.DB_REF_USER_REPOSTS);
                        PrismUser followingPrismUser = Helper.constructPrismUserObject(followingUserSnapshot);
                        if (likesSnapshot.exists()) {
                            for (DataSnapshot postIdSnapshot : likesSnapshot.getChildren()) {
                                randomPostIdsForLike.put(postIdSnapshot.getKey(), followingPrismUser);

                            }
                        }
                        if (repostSnapshot.exists()) {
                            for (DataSnapshot postIdSnapshot : repostSnapshot.getChildren()) {
                                randomPostIdsForRepost.put(postIdSnapshot.getKey(), followingPrismUser);
                            }
                        }
                    }
                }

                allPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot allPostsSnapshot) {
                        for (Map.Entry<String, PrismUser> post : randomPostIdsForLike.entrySet()) {
                            DiscoveryPost discoveryPost = constructDiscoveryPost(allPostsSnapshot, post, allUsersSnapshot);
                            listOfPostsLikedByUserFollowings.add(discoveryPost);
                        }

                        for (Map.Entry<String, PrismUser> post : randomPostIdsForRepost.entrySet()) {
                            DiscoveryPost discoveryPost = constructDiscoveryPost(allPostsSnapshot, post, allUsersSnapshot);
                            listOfPostsRepostedByUserFollowings.add(discoveryPost);
                        }


                        DiscoveryRecyclerView followingLikeRecyclerView = new DiscoveryRecyclerView(Discovery.LIKE_BY_FOLLOWINGS, R.drawable.like_heart, "People you know liked");
                        SearchFragment.addDiscoveryRecyclerView(context, followingLikeRecyclerView);

                        DiscoveryRecyclerView followingRepostRecyclerView = new DiscoveryRecyclerView(Discovery.REPOST_BY_FOLLOWINGS, R.drawable.repost_iris, "People you know reposted");
                        SearchFragment.addDiscoveryRecyclerView(context, followingRepostRecyclerView);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            private DiscoveryPost constructDiscoveryPost(DataSnapshot allPostsSnapshot, Map.Entry<String, PrismUser> post, DataSnapshot allUsersSnapshot) {
                DiscoveryPost discoveryPost = null;
                String postId = post.getKey();
                PrismUser followingPrismUser = post.getValue();
                DataSnapshot postSnapshot = allPostsSnapshot.child(postId);
                if (postSnapshot.exists()) {
                    PrismPost prismPost = Helper.constructPrismPostObject(postSnapshot);
                    DataSnapshot prismPostAuthorSnapshot = allUsersSnapshot.child(prismPost.getUid());
                    if (prismPostAuthorSnapshot.exists()) {
                        PrismUser prismPostAuthor = Helper.constructPrismUserObject(prismPostAuthorSnapshot);
                        prismPost.setPrismUser(prismPostAuthor);
                        discoveryPost = new DiscoveryPost(prismPost, followingPrismUser);
                    }
                }
                return discoveryPost;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

                // TODO @Mike -- pick it up form here
                // use `getListOfRandomPrismUsers()` method to get the list ;)
                DiscoveryRecyclerView recyclerView = new DiscoveryRecyclerView(Discovery.USER, R.drawable.ic_account_white_36dp, "Users");
                SearchFragment.addDiscoveryRecyclerView(context, recyclerView);
            }



            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }


    private static void fetchUserDetailsAndGenerateRecyclerView(Context context, ArrayList<PrismPost> prismPosts, List<DiscoveryRecyclerView> recyclerViews) {
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

                for (DiscoveryRecyclerView recyclerView : recyclerViews) {
                    SearchFragment.addDiscoveryRecyclerView(context, recyclerView);
                }

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

                                ArrayList<DiscoveryRecyclerView> recyclerViews = new ArrayList<DiscoveryRecyclerView>() {{
                                    add(new DiscoveryRecyclerView(Discovery.TAG, R.drawable.ic_pound_white_48dp, randomTag));
                                }};

                                fetchUserDetailsAndGenerateRecyclerView(context, listOfPrismPostsForRandomTag, recyclerViews);

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
        allPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PrismPost prismPost = Helper.constructPrismPostObject(postSnapshot);
                    listOfAllPrismPosts.add(prismPost);
                }

                ArrayList<DiscoveryRecyclerView> recyclerViews = new ArrayList<DiscoveryRecyclerView>() {{
                    add(new DiscoveryRecyclerView(Discovery.LIKE, R.drawable.like_heart, "Most Liked"));
                    add(new DiscoveryRecyclerView(Discovery.REPOST, R.drawable.repost_iris, "Most Reposted"));
                }};

                fetchUserDetailsAndGenerateRecyclerView(context, listOfAllPrismPosts, recyclerViews);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });


    }

    public static ArrayList<PrismPost> generateHighestRepostedPosts() {
        ArrayList<PrismPost> highestRepostedPosts = new ArrayList<>(listOfAllPrismPosts);
        Collections.sort(highestRepostedPosts, new Comparator<PrismPost>() {
            @Override
            public int compare(PrismPost p1, PrismPost p2) {
                return p2.getReposts().compareTo(p1.getReposts());
            }
        });
        return highestRepostedPosts;
    }

    public static ArrayList<PrismPost> generateHighestLikedPosts() {
        ArrayList<PrismPost> highestLikedPosts = new ArrayList<>(listOfAllPrismPosts);
        Collections.sort(highestLikedPosts, new Comparator<PrismPost>() {
            @Override
            public int compare(PrismPost p1, PrismPost p2) {
                return p2.getLikes().compareTo(p1.getLikes());
            }
        });
        return highestLikedPosts;
    }

    public static ArrayList<PrismPost> getListOfPrismPostsForRandomTag() {
        return listOfPrismPostsForRandomTag;
    }

    public static ArrayList<PrismUser> getListOfRandomPrismUsers() {
        return listOfRandomPrismUsers;
    }

    public static ArrayList<DiscoveryPost> getListOfPostsLikedByUserFollowings() {
        return listOfPostsLikedByUserFollowings;
    }

    public static ArrayList<DiscoveryPost> getListOfPostsRepostedByUserFollowings() {
        return listOfPostsRepostedByUserFollowings;
    }



}
