package com.example.authapp.repositories;

import com.google.gson.Gson;
import config.Config;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;

/**
 * Общие константы и хелперы для всех репозиториев, работающих с Supabase REST API.
 */
public final class SupabaseBase {
    public static final String URL = Config.SUPABASE_URL;
    public static final String KEY = Config.SUPABASE_ANON_KEY;
    public static final HttpClient HTTP = HttpClient.newHttpClient();
    public static final Gson GSON = new Gson();

    private SupabaseBase() {}

    public static HttpRequest.Builder authed(String fullUrl) {
        return HttpRequest.newBuilder()
                .uri(java.net.URI.create(fullUrl))
                .header("Authorization", "Bearer " + KEY)
                .header("apikey", KEY)
                .header("Content-Type", "application/json");
    }
}
