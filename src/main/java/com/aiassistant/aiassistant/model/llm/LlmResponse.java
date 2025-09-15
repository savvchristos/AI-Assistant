package com.aiassistant.aiassistant.model.llm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LlmResponse {

    @JsonProperty("choices")
    private List<Choice> choices;

    public List<Choice> getChoices() {
        return choices;
    }

    public static class Choice {

        @JsonProperty("message")
        private LlmMessage message;

        public LlmMessage getMessage() {
            return message;
        }
    }
}