package com.example.authapp.repositories;

import com.example.authapp.dto.ComponentPriceDTO;
import com.example.authapp.models.ComponentPrice;
import com.example.authapp.models.Store;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Работа с таблицей component_prices — кэш цен по каждому (component, store).
 */
public class ComponentPriceRepository {
    private static final String TABLE = "component_prices";

    /**
     * Все цены на конкретный компонент, с подставленной информацией о магазине.
     */
    public static List<ComponentPrice> loadForComponent(int componentId) throws Exception {
        String url = SupabaseBase.URL + "/rest/v1/" + TABLE
                + "?component_id=eq." + componentId
                + "&select=*&order=price.asc";
        HttpRequest req = SupabaseBase.authed(url).GET().build();
        HttpResponse<String> resp = SupabaseBase.HTTP.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            throw new Exception("Ошибка загрузки цен: HTTP " + resp.statusCode() + " — " + resp.body());
        }

        List<ComponentPrice> out = parseArray(resp.body());
        enrichWithStores(out);
        return out;
    }

    /**
     * Загружает минимальные цены для ВСЕХ компонентов одним запросом.
     * Возвращает карту componentId -> (minPrice, offerCount).
     */
    public static Map<Integer, double[]> loadMinPricesMap() throws Exception {
        // Supabase REST не умеет GROUP BY в одном запросе без RPC, поэтому
        // тянем всё и агрегируем на клиенте. Для учебного объёма данных ОК.
        String url = SupabaseBase.URL + "/rest/v1/" + TABLE
                + "?select=component_id,price,in_stock";
        HttpRequest req = SupabaseBase.authed(url).GET().build();
        HttpResponse<String> resp = SupabaseBase.HTTP.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            throw new Exception("Ошибка загрузки цен: HTTP " + resp.statusCode());
        }
        JsonArray arr = JsonParser.parseString(resp.body()).getAsJsonArray();
        Map<Integer, double[]> map = new HashMap<>(); // [0]=min, [1]=count
        for (int i = 0; i < arr.size(); i++) {
            JsonObject obj = arr.get(i).getAsJsonObject();
            int cid = obj.get("component_id").getAsInt();
            double price = obj.get("price").getAsDouble();
            boolean inStock = obj.has("in_stock") && !obj.get("in_stock").isJsonNull()
                    && obj.get("in_stock").getAsBoolean();
            if (!inStock) continue;
            double[] cur = map.computeIfAbsent(cid, k -> new double[]{Double.MAX_VALUE, 0});
            if (price > 0 && price < cur[0]) cur[0] = price;
            cur[1]++;
        }
        // нормализуем: если не нашли — 0
        for (double[] v : map.values()) {
            if (v[0] == Double.MAX_VALUE) v[0] = 0;
        }
        return map;
    }

    /**
     * Upsert (insert on conflict update). Supabase REST поддерживает
     * Prefer: resolution=merge-duplicates по уникальному индексу.
     * Для этого в таблице должен быть UNIQUE(component_id, store_id).
     */
    public static void upsert(int componentId, int storeId,
                              double price, String productUrl, boolean inStock, String imageUrl) throws Exception {
        String url = SupabaseBase.URL + "/rest/v1/" + TABLE + "?on_conflict=component_id,store_id";

        JsonObject body = new JsonObject();
        body.addProperty("component_id", componentId);
        body.addProperty("store_id", storeId);
        body.addProperty("price", price);
        body.addProperty("product_url", productUrl == null ? "" : productUrl);
        body.addProperty("image_url", imageUrl == null ? "" : imageUrl);
        body.addProperty("in_stock", inStock);
        body.addProperty("updated_at", Instant.now().toString());

        HttpRequest req = SupabaseBase.authed(url)
                .header("Prefer", "resolution=merge-duplicates,return=minimal")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> resp = SupabaseBase.HTTP.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 201 && resp.statusCode() != 200 && resp.statusCode() != 204) {
            throw new Exception("Ошибка upsert цены: HTTP " + resp.statusCode() + " — " + resp.body());
        }
    }

    private static List<ComponentPrice> parseArray(String json) {
        JsonArray arr = JsonParser.parseString(json).getAsJsonArray();
        List<ComponentPrice> out = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            ComponentPriceDTO dto = SupabaseBase.GSON.fromJson(arr.get(i), ComponentPriceDTO.class);
            Instant updated;
            try {
                updated = dto.updatedAt != null ? Instant.parse(dto.updatedAt) : Instant.now();
            } catch (Exception e) {
                updated = Instant.now();
            }
            out.add(new ComponentPrice(
                    dto.id, dto.componentId, dto.storeId,
                    dto.price, dto.productUrl, dto.inStock, updated
            ));
        }
        return out;
    }

    private static void enrichWithStores(List<ComponentPrice> prices) {
        for (ComponentPrice cp : prices) {
            Store s = StoreRepository.byId(cp.getStoreId());
            if (s != null) {
                cp.setStoreName(s.getName());
                cp.setStoreCode(s.getCode());
                cp.setStoreLogoUrl(s.getLogoUrl());
            } else {
                cp.setStoreName("Магазин #" + cp.getStoreId());
            }
        }
    }
}
