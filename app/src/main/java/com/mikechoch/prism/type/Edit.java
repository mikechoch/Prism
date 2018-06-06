package com.mikechoch.prism.type;

import com.mikechoch.prism.R;

public enum Edit {

    BRIGHTNESS(R.drawable.ic_brightness_6_white_48dp, "Brightness", -100f, 100f, 0f),
    CONTRAST(R.drawable.ic_contrast_circle_white_48dp, "Contrast", 0.4f, 1.6f, 1f),
    SATURATION(R.drawable.ic_water_white_48dp, "Saturation", 0, 2f, 1f);

    private final int icon;
    private final String title;
    private final float min;
    private final float max;
    private final float def;

    Edit(int icon, String title, float min, float max, float def) {
        this.icon = icon;
        this.title = title;
        this.min = min;
        this.max = max;
        this.def = def;
    }

    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public float getDef() {
        return def;
    }
}
