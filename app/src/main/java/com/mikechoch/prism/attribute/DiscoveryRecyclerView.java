package com.mikechoch.prism.attribute;

import android.support.v7.widget.RecyclerView;

import com.mikechoch.prism.type.Discovery;

public class DiscoveryRecyclerView {

    private Discovery discoveryType;
    private int titleIcon;
    private String title;
    private RecyclerView discoveryRecyclerView;

    public DiscoveryRecyclerView(Discovery discoveryType, int titleIcon, String title) {
        this.discoveryType = discoveryType;
        this.titleIcon = titleIcon;
        this.title = title;
    }

    public Discovery getDiscoveryType() {
        return discoveryType;
    }

    public int getTitleIcon() {
        return titleIcon;
    }

    public String getTitle() {
        return title;
    }

    public RecyclerView getDiscoveryRecyclerView() {
        return discoveryRecyclerView;
    }

    public void setDiscoveryType(Discovery discoveryType) {
        this.discoveryType = discoveryType;
    }

    public void setTitleIcon(int titleIcon) {
        this.titleIcon = titleIcon;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDiscoveryRecyclerView(RecyclerView discoveryRecyclerView) {
        this.discoveryRecyclerView = discoveryRecyclerView;
    }
}
