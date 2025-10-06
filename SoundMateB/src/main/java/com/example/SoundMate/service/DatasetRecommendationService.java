package com.example.SoundMate.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DatasetRecommendationService {

    private static final String CSV_PATH = "SoundMateB/src/main/resources/final_dataset.csv";

    private final List<Map<String, String>> musicDataset = new ArrayList<>();

    private final List<String> featureKeys = List.of(
            "acousticness",
            "danceability",
            "energy",
            "valence",
            "speechiness",
            "instrumentalness",
            "liveness",
            "tempo",
            "energy_danceability",
            "acoustic_instrumental_diff",
            "mood_index"
    );

    @PostConstruct
    public void loadDataset() {
        try (CSVReader reader = new CSVReaderBuilder(
                new InputStreamReader(new FileInputStream(CSV_PATH), StandardCharsets.UTF_8))
                .withCSVParser(new CSVParserBuilder()
                        .withSeparator(',')
                        .withQuoteChar('"')
                        .build())
                .build()) {

            List<String[]> rows = reader.readAll();
            String[] headers = rows.get(0);

            int trackNameIndex = Arrays.asList(headers).indexOf("track_name");

            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);

                if (row.length != headers.length) {
                    System.err.println("열 개수 불일치 - line " + (i + 1));
                    continue;
                }

                Map<String, String> rowMap = new HashMap<>();
                for (int j = 0; j < headers.length; j++) {
                    String key = headers[j].trim();
                    String value = row[j].trim();
                    rowMap.put(key, value);
                }

                // track_name 유효성 검사 및 로그 출력
                String trackName = rowMap.getOrDefault("track_name", "").trim();
                if (trackName.isEmpty()) {
                    String artist = rowMap.getOrDefault("artist_name", "(알 수 없음)");
                    String raw = (trackNameIndex >= 0 && trackNameIndex < row.length) ? row[trackNameIndex] : "N/A";
                    System.err.println("[track_name 없음] line " + (i + 1) + ", artist=" + artist + ", 원본문자열=[" + raw + "]");
                }

                musicDataset.add(rowMap);
            }

            System.out.println("데이터셋 로드 완료: " + musicDataset.size() + "곡");

        } catch (IOException | com.opencsv.exceptions.CsvException e) {
            System.err.println("CSV 파일 로딩 오류: " + e.getMessage());
        }
    }



    public List<Map<String, Object>> recommendSongs(Map<String, Object> inputFeatures) {
        enrichWithDerivedFeatures(inputFeatures);

        Set<String> seenTrackIds = new HashSet<>();

        List<Map<String, Object>> fullRecommendations = musicDataset.stream()
            .filter(track -> {
                // 동일한 track_name + artist_name 조합 중복 제거
                String id = track.getOrDefault("track_name", "").trim() + "|" +
                            track.getOrDefault("artist_name", "").trim();
                if (seenTrackIds.contains(id)) return false;
                seenTrackIds.add(id);
                return true;
            })
            .map(track -> {
                double distance = calculateDistance(inputFeatures, new HashMap<>(track));

                double artistPop = parseDouble(track.getOrDefault("artist_popularity", "0"));
                double trackPop = parseDouble(track.getOrDefault("track_popularity", "0"));
                double popularityScore = 0.6 * artistPop + 0.4 * trackPop;
                double normalizedPop = popularityScore / 78.0;

                double adjustedScore = 0.7 * distance + 0.3 * (1.0 - normalizedPop);

                Map<String, Object> song = new HashMap<>();
                song.put("track_name", track.getOrDefault("track_name", "").trim());
                song.put("artist_name", track.getOrDefault("artist_name", "").trim());
                song.put("score", adjustedScore);
                return song;
            })
            .sorted(Comparator.comparingDouble(song -> (double) song.get("score")))
            .limit(200)
            .collect(Collectors.toList());

        // 로그 출력
        /*System.out.println("추천 결과:");
        for (Map<String, Object> song : fullRecommendations) {
            System.out.println("- " + song.get("artist_name") + " - " + song.get("track_name"));
        }*/

        return fullRecommendations.stream()
            .map(song -> {
                Map<String, Object> result = new HashMap<>();
                result.put("track_name", song.get("track_name"));
                result.put("artist_name", song.get("artist_name"));
                return result;
            })
            .collect(Collectors.toList());
    }

    
    

    private void enrichWithDerivedFeatures(Map<String, Object> features) {
        double energy = parseDouble(features.get("energy"));
        double danceability = parseDouble(features.get("danceability"));
        double acousticness = parseDouble(features.get("acousticness"));
        double instrumentalness = parseDouble(features.get("instrumentalness"));
        double valence = parseDouble(features.get("valence"));

        features.put("energy_danceability", energy + danceability);
        features.put("acoustic_instrumental_diff", acousticness - instrumentalness);
        features.put("mood_index", valence + energy);
    }

    private double calculateDistance(Map<String, Object> input, Map<String, Object> track) {
        double sum = 0.0;
        int count = 0;

        for (String key : featureKeys) {
            double inputVal = parseDouble(input.getOrDefault(key, 0.0));
            double trackVal = parseDouble(track.getOrDefault(key, 0.0));

            if ("tempo".equals(key)) {
                inputVal = normalizeTempo(inputVal);
                trackVal = normalizeTempo(trackVal);
            }

            sum += Math.pow(inputVal - trackVal, 2);
            count++;
        }

        return count > 0 ? Math.sqrt(sum / count) : Double.MAX_VALUE;
    }

    private double normalizeTempo(double tempo) {
        return (tempo - 30.0) / (250.0 - 30.0); // 정규화
    }

    private double parseDouble(Object value) {
        if (value == null) return 0.0;
        try {
            return Double.parseDouble(value.toString().trim());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
