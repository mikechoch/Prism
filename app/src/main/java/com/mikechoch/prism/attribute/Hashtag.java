package com.mikechoch.prism.attribute;

public class Hashtag {

    String tagString;
    int indexStart;

    Hashtag(String tagString, int indexStart) {
        this.tagString = tagString;
        this.indexStart = indexStart;
    }

    public String getTagString() {
        return tagString;
    }

    public int getIndexStart() {
        return indexStart;
    }

    public int getIndexEnd() {
        int indexEnd = indexStart + tagString.length() - 2;
        return indexEnd < 0 ? 0 : indexEnd;
    }

    public void setTagString(String tagString) {
        this.tagString = tagString;
    }

    public void setIndexStart(int indexStart) {
        this.indexStart = indexStart;
    }

}
