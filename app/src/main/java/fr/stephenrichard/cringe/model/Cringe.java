package fr.stephenrichard.cringe.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thomas on 05/06/16.
 */
@IgnoreExtraProperties
public class Cringe {
    public Boolean isPrivate;
    public String author;
    public String created_at;
    public Integer level;
    public String desc;
    public String uid;
    public Double lng;
    public Double lat;

    public Cringe() {

    }

    public Cringe(Boolean isPrivate, String created_at, String author, Integer level, String desc, String uid, Double lng, Double lat) {
        this.isPrivate = isPrivate;
        this.created_at = created_at;
        this.author = author;
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
        result.put("created_at", created_at);
        result.put("author", author);
        result.put("level", level);
        result.put("desc", desc);
        result.put("uid", uid);
        result.put("lat", lat);
        result.put("lng", lng);

        return result;
    }
}
