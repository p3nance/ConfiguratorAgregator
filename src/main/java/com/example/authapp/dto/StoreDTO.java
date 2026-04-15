package com.example.authapp.dto;

import com.google.gson.annotations.SerializedName;

public class StoreDTO {
    @SerializedName("id")
    public int id;

    @SerializedName("code")
    public String code;

    @SerializedName("name")
    public String name;

    @SerializedName("website")
    public String website;

    @SerializedName("logo_url")
    public String logoUrl;

    @SerializedName("search_url_template")
    public String searchUrlTemplate;

    public StoreDTO() {}
}
