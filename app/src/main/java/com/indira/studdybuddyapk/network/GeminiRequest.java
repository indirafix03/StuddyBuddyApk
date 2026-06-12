package com.indira.studdybuddyapk.network;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class GeminiRequest {
    @SerializedName("contents")
    public List<Content> contents;

    public GeminiRequest(String text) {
        this.contents = new ArrayList<>();
        Content content = new Content();
        content.parts = new ArrayList<>();
        Part part = new Part();
        part.text = text;
        content.parts.add(part);
        this.contents.add(content);
    }

    public static class Content {
        @SerializedName("parts")
        public List<Part> parts;
    }

    public static class Part {
        @SerializedName("text")
        public String text;
    }
}
