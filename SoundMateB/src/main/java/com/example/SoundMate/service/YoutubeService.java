package com.example.SoundMate.service;

import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;



@Service
public class YoutubeService {

    @Value("${youtube.api-key}")
    private String apiKey;

    private static final String YOUTUBE_SEARCH_URL =
        "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&maxResults=1&q=%s&key=%s";

    public String searchTopVideoUrl(String keyword) {
        try {
        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        String url = String.format(YOUTUBE_SEARCH_URL, encodedKeyword, apiKey); 

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject json = new JSONObject(response.body());
        String videoId = json.getJSONArray("items")
                             .getJSONObject(0)
                             .getJSONObject("id")
                             .getString("videoId");

        return "https://www.youtube.com/watch?v=" + videoId;

    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
    }
}