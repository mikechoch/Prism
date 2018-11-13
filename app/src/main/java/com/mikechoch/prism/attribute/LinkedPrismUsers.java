package com.mikechoch.prism.attribute;

import java.util.Collection;
import java.util.LinkedHashMap;

public class LinkedPrismUsers {

    private LinkedHashMap<String, PrismUser> prismUsersHashMap;

    public LinkedPrismUsers() {
        prismUsersHashMap = new LinkedHashMap<>();
    }

    public void addPrismUser(PrismUser prismUser) {
        prismUsersHashMap.put(prismUser.getUid(), prismUser);
    }

    public PrismUser getPrismUser(String prismUserId) {
        return prismUsersHashMap.get(prismUserId);
    }

    public Collection<PrismUser> getPrismUsers() {
        return prismUsersHashMap.values();
    }

    public LinkedHashMap<String, PrismUser> getPrismUsersHashMap() {
        return prismUsersHashMap;
    }
}
