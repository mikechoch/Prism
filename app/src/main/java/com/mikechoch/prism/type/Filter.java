package com.mikechoch.prism.type;

public enum Filter {

    NORMAL("Normal", 100, 100, 100),
    SHADE("Shade", 70, 150, 80),
    COLORLESS("Colorless", 100, 140, 0),
    COLORFUL("Colorful", 100, 140, 140);

    private final String title;
    private final int brightness;
    private final int contrast;
    private final int saturation;

    Filter(String title, int brightness, int contrast, int saturation) {
        this.title = title;
        this.brightness = brightness;
        this.contrast = contrast;
        this.saturation = saturation;
    }

    public String getTitle() {
        return title;
    }

    public int getBrightness() {
        return brightness;
    }

    public int getContrast() {
        return contrast;
    }

    public int getSaturation() {
        return saturation;
    }
}
