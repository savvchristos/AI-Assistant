package com.aiassistant.aiassistant.model.llm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LlmRequest {

    @JsonProperty("model")
    private String model;

    @JsonProperty("messages")
    private List<LlmMessage> messages;

    public LlmRequest(String model, List<LlmMessage> messages) {
        this.model = model;
        this.messages = messages;
    }

    public String getModel() {
        return model;
    }

    public List<LlmMessage> getMessages() {
        return messages;
    }
}