package fr.stephenrichard.cringe.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by thomas on 05/06/16.
 */
public class Cringe {

    public Boolean isPrivate;
    public String author;
    public Integer level;
    public String desc;
    public String uid;

    public Cringe() {

    }

    public Cringe(Boolean isPrivate, String author, Integer level, String desc, String uid){
        this.isPrivate  = isPrivate;
        this.author     = author;
        this.level      = level;
        this.desc       = desc;
        this.uid        = uid;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("level", level);
        result.put("desc", desc);
        result.put("isPrivate", isPrivate);

        return result;
    }

}
