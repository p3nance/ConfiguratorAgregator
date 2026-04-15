package com.example.authapp.dto;

import com.google.gson.annotations.SerializedName;

public class ComponentPriceDTO {
    @SerializedName("id")
    public int id;

    @SerializedName("component_id")
    public int componentId;

    @SerializedName("store_id")
    public int storeId;

    @SerializedName("price")
    public double price;

    @SerializedName("product_url")
    public String productUrl;

    @SerializedName("image_url")
    public String imageUrl;

    @SerializedName("in_stock")
    public boolean inStock;

    @SerializedName("updated_at")
    public String updatedAt;   // ISO-8601 строка от Supabase

    public ComponentPriceDTO() {}
}
