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
    public Integer level;
    public String desc;
    public String uid;

    public Cringe() {

    }

    public Cringe(Boolean isPrivate, String author, Integer level, String desc, String uid) {
        this.isPrivate = isPrivate;
        this.author = author;
        this.level = level;
        this.desc = desc;
        this.uid = uid;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("isPrivate", isPrivate);
        result.put("author", author);
        result.put("level", level);
        result.put("desc", desc);
        result.put("uid", uid);

        return result;
    }
}
