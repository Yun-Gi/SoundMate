package com.example.SoundMate.dto;

public class ConversationResponseDTO {
    private String reply;
    private boolean recommend;

    private String emotion;
    private String mood;

    private double acousticness;
    private double danceability;
    private double energy;
    private double valence;
    private double speechiness;
    private double instrumentalness;
    private double liveness;
    private double tempo;

    private String recommendedSong;
    private String artist;
    private String youtubeUrl;

    public ConversationResponseDTO() {}

    public ConversationResponseDTO(String reply, boolean recommend, String emotion, String mood,
                                   double acousticness, double danceability, double energy, double valence,
                                   double speechiness, double instrumentalness, double liveness, double tempo,
                                   String recommendedSong, String artist, String youtubeUrl) {
        this.reply = reply;
        this.recommend = recommend;
        this.emotion = emotion;
        this.mood = mood;
        this.acousticness = acousticness;
        this.danceability = danceability;
        this.energy = energy;
        this.valence = valence;
        this.speechiness = speechiness;
        this.instrumentalness = instrumentalness;
        this.liveness = liveness;
        this.tempo = tempo;
        this.recommendedSong = recommendedSong;
        this.artist = artist;
        this.youtubeUrl = youtubeUrl;
    }

    
    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public boolean isRecommend() {
        return recommend;
    }

    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public double getAcousticness() {
        return acousticness;
    }

    public void setAcousticness(double acousticness) {
        this.acousticness = acousticness;
    }

    public double getDanceability() {
        return danceability;
    }

    public void setDanceability(double danceability) {
        this.danceability = danceability;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public double getValence() {
        return valence;
    }

    public void setValence(double valence) {
        this.valence = valence;
    }

    public double getSpeechiness() {
        return speechiness;
    }

    public void setSpeechiness(double speechiness) {
        this.speechiness = speechiness;
    }

    public double getInstrumentalness() {
        return instrumentalness;
    }

    public void setInstrumentalness(double instrumentalness) {
        this.instrumentalness = instrumentalness;
    }

    public double getLiveness() {
        return liveness;
    }

    public void setLiveness(double liveness) {
        this.liveness = liveness;
    }

    public double getTempo() {
        return tempo;
    }

    public void setTempo(double tempo) {
        this.tempo = tempo;
    }

    public String getRecommendedSong() {
        return recommendedSong;
    }

    public void setRecommendedSong(String recommendedSong) {
        this.recommendedSong = recommendedSong;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }
}
