package com.nisaelek.servisimnerede;

import com.google.firebase.database.Exclude;

public class Takip {
    private String takipEden,takipEdilen,key;

    public Takip() {
        //boş yapıcı metod
    }

    public Takip(String takipEden, String takipEdilen) {
        this.takipEden = takipEden;
        this.takipEdilen = takipEdilen;
    }

    public String getTakipEden() {
        return takipEden;
    }

    public void setTakipEden(String takipEden) {
        this.takipEden = takipEden;
    }

    public String getTakipEdilen() {
        return takipEdilen;
    }

    public void setTakipEdilen(String takipEdilen) {
        this.takipEdilen = takipEdilen;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
