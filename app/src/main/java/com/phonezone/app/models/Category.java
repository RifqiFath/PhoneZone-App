package com.phonezone.app.models;

import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("id")
    private int id;

    @SerializedName("count")
    private int count;

    @SerializedName("description")
    private String description;

    @SerializedName("link")
    private String link;

    @SerializedName("name")
    private String name;

    @SerializedName("slug")
    private String slug;

    @SerializedName("parent")
    private int parent;

    // Getters
    public int getId() { return id; }
    public int getCount() { return count; }
    public String getDescription() { return description; }
    public String getLink() { return link; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public int getParent() { return parent; }
}
