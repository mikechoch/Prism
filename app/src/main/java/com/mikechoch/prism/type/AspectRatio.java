package com.mikechoch.prism.type;

public enum AspectRatio {

    ASPECT_RATIO_1_1("1:1", 1, 1),
    ASPECT_RATIO_4_3("4:3", 4, 3),
    ASPECT_RATIO_ORIGINAL("ORIGINAL", 0, 0),
    ASPECT_RATIO_9_16("9:16", 9, 16),
    ASPECT_RATIO_2_3("2:3", 2, 3);

    final private String title;
    final private int x;
    final private int y;

    AspectRatio(String title, int x, int y) {
        this.title = title;
        this.x = x;
        this.y = y;
    }

    public String getTitle() {
        return title;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
