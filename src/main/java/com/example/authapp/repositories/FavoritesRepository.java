package com.example.authapp.repositories;

import com.example.authapp.models.Component;
import com.google.gson.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Локальное хранение избранных компонентов (JSON-файл).
 * Без авторизации/сервера — просто файл favorites.json рядом с приложением.
 */
public class FavoritesRepository {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FAV_FILE = Path.of("favorites.json");

    private static List<Integer> loadIds() {
        try {
            if (Files.exists(FAV_FILE)) {
                String json = Files.readString(FAV_FILE, StandardCharsets.UTF_8);
                JsonArray arr = JsonParser.parseString(json).getAsJsonArray();
                List<Integer> ids = new ArrayList<>();
                for (JsonElement el : arr) {
                    ids.add(el.getAsInt());
                }
                return ids;
            }
        } catch (Exception e) {
            System.err.println("loadIds failed: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private static void saveIds(List<Integer> ids) {
        try {
            JsonArray arr = new JsonArray();
            for (int id : ids) {
                arr.add(id);
            }
            JsonObject obj = new JsonObject();
            obj.add("favorites", arr);
            Files.writeString(FAV_FILE, GSON.toJson(arr), StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("saveIds failed: " + e.getMessage());
        }
    }

    /**
     * Получить список всех избранных компонентов (загружает ID из файла,
     * затем находит компоненты в общей базе).
     */
    public static List<Component> getFavorites() {
        List<Integer> ids = loadIds();
        List<Component> all;
        try {
            all = ComponentRepository.loadAll();
        } catch (Exception e) {
            System.err.println("getFavorites failed: " + e.getMessage());
            return new ArrayList<>();
        }
        List<Component> favorites = new ArrayList<>();
        for (int id : ids) {
            for (Component c : all) {
                if (c.getId() == id) {
                    favorites.add(c);
                    break;
                }
            }
        }
        return favorites;
    }

    /**
     * Проверить, находится ли компонент в избранном.
     */
    public static boolean isFavorite(int componentId) {
        return loadIds().contains(componentId);
    }

    /**
     * Добавить компонент в избранное.
     */
    public static void addFavorite(int componentId) {
        List<Integer> ids = loadIds();
        if (!ids.contains(componentId)) {
            ids.add(componentId);
            saveIds(ids);
        }
    }

    /**
     * Удалить компонент из избранного.
     */
    public static void removeFavorite(int componentId) {
        List<Integer> ids = loadIds();
        ids.remove(Integer.valueOf(componentId));
        saveIds(ids);
    }

    /**
     * Переключить статус избранного. Возвращает true если стал избранным.
     */
    public static boolean toggleFavorite(int componentId) {
        if (isFavorite(componentId)) {
            removeFavorite(componentId);
            return false;
        } else {
            addFavorite(componentId);
            return true;
        }
    }
}
