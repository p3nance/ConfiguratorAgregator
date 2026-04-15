package com.example.authapp.models;

import java.time.Instant;

/**
 * Цена конкретного компонента в конкретном магазине.
 * Это кэшированное значение, которое обновляется парсером или вручную.
 */
public class ComponentPrice {
    private final int id;
    private final int componentId;
    private final int storeId;
    private String storeName;      // denormalized для отображения
    private String storeCode;
    private String storeLogoUrl;
    private double price;
    private String productUrl;     // прямая ссылка на страницу товара в магазине
    private String imageUrl;       // URL изображения товара из магазина
    private boolean inStock;
    private Instant updatedAt;

    public ComponentPrice(int id, int componentId, int storeId,
                          double price, String productUrl,
                          boolean inStock, Instant updatedAt) {
        this(id, componentId, storeId, price, productUrl, null, inStock, updatedAt);
    }

    public ComponentPrice(int id, int componentId, int storeId,
                          double price, String productUrl, String imageUrl,
                          boolean inStock, Instant updatedAt) {
        this.id = id;
        this.componentId = componentId;
        this.storeId = storeId;
        this.price = price;
        this.productUrl = productUrl != null ? productUrl : "";
        this.imageUrl = imageUrl != null ? imageUrl : "";
        this.inStock = inStock;
        this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
    }

    public int getId() { return id; }
    public int getComponentId() { return componentId; }
    public int getStoreId() { return storeId; }
    public String getStoreName() { return storeName; }
    public String getStoreCode() { return storeCode; }
    public String getStoreLogoUrl() { return storeLogoUrl; }
    public double getPrice() { return price; }
    public String getProductUrl() { return productUrl; }
    public String getImageUrl() { return imageUrl; }
    public boolean isInStock() { return inStock; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setStoreName(String storeName) { this.storeName = storeName; }
    public void setStoreCode(String storeCode) { this.storeCode = storeCode; }
    public void setStoreLogoUrl(String storeLogoUrl) { this.storeLogoUrl = storeLogoUrl; }
    public void setPrice(double price) { this.price = price; }
    public void setProductUrl(String productUrl) { this.productUrl = productUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
