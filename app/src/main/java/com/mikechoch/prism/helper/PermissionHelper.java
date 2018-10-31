package com.mikechoch.prism.helper;

import com.google.firebase.auth.FirebaseUser;
import com.mikechoch.prism.attribute.PrismPost;
import com.mikechoch.prism.fire.CurrentUser;

public class PermissionHelper {


    public static boolean allowDeletePost(PrismPost prismPost) {
        FirebaseUser user = CurrentUser.getFirebaseUser();
        return user.isEmailVerified() && user.getUid().equals(prismPost.getUid());
    }

    public static boolean allowUploadPost() {
        return CurrentUser.getFirebaseUser().isEmailVerified();
    }

    public static boolean allowRepost(PrismPost prismPost) {
        return !Helper.isPrismUserCurrentUser(prismPost.getPrismUser());
    }
}
