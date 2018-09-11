package com.mikechoch.prism.type;

import com.mikechoch.prism.R;
import com.mikechoch.prism.constant.Default;

public enum ProfilePictureOption {

    GALLERY("Choose from gallery", R.drawable.ic_image_white_36dp, Default.PROFILE_PICTURE_GALLERY),
    SELFIE("Take a selfie", R.drawable.ic_camera_front_variant_white_36dp, Default.PROFILE_PICTURE_SELFIE),
    VIEW("View profile picture", R.drawable.ic_account_circle_white_36dp, Default.PROFILE_PICTURE_VIEW);

    final String title;
    final int icon;
    final int id;

    ProfilePictureOption(String title, int icon, int id) {
        this.title = title;
        this.icon = icon;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public int getId() {
        return id;
    }
}
