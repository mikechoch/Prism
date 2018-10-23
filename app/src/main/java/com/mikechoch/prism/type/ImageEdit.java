package com.mikechoch.prism.type;

import com.mikechoch.prism.constant.Default;

public enum ImageEdit {

    FILTER("FILTER", Default.IMAGE_EDIT_TYPE_VIEW_PAGER_FILTER),
    EDIT("EDIT", Default.IMAGE_EDIT_TYPE_VIEW_PAGER_EDIT);

    private final String title;
    private final int id;

    ImageEdit(String title, int id) {
        this.title = title;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }
}
