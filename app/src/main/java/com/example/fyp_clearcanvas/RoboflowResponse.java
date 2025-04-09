package com.example.fyp_clearcanvas;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RoboflowResponse {
    @SerializedName("outputs")
    private List<Output> outputs;

    public List<Output> getOutputs() {
        return outputs;
    }

    public static class Output {
        @SerializedName("google_gemini")
        private String googleGemini;

        public String getGoogleGemini() {
            return googleGemini;
        }
    }
}

