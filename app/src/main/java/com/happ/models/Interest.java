package com.happ.models;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.happ.App;
import com.happ.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dante on 7/26/16.
 */
public class Interest extends RealmObject {

    @PrimaryKey
    private String id;
    @SerializedName("parent")
    private String parentId;
    private String title;
    private String color;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getFullTitle() {
        ArrayList<String> result = new ArrayList<>();
        Interest parent = getParent();
        if (parent != null) result.add(parent.title);
        result.add(title);
        return result;
    }

    public String getColor() {
        Interest parent = getParent();
        if (parent != null && parent.color != null) return parent.getColor();
        if (parent == null && this.color == null) {
            return App.getContext().getResources().getString(0+R.color.colorPrimary);
        }
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Interest getParent() {
        Realm realm = Realm.getDefaultInstance();
        Interest parent = null;
        try {
            if (this.parentId == null) throw new Exception("Interest.class > getParent(): This interest has no parent");
            parent = realm.where(Interest.class).equalTo("id", this.parentId).findFirst();
            parent = realm.copyFromRealm(parent);
        } catch (Exception ex) {
            Log.e("MODELS", ex.getLocalizedMessage());
        } finally {
            realm.close();
            return parent;
        }
    }

    public void setParent(Interest interest) {
        Realm realm = Realm.getDefaultInstance();
        try {
            if (interest == null) {
                this.parentId = null;
            } else {
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(interest);
                realm.commitTransaction();
                this.parentId = interest.id;
            }
        } catch (Exception ex) {
            Log.e("MODELS", ex.getLocalizedMessage());
        } finally {
            realm.close();
        }
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
