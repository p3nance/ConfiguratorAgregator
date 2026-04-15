package com.example.authapp.models;

import javafx.beans.property.*;

/**
 * Комплектующее ПК — объект-агрегат. В отличие от прежнего Product,
 * у компонента НЕТ собственной цены: цена берётся из таблицы
 * component_prices по каждому магазину отдельно.
 *
 * Поле minPrice — это кэш минимальной цены по всем магазинам,
 * заполняется сервисом PriceAggregatorService при загрузке.
 */
public class Component {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty description;
    private final StringProperty imageUrl;
    private final StringProperty category;
    private final StringProperty manufacturer;
    private final StringProperty specs;           // сырые характеристики (JSON или key:value)
    private final DoubleProperty  minPrice;       // кэш: минимальная цена среди магазинов
    private final IntegerProperty offerCount;     // сколько магазинов продают

    public Component(int id, String name, String description,
                     String imageUrl, String category, String manufacturer,
                     String specs) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name != null ? name : "");
        this.description = new SimpleStringProperty(description != null ? description : "");
        this.imageUrl = new SimpleStringProperty(imageUrl != null ? imageUrl : "");
        this.category = new SimpleStringProperty(category != null ? category : "");
        this.manufacturer = new SimpleStringProperty(manufacturer != null ? manufacturer : "");
        this.specs = new SimpleStringProperty(specs != null ? specs : "");
        this.minPrice = new SimpleDoubleProperty(0);
        this.offerCount = new SimpleIntegerProperty(0);
    }

    public Component() {
        this(0, "", "", "", "", "", "");
    }

    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getDescription() { return description.get(); }
    public String getImageUrl() { return imageUrl.get(); }
    public String getCategory() { return category.get(); }
    public String getManufacturer() { return manufacturer.get(); }
    public String getSpecs() { return specs.get(); }
    public double getMinPrice() { return minPrice.get(); }
    public int getOfferCount() { return offerCount.get(); }

    public void setMinPrice(double value) { minPrice.set(value); }
    public void setOfferCount(int value) { offerCount.set(value); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty categoryProperty() { return category; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Component)) return false;
        return id.get() == ((Component) o).id.get();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id.get());
    }

    @Override
    public String toString() {
        return getName();
    }
}
