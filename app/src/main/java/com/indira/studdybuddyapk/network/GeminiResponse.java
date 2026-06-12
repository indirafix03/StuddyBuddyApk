package com.indira.studdybuddyapk.network;

import java.util.List;

public class GeminiResponse {
    public List<Candidate> candidates;

    public String getResponseText() {
        if (candidates != null && !candidates.isEmpty() &&
                candidates.get(0).content != null &&
                candidates.get(0).content.parts != null) {
            return candidates.get(0).content.parts.get(0).text;
        }
        return "";
    }

    public static class Candidate {
        public Content content;
    }

    public static class Content {
        public List<Part> parts;
    }

    public static class Part {
        public String text;
    }
}
