package com.example.authapp.repositories;

import com.example.authapp.dto.ComponentDTO;
import com.example.authapp.models.Component;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Загрузка компонентов из таблицы `components` в Supabase.
 * В отличие от прежнего ProductRepository, здесь НЕТ price/stock —
 * цены живут в отдельной таблице component_prices.
 */
public class ComponentRepository {
    private static final String TABLE = "components";

    public static List<Component> loadAll() throws Exception {
        String url = SupabaseBase.URL + "/rest/v1/" + TABLE + "?select=*&order=category.asc,name.asc";
        HttpRequest req = SupabaseBase.authed(url).GET().build();
        HttpResponse<String> resp = SupabaseBase.HTTP.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            throw new Exception("Ошибка загрузки компонентов: HTTP " + resp.statusCode() + " — " + resp.body());
        }
        return parseArray(resp.body());
    }

    public static Component loadById(int id) throws Exception {
        String url = SupabaseBase.URL + "/rest/v1/" + TABLE + "?id=eq." + id + "&select=*";
        HttpRequest req = SupabaseBase.authed(url).GET().build();
        HttpResponse<String> resp = SupabaseBase.HTTP.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            throw new Exception("Ошибка загрузки компонента: HTTP " + resp.statusCode());
        }
        List<Component> list = parseArray(resp.body());
        return list.isEmpty() ? null : list.get(0);
    }

    private static List<Component> parseArray(String json) {
        JsonArray arr = JsonParser.parseString(json).getAsJsonArray();
        List<Component> out = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            ComponentDTO dto = SupabaseBase.GSON.fromJson(arr.get(i), ComponentDTO.class);
            out.add(new Component(
                    dto.id,
                    dto.name,
                    dto.description,
                    dto.imageUrl,
                    dto.category,
                    dto.manufacturer,
                    dto.specs
            ));
        }
        return out;
    }
}
