package com.example.SoundMate.dto;

import java.util.List;

public class ConversationRequestDTO {
    private List<String> messages;
    private UserInfoDTO userInfo;

    public ConversationRequestDTO() {}

    public ConversationRequestDTO(List<String> messages, UserInfoDTO userInfo) {
        this.messages = messages;
        this.userInfo = userInfo;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public UserInfoDTO getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoDTO userInfo) {
        this.userInfo = userInfo;
    }
}
