package com.mikechoch.prism.type;

import com.mikechoch.prism.R;

public enum Rotation {

    CLOCKWISE_90(R.drawable.ic_format_rotate_90_white_48dp),
    FLIP_VERTICAL(R.drawable.ic_reflect_vertical_white_48dp),
    FLIP_HORIZONTAL(R.drawable.ic_reflect_horizontal_white_48dp);

    private final int icon;

    Rotation(int icon) {
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }
}
