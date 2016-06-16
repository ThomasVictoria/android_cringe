package fr.stephenrichard.cringe.model;

import android.graphics.Bitmap;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thomas on 05/06/16.
 */
@IgnoreExtraProperties
public class Cringe {
    public Boolean isPrivate;
    public String author;
    public Long createdAt;
    public Integer level;
    public String desc;
    public String uid;
    public Double lng;
    public Double lat;
    public String author_picture;

    public Cringe() {

    }

    public Cringe(Boolean isPrivate, Long created_at, String author, String author_picture, Integer level, String desc, String uid, Double lng, Double lat) {
        this.isPrivate = isPrivate;
        this.createdAt = created_at;
        this.author = author;
        this.author_picture = author_picture;
        this.level = level;
        this.desc = desc;
        this.uid = uid;
        this.lng = lng;
        this.lat = lat;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("isPrivate", isPrivate);
        result.put("createdAt", createdAt);
        result.put("author", author);
        result.put("author_picture", author_picture);
        result.put("level", level);
        result.put("desc", desc);
        result.put("uid", uid);
        result.put("lng", lng);
        result.put("lat", lat);

        return result;
    }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }

    public Long getDateCreation() { return createdAt; }
    public void setDateCreation(Long created_at) { this.createdAt= created_at; }

    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }

    public String getAuthor_picture() { return author_picture; }
    public void setAuthor_picture(String author_picture) { this.author_picture = author_picture; }

}
