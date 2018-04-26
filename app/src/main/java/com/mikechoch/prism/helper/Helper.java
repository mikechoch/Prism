package com.mikechoch.prism.helper;

import android.text.format.DateFormat;

import com.google.firebase.database.DataSnapshot;
import com.mikechoch.prism.fire.CurrentUser;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.attribute.PrismUser;
import com.mikechoch.prism.attribute.ProfilePicture;
import com.mikechoch.prism.constant.Default;
import com.mikechoch.prism.constant.Key;
import com.mikechoch.prism.constant.MyTimeUnit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by parth on 2/16/18.
 */

public class Helper {


    /**
     * Takes in a dataSnapshot object and parses its contents
     * and returns a prismPost object
     * @return PrismPost object
     */
    public static PrismPost constructPrismPostObject(DataSnapshot postSnapshot) {
        PrismPost prismPost = postSnapshot.getValue(PrismPost.class);
        prismPost.setPostId(postSnapshot.getKey());
        prismPost.setLikes((int) postSnapshot.child(Key.DB_REF_POST_LIKED_USERS).getChildrenCount());
        prismPost.setReposts((int) postSnapshot.child(Key.DB_REF_POST_REPOSTED_USERS).getChildrenCount());
        return prismPost;
    }

    /**
     * Takes in userSnapshot object and parses the firebaseUser details
     * and creates a prismUser object
     * @return PrismUser object
     */
    public static PrismUser constructPrismUserObject(DataSnapshot userSnapshot) {
        PrismUser prismUser = new PrismUser();
        prismUser.setUid(userSnapshot.getKey());
        prismUser.setUsername((String) userSnapshot.child(Key.USER_PROFILE_USERNAME).getValue());
        prismUser.setFullName((String) userSnapshot.child(Key.USER_PROFILE_FULL_NAME).getValue());
        prismUser.setProfilePicture(new ProfilePicture((String) userSnapshot.child(Key.USER_PROFILE_PIC).getValue()));

        int followerCount = 0;
        int followingCount = 0;

        if (userSnapshot.hasChild(Key.DB_REF_USER_FOLLOWERS)) {
            followerCount = (int) userSnapshot.child(Key.DB_REF_USER_FOLLOWERS).getChildrenCount();
        }
        if (userSnapshot.hasChild(Key.DB_REF_USER_FOLLOWINGS)) {
            followingCount = (int) userSnapshot.child(Key.DB_REF_USER_FOLLOWINGS).getChildrenCount();
        }
        if (userSnapshot.hasChild(Key.USER_TOKEN)) {
            prismUser.setToken((String) userSnapshot.child(Key.USER_TOKEN).getValue());
        }

        prismUser.setFollowerCount(followerCount);
        prismUser.setFollowingCount(followingCount);

        return prismUser;
    }

    /**
     *
     */
    public static boolean isPrismUserCurrentUser(PrismUser prismUser) {
        return CurrentUser.prismUser.getUid().equals(prismUser.getUid());
    }

    /**
     *
     */
    public static String getSingularOrPluralText(String string, int count) {
        return count == 1 ? string : string + "s";
    }

    /**
     * Takes in the time of the post and creates a fancy string difference
     * Examples:
     * 10 seconds ago/Just now      (time < minute)
     * 20 minutes ago               (time < hour)
     * 2 hours ago                  (time < day)
     * 4 days ago                   (time < week)
     * January 21                   (time < year)
     * September 18, 2017           (else)
     */
    public static String getFancyDateDifferenceString(long time) {
        // Create a calendar object and calculate the timeFromStart
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        long timeFromCurrent = calendar.getTimeInMillis() - time;

        // Set the calendar object to be the time of the post
        calendar.setTimeInMillis(time);

        // Calculate all units for the given timeFromCurrent
        long secondsTime = TimeUnit.MILLISECONDS.toSeconds(timeFromCurrent);
        long minutesTime = TimeUnit.MILLISECONDS.toMinutes(timeFromCurrent);
        long hoursTime = TimeUnit.MILLISECONDS.toHours(timeFromCurrent);
        long daysTime = TimeUnit.MILLISECONDS.toDays(timeFromCurrent);

        // The fancyDateString will start off as this DateFormat to satisfy the else case
        String fancyDateString = DateFormat.format("MMM dd, yyyy", calendar).toString();

        // Check each calculated time unit until it is clear the unit of timeFromCurrent
        if (secondsTime < MyTimeUnit.SECONDS_UNIT) {
//                String fancyDateTail = secondsTime == 1 ? " second ago" : " seconds ago";
//                fancyDateString = secondsTime + fancyDateTail;
            fancyDateString = "Just now";
        } else if (minutesTime < MyTimeUnit.MINUTES_UNIT) {
            String fancyDateTail = minutesTime == 1 ? " minute ago" : " minutes ago";
            fancyDateString = minutesTime + fancyDateTail;
        } else if (hoursTime < MyTimeUnit.HOURS_UNIT) {
            String fancyDateTail = hoursTime == 1 ? " hour ago" : " hours ago";
            fancyDateString = hoursTime + fancyDateTail;
        } else if (daysTime < MyTimeUnit.DAYS_UNIT) {
            String fancyDateTail = daysTime == 1 ? " day ago" : " days ago";
            fancyDateString = daysTime + fancyDateTail;
        } else if (daysTime < MyTimeUnit.YEARS_UNIT) {
            fancyDateString = DateFormat.format("MMM dd", calendar).toString();
        }
        return fancyDateString;
    }

    /**
     * Takes the user inputted formatted usernmae and replaces the
     * period `.` character with a dash `-` so that it can be saved in firebase
     */
    public static String getFirebaseEncodedUsername(String inputUsername) {
        return inputUsername.replace(Default.USERNAME_PERIOD, Default.USERNAME_PERIOD_REPLACE);
    }

    /**
     * Takes the username stored in firebase and replaces the dash `-`
     * character with the period `.` so
     */
    public static String getFirebaseDecodedUsername(String encodedUsername) {
        return encodedUsername.replace(Default.USERNAME_PERIOD_REPLACE, Default.USERNAME_PERIOD);
    }

    /**
     * Checks to see if given prismPost has been reposted by given
     * prismUser by comparing the uid of prismPost author by given
     * prismUser. If uids match, post author = given prismUser and
     * hence it's an upload, otherwise it is a repost
     */
    public static boolean isPostReposted(PrismPost prismPost, PrismUser prismUser) {
        return !prismPost.getUid().equals(prismUser.getUid());
    }

    /**
     *
     */
    public static ArrayList<String> parseDescriptionForTags(String description) {
        HashSet<String>  hashTags = new HashSet<>();
        Matcher match = Pattern.compile(Default.REGEX_HASHTAG).matcher(description);
        while (match.find()) {
            hashTags.add(match.group(1));
        }
        return new ArrayList<>(hashTags);



        /* ArrayList<String> listOfTags = new ArrayList<>();
        for (int i = 0; i < description.length(); i++) {
            char currentChar = description.charAt(i++);
            if (currentChar == '#') {
                String tag = "";
                while (true) {
                    currentChar = description.charAt(i);
                    if (currentChar == ' ' || currentChar == '#' || i >= description.length()) {
                        break;
                    }
                    tag += currentChar;
                }
                listOfTags.add(tag);
            }
        }
        return listOfTags; */
    }

    public static boolean stringContains(String mainString, String subString) {
        return Pattern.compile(Pattern.quote(subString), Pattern.CASE_INSENSITIVE).matcher(mainString).find();
    }

}
