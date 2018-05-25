package com.mikechoch.prism.type;

import com.mikechoch.prism.R;

public enum Edit {

    BRIGHTNESS(R.drawable.ic_brightness_6_white_48dp, "Brightness"),
    CONTRAST(R.drawable.ic_contrast_circle_white_48dp, "Contrast"),
    SATURATION(R.drawable.ic_water_white_48dp, "Saturation");

    private final int icon;
    private final String title;

    Edit(int icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }
}
