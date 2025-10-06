package com.example.SoundMate.service;

import com.example.SoundMate.dto.ChatRequestDTO;
import com.example.SoundMate.dto.ChatResponseDTO;
import com.example.SoundMate.dto.UserInfoDTO;
import com.example.SoundMate.entity.ChatbotData;
import com.example.SoundMate.repository.ChatbotDataRepository;
import com.example.SoundMate.repository.RecommendedSongRepository;
import com.example.SoundMate.dto.ConversationResponseDTO;
import com.example.SoundMate.dto.ConversationRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Autowired
    private YoutubeService youtubeService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DatasetRecommendationService datasetRecommendationService;
    private final List<Map<String, Object>> recentFeatureHistory = new ArrayList<>();
    private final Set<String> recommendedSongs = new HashSet<>();
    private final ChatbotDataRepository chatbotDataRepository;
    private final RecommendedSongRepository recommendedSongRepository;


    public OpenAIService(
    DatasetRecommendationService datasetRecommendationService,
    ChatbotDataRepository chatbotDataRepository,
    RecommendedSongRepository recommendedSongRepository
) {
    this.datasetRecommendationService = datasetRecommendationService;
    this.chatbotDataRepository = chatbotDataRepository;
    this.recommendedSongRepository = recommendedSongRepository;
}
    public Map<String, Object> analyzeText(String userMessage, UserInfoDTO userInfo) {
        String prompt =
            "사용자 정보: 나이 " + userInfo.getAge() +
            ", 성별 " + userInfo.getGender() +
            ", 선호 장르 " + userInfo.getGenre() + ". " +
            "다음 텍스트를 기반으로 음악의 특성을 예측해서 정확한 JSON만 반환해. " +
            "모든 값은 0.0 ~ 1.0 사이 소수 세번째까지 자세히, tempo는 30.0 ~ 250.0 사이의 BPM(비트) 값으로 응답해야 해. " +
            "절대 gpt가 노래 추천해주지말고 분석만 해주세요.\n" +
            "다음 구조를 따라:\n\n" +
            "{\n" +
            "  \"acousticness\": 0.000,\n" +
            "  \"danceability\": 0.000,\n" +
            "  \"energy\": 0.000,\n" +
            "  \"valence\": 0.000,\n" +
            "  \"speechiness\": 0.000,\n" +
            "  \"instrumentalness\": 0.000,\n" +
            "  \"liveness\": 0.000,\n" +
            "  \"tempo\": 120.0\n" +
            "}\n\n" +
            "텍스트: \"" + userMessage + "\"";

        try {
            ChatRequestDTO.Message message = new ChatRequestDTO.Message("user", prompt);
            ChatRequestDTO request = new ChatRequestDTO("gpt-4o", Collections.singletonList(message));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<ChatRequestDTO> entity = new HttpEntity<>(request, headers);
            ResponseEntity<ChatResponseDTO> response = restTemplate.postForEntity(apiUrl, entity, ChatResponseDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String content = response.getBody().getChoices().get(0).getMessage().getContent();
                System.out.println("GPT 응답 (text):\n" + content);
                return parseMusicFeatures(content);
            } else {
                return Map.of("error", "GPT 응답 오류");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "텍스트 분석 중 오류 발생: " + e.getMessage());
        }
    }

    public ConversationResponseDTO analyzeImage(MultipartFile image, UserInfoDTO userInfo) {
        try {
            byte[] imageBytes = image.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            String imageDataUrl = "data:image/png;base64," + base64Image;
            

            String prompt =
                "너는 SoundMate이라고 하는 음악 추천 챗봇이야.\n" +
                "이 대화를 바탕으로 다음 JSON을 생성하세요.\n" +
                "1. 사용자에게 자연스럽게 이어지는 응답 문장 (reply)\n" +
                "2. 감정(emotion)과 분위기(mood)\n" +
                "3. 음악 특성 분석값 (0.0 ~ 1.0 사이 소수 세번째까지 자세히, tempo는 30.0 ~ 250.0 BPM 값으로 응답해야 해. )\n" +
                "4. 지금 이 시점에서 음악 추천이 필요한 경우에만 \"recommend\": true, 필요하지 않다면 false로 설정하세요.\n" +
                "추천이 필요하지 않은 경우도 매우 많습니다. 반드시 상황을 분석해서 판단하세요.\n" +
                "절대 gpt가 노래 추천해주지말고 분석만 해주세요.\n" +
                "절대로 설명하지 말고 JSON만 반환하세요. 예:\n" +
                "{\n" +
                "  \"reply\": \"이런 노래 어때요?\",\n" +
                "  \"recommend\": true,\n" +
                "  \"emotion\": \"(예: 우울, 기쁨 등)\",\n" +
                "  \"mood\": \"(예: 조용한, 활기찬 등)\",\n" +
                "  \"acousticness\": (0.000 ~ 1.0),\n" +
                "  \"danceability\": (0.000 ~ 1.0),\n" +
                "  \"energy\": (0.000 ~ 1.0),\n" +
                "  \"valence\": (0.000 ~ 1.0),\n" +
                "  \"speechiness\": (0.000 ~ 1.0),\n" +
                "  \"instrumentalness\": (0.000 ~ 1.0),\n" +
                "  \"liveness\": (0.000 ~ 1.0),\n" +
                "  \"tempo\": (30.0 ~ 250.0)\n" +
                "}\n";

            Map<String, Object> imagePart = Map.of(
                "type", "image_url",
                "image_url", Map.of("url", imageDataUrl)
            );
            Map<String, Object> textPart = Map.of(
                "type", "text",
                "text", prompt
            );

            Map<String, Object> message = Map.of(
                "role", "user",
                "content", List.of(textPart, imagePart)
            );

            Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o",
                "messages", List.of(message)
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey.replace("Bearer ", ""));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
                Map<String, Object> messageObj = (Map<String, Object>) choices.get(0).get("message");
                String content = (String) messageObj.get("content");
                content = content.replaceAll("```json|```", "").trim();
                System.out.println("GPT 응답 원문:\n" + content);

                // 아래 추가: OpenAI 거절 메시지 대응
                if (content.toLowerCase().contains("i'm sorry") || content.contains("죄송합니다")) {
                    return new ConversationResponseDTO(
                        "이미지로부터 감정과 음악을 분석할 수 없어요. 다른 이미지를 시도해보시겠어요?",
                        false, "알 수 없음", "알 수 없음",
                        0.0, 0.0, 0.0, 0.0,
                        0.0, 0.0, 0.0, 0.0,
                        null, null, null
                    );
                }

                // 기존 JSON 유효성 검사
                if (!content.startsWith("{") || !content.endsWith("}")) {
                    throw new RuntimeException("GPT 응답이 JSON 형식이 아님:\n" + content);
                }

                Map<String, Object> result = objectMapper.readValue(content, Map.class);

                return new ConversationResponseDTO(
                    (String) result.get("reply"),
                    Boolean.TRUE.equals(result.get("recommend")),
                    (String) result.get("emotion"),
                    (String) result.get("mood"),
                    (Double) result.get("acousticness"),
                    (Double) result.get("danceability"),
                    (Double) result.get("energy"),
                    (Double) result.get("valence"),
                    (Double) result.get("speechiness"),
                    (Double) result.get("instrumentalness"),
                    (Double) result.get("liveness"),
                    (Double) result.get("tempo"),
                    null, null, null
                );
            } else {
                throw new RuntimeException("GPT 응답 오류");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("이미지 분석 중 오류 발생: " + e.getMessage());
        }
    }


    public ConversationResponseDTO analyzeTextAndImage(String message, MultipartFile image, UserInfoDTO userInfo) {
        try {
            byte[] imageBytes = image.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            String imageDataUrl = "data:image/png;base64," + base64Image;

            String prompt =
                "사용자 정보: 나이 " + userInfo.getAge() +
                ", 성별 " + userInfo.getGender() +
                ", 선호 장르 " + userInfo.getGenre() + ". " +
                "텍스트와 이미지를 바탕으로 음악 특성을 분석해 JSON만 반환해. " +
                "모든 값은 0.0 ~ 1.0 사이 소수 세번째까지 자세히, tempo는 30.0 ~ 250.0 BPM 값으로 응답해야 해. " +
                "절대 gpt가 노래 추천해주지말고 분석만 해주세요.\n" +
                "구조는 다음과 같아:\n" +
                "{\n" +
                "  \"reply\": \"(응답 문장)\",\n" +
                "  \"recommend\": true,\n" +
                "  \"emotion\": \"(감정)\",\n" +
                "  \"mood\": \"(분위기)\",\n" +
                "  \"acousticness\": 0.000,\n" +
                "  \"danceability\": 0.000,\n" +
                "  \"energy\": 0.000,\n" +
                "  \"valence\": 0.000,\n" +
                "  \"speechiness\": 0.000,\n" +
                "  \"instrumentalness\": 0.000,\n" +
                "  \"liveness\": 0.000,\n" +
                "  \"tempo\": 120.0,\n" +
                "  \"recommendedSong\": \"노래 제목\",\n" +
                "  \"artist\": \"가수\"\n" +
                "}";

             Map<String, Object> imagePart = Map.of(
                "type", "image_url",
                "image_url", Map.of("url", imageDataUrl)
            );
            Map<String, Object> textPart = Map.of(
                "type", "text",
                "text", prompt
            );

            Map<String, Object> chatMessage = Map.of(
                "role", "user",
                "content", List.of(textPart, imagePart)
            );

            Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o",
                "messages", List.of(chatMessage)
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey.replace("Bearer ", ""));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
                Map<String, Object> messageObj = (Map<String, Object>) choices.get(0).get("message");
                String content = (String) messageObj.get("content");
                content = content.replaceAll("```json|```", "").trim();
                System.out.println("GPT 응답 원문:\n" + content);

                if (content.toLowerCase().contains("i'm sorry") || content.contains("죄송합니다")) {
                    return new ConversationResponseDTO(
                        "이미지로부터 감정과 음악을 분석할 수 없어요. 다른 이미지를 시도해보시겠어요?",
                        false, "알 수 없음", "알 수 없음",
                        0.0, 0.0, 0.0, 0.0,
                        0.0, 0.0, 0.0, 0.0,
                        null, null, null
                    );
                }

                if (!content.startsWith("{") || !content.endsWith("}")) {
                    throw new RuntimeException("GPT 응답이 JSON 형식이 아님:\n" + content);
                }

                Map<String, Object> result = objectMapper.readValue(content, Map.class);

                String recommendedSong = (String) result.getOrDefault("recommendedSong", null);
                String artist = (String) result.getOrDefault("artist", null);
                String youtubeUrl = (recommendedSong != null && artist != null) ?
                    youtubeService.searchTopVideoUrl(recommendedSong + " " + artist) : null;

                return new ConversationResponseDTO(
                    (String) result.getOrDefault("reply", "좋은 하루 되세요!"),
                    Boolean.TRUE.equals(result.get("recommend")),
                    (String) result.get("emotion"),
                    (String) result.get("mood"),
                    ((Number) result.get("acousticness")).doubleValue(),
                    ((Number) result.get("danceability")).doubleValue(),
                    ((Number) result.get("energy")).doubleValue(),
                    ((Number) result.get("valence")).doubleValue(),
                    ((Number) result.get("speechiness")).doubleValue(),
                    ((Number) result.get("instrumentalness")).doubleValue(),
                    ((Number) result.get("liveness")).doubleValue(),
                    ((Number) result.get("tempo")).doubleValue(),
                    recommendedSong,
                    artist,
                    youtubeUrl
                );
            } else {
                throw new RuntimeException("GPT 응답 오류");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("텍스트+이미지 분석 중 오류 발생: " + e.getMessage());
        }
    }


    private Map<String, Object> parseMusicFeatures(String content) {
        try {
            content = content.replaceAll("“|”", "\"").trim();

            if (content.startsWith("```json")) {
                content = content.replaceFirst("```json", "").trim();
            }
            if (content.endsWith("```")) {
                content = content.substring(0, content.length() - 3).trim();
            }

            //System.out.println("정제된 content:\n" + content);

            if (content.startsWith("{") && content.endsWith("}")) {
                Map<String, Object> json = objectMapper.readValue(content, Map.class);
                if (json.isEmpty()) {
                    return Map.of("error", "응답은 JSON이지만 필드가 비어있습니다.");
                }
                return json;
            }

            return Map.of("error", "JSON 형식이 아닙니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "파싱 중 오류 발생: " + e.getMessage());
        }
    }
    
     public ConversationResponseDTO analyzeConversation(List<String> messages, UserInfoDTO userInfo) {
        String joinedConversation = String.join("\n", messages);
        String prompt =
            "너는 SoundMate이라고 하는 음악 추천 챗봇이야.\n" +
            "다음은 사용자와의 대화 기록입니다:\n" + joinedConversation + "\n\n" +
            "이 대화를 바탕으로 다음 JSON을 생성하세요.\n" +
            "1. 사용자에게 자연스럽게 이어지는 응답 문장 (reply)\n" +
            "2. 감정(emotion)과 분위기(mood)\n" +
            "3. 음악 특성 분석값 (0.0 ~ 1.0 사이 소수 세번째까지 자세히, tempo는 30.0 ~ 250.0 BPM 값으로 응답해야 해. )\n" +
            "4. 지금 이 시점에서 음악 추천이 필요한 경우에만 \"recommend\": true, 필요하지 않다면 false로 설정하세요.\n" +
            "추천이 필요하지 않은 경우도 매우 많습니다. 반드시 상황을 분석해서 판단하세요.\n" +
            "절대 gpt가 노래 추천해주지말고 분석만 해주세요.\n" +
            "절대로 설명하지 말고 JSON만 반환하세요. 예:\n" +
            "{\n" +
            "  \"reply\": \"이런 노래 어때요?\",\n" +
            "  \"recommend\": true or false,\n" +
            "  \"emotion\": \"(예: 우울, 기쁨 등)\",\n" +
            "  \"mood\": \"(예: 조용한, 활기찬 등)\",\n" +
            "  \"acousticness\": (0.000 ~ 1.0),\n" +
            "  \"danceability\": (0.000 ~ 1.0),\n" +
            "  \"energy\": (0.000 ~ 1.0),\n" +
            "  \"valence\": (0.000 ~ 1.0),\n" +
            "  \"speechiness\": (0.000 ~ 1.0),\n" +
            "  \"instrumentalness\": (0.000 ~ 1.0),\n" +
            "  \"liveness\": (0.000 ~ 1.0),\n" +
            "  \"tempo\": (30.0 ~ 250.0)\n" +
            "}\n";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4o",
                    "messages", List.of(Map.of("role", "user", "content", prompt))
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

            String content = objectMapper.readTree(response.getBody())
                    .get("choices").get(0).get("message").get("content").asText();

            content = content.replaceAll("```json|```", "").trim();
            Map<String, Object> result = objectMapper.readValue(content, Map.class);

            String reply = (String) result.getOrDefault("reply", "좋은 하루 되세요!");
            boolean recommend = Boolean.TRUE.equals(result.get("recommend"));
            String emotion = (String) result.get("emotion");
            String mood = (String) result.get("mood");

            Map<String, Object> extractedFeatures = new HashMap<>();
            for (String key : List.of("acousticness", "danceability", "energy", "valence",
                    "speechiness", "instrumentalness", "liveness", "tempo")) {
                if (result.containsKey(key)) {
                    extractedFeatures.put(key, ((Number) result.get(key)).doubleValue());
                }
            }

            if (recommend) {
                Map<String, Object> weightedAverage = weightedAverageFeatures(recentFeatureHistory);

                for (String key : extractedFeatures.keySet()) {
                    double current = ((Number) extractedFeatures.get(key)).doubleValue();
                    weightedAverage.merge(key, current, (a, b) -> ((Double) a + (Double) b) / 2);
                }

                List<Map<String, Object>> songs = datasetRecommendationService.recommendSongs(weightedAverage);
                Collections.shuffle(songs); // 유사도 순서 고정 방지

                List<Map<String, Object>> filteredSongs = songs.stream()
                    .filter(song -> {
                        String track = song.get("track_name").toString();
                        String artist = song.get("artist_name").toString();
                        boolean exists = recommendedSongRepository.existsByUserIdAndTrackNameAndArtistName(userInfo.getUserId(), track, artist);
                        //System.out.println(" 중복 확인: " + track + " / " + artist + " → " + exists);
                        return !exists;
                    })
                    .collect(Collectors.toList());

                if (filteredSongs.isEmpty()) {
                    return new ConversationResponseDTO(
                            "추천 가능한 곡이 더 이상 없어요!", false, emotion, mood,
                            (Double) extractedFeatures.get("acousticness"),
                            (Double) extractedFeatures.get("danceability"),
                            (Double) extractedFeatures.get("energy"),
                            (Double) extractedFeatures.get("valence"),
                            (Double) extractedFeatures.get("speechiness"),
                            (Double) extractedFeatures.get("instrumentalness"),
                            (Double) extractedFeatures.get("liveness"),
                            (Double) extractedFeatures.get("tempo"),
                            null, null, null
                    );
                }

                Map<String, Object> topSong = filteredSongs.get(new Random().nextInt(filteredSongs.size()));
                String keyword = topSong.get("track_name") + " " + topSong.get("artist_name");
                String youtubeUrl = youtubeService.searchTopVideoUrl(keyword);
                System.out.println("사용자에게 최종 추천된 곡:");
                System.out.println(topSong.get("track_name") + " - " + topSong.get("artist_name"));

                return new ConversationResponseDTO(
                        reply, true, emotion, mood,
                        (Double) extractedFeatures.get("acousticness"),
                        (Double) extractedFeatures.get("danceability"),
                        (Double) extractedFeatures.get("energy"),
                        (Double) extractedFeatures.get("valence"),
                        (Double) extractedFeatures.get("speechiness"),
                        (Double) extractedFeatures.get("instrumentalness"),
                        (Double) extractedFeatures.get("liveness"),
                        (Double) extractedFeatures.get("tempo"),
                        topSong.get("track_name").toString(),
                        topSong.get("artist_name").toString(),
                        youtubeUrl
                );
            } else {
                recentFeatureHistory.add(extractedFeatures);
                return new ConversationResponseDTO(
                        reply, false, emotion, mood,
                        (Double) extractedFeatures.get("acousticness"),
                        (Double) extractedFeatures.get("danceability"),
                        (Double) extractedFeatures.get("energy"),
                        (Double) extractedFeatures.get("valence"),
                        (Double) extractedFeatures.get("speechiness"),
                        (Double) extractedFeatures.get("instrumentalness"),
                        (Double) extractedFeatures.get("liveness"),
                        (Double) extractedFeatures.get("tempo"),
                        null, null, null
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("대화 분석 중 오류 발생", e);
        }
    }

    private Map<String, Object> weightedAverageFeatures(List<Map<String, Object>> featuresList) {
        Map<String, Double> weightedSum = new HashMap<>();
        Map<String, Object> weightedAvg = new HashMap<>();
        int size = featuresList.size();
        double totalWeight = 0;

        for (int i = 0; i < size; i++) {
            Map<String, Object> feature = featuresList.get(i);
            double weight = (i + 1) / (double) size;
            totalWeight += weight;

            for (String key : feature.keySet()) {
                double value = Double.parseDouble(feature.get(key).toString());
                weightedSum.put(key, weightedSum.getOrDefault(key, 0.0) + value * weight);
            }
        }

        for (String key : weightedSum.keySet()) {
            weightedAvg.put(key, weightedSum.get(key) / totalWeight);
        }

        return weightedAvg;
    }
}
