package com.example.authapp.repositories;

import com.example.authapp.dto.StoreDTO;
import com.example.authapp.models.Store;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Загрузка справочника магазинов.
 */
public class StoreRepository {
    private static final String TABLE = "stores";

    private static volatile List<Store> cache = null;
    private static volatile Map<Integer, Store> byId = null;

    public static List<Store> loadAll() throws Exception {
        if (cache != null) return cache;
        String url = SupabaseBase.URL + "/rest/v1/" + TABLE + "?select=*&order=name.asc";
        HttpRequest req = SupabaseBase.authed(url).GET().build();
        HttpResponse<String> resp = SupabaseBase.HTTP.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            throw new Exception("Ошибка загрузки магазинов: HTTP " + resp.statusCode() + " — " + resp.body());
        }
        JsonArray arr = JsonParser.parseString(resp.body()).getAsJsonArray();
        List<Store> list = new ArrayList<>();
        Map<Integer, Store> map = new HashMap<>();
        for (int i = 0; i < arr.size(); i++) {
            StoreDTO dto = SupabaseBase.GSON.fromJson(arr.get(i), StoreDTO.class);
            Store s = new Store(dto.id, dto.code, dto.name, dto.website, dto.logoUrl, dto.searchUrlTemplate);
            list.add(s);
            map.put(s.getId(), s);
        }
        cache = list;
        byId = map;
        return list;
    }

    public static Store byId(int id) {
        if (byId == null) {
            try { loadAll(); } catch (Exception ignored) {}
        }
        return byId == null ? null : byId.get(id);
    }

    public static void invalidateCache() {
        cache = null;
        byId = null;
    }
}
