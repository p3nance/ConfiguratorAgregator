package com.example.authapp.dto;

import com.google.gson.annotations.SerializedName;

public class ComponentDTO {
    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("description")
    public String description;

    @SerializedName("image_url")
    public String imageUrl;

    @SerializedName("category")
    public String category;

    @SerializedName("manufacturer")
    public String manufacturer;

    @SerializedName("specs")
    public String specs;

    public ComponentDTO() {}
}
