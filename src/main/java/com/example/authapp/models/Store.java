package com.example.authapp.models;

/**
 * Интернет-магазин, который агрегируется приложением.
 */
public class Store {
    private final int id;
    private final String code;           // короткий код: "dns", "citilink", "ozon"
    private final String name;           // отображаемое имя: "DNS", "Ситилинк"
    private final String website;        // главная страница магазина
    private final String logoUrl;        // ссылка на логотип
    private final String searchUrlTemplate; // шаблон поиска, {query} = поисковый запрос

    public Store(int id, String code, String name, String website,
                 String logoUrl, String searchUrlTemplate) {
        this.id = id;
        this.code = code != null ? code : "";
        this.name = name != null ? name : "";
        this.website = website != null ? website : "";
        this.logoUrl = logoUrl != null ? logoUrl : "";
        this.searchUrlTemplate = searchUrlTemplate != null ? searchUrlTemplate : "";
    }

    public int getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getWebsite() { return website; }
    public String getLogoUrl() { return logoUrl; }
    public String getSearchUrlTemplate() { return searchUrlTemplate; }

    /**
     * Возвращает готовую ссылку на страницу поиска в магазине.
     */
    public String buildSearchUrl(String query) {
        if (searchUrlTemplate.isEmpty()) return website;
        String encoded = java.net.URLEncoder.encode(
                query != null ? query : "",
                java.nio.charset.StandardCharsets.UTF_8);
        return searchUrlTemplate.replace("{query}", encoded);
    }

    @Override
    public String toString() { return name; }
}
