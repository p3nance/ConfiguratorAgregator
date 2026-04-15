package com.example.authapp.repositories;

import com.example.authapp.models.Component;
import com.google.gson.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Локальное сохранение выбранной сборки конфигуратора (JSON-файл).
 * Хранит ID выбранных компонентов по категориям.
 */
public class BuildRepository {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path BUILD_FILE = Path.of("build.json");

    /**
     * Сохранить текущую сборку: map категория -> componentId
     */
    public static void saveBuild(Map<String, Integer> build) {
        try {
            JsonObject obj = new JsonObject();
            for (Map.Entry<String, Integer> e : build.entrySet()) {
                if (e.getValue() != null) {
                    obj.addProperty(e.getKey(), e.getValue());
                }
            }
            Files.writeString(BUILD_FILE, GSON.toJson(obj), StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("saveBuild failed: " + e.getMessage());
        }
    }

    /**
     * Загрузить сохранённую сборку. Возвращает map категория -> componentId.
     */
    public static Map<String, Integer> loadBuild() {
        try {
            if (Files.exists(BUILD_FILE)) {
                String json = Files.readString(BUILD_FILE, StandardCharsets.UTF_8);
                JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                Map<String, Integer> build = new LinkedHashMap<>();
                for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
                    build.put(e.getKey(), e.getValue().getAsInt());
                }
                return build;
            }
        } catch (Exception e) {
            System.err.println("loadBuild failed: " + e.getMessage());
        }
        return new LinkedHashMap<>();
    }

    /**
     * Загрузить и разрешить ID в объекты Component.
     */
    public static Map<String, Component> loadBuildComponents() {
        Map<String, Integer> build = loadBuild();
        if (build.isEmpty()) {
            return new LinkedHashMap<>();
        }

        List<Component> all;
        try {
            all = ComponentRepository.loadAll();
        } catch (Exception e) {
            System.err.println("loadBuildComponents failed: " + e.getMessage());
            return new LinkedHashMap<>();
        }

        // Загружаем цены
        Map<Integer, double[]> priceMap;
        try {
            priceMap = ComponentPriceRepository.loadMinPricesMap();
        } catch (Exception e) {
            priceMap = new HashMap<>();
        }
        for (Component c : all) {
            double[] agg = priceMap.get(c.getId());
            if (agg != null) {
                c.setMinPrice(agg[0]);
                c.setOfferCount((int) agg[1]);
            }
        }

        Map<Integer, Component> byId = new HashMap<>();
        for (Component c : all) {
            byId.put(c.getId(), c);
        }

        Map<String, Component> result = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> e : build.entrySet()) {
            Component c = byId.get(e.getValue());
            result.put(e.getKey(), c);
        }
        return result;
    }
}
