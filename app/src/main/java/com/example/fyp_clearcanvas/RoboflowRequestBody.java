package com.example.fyp_clearcanvas;

public class RoboflowRequestBody {
    private String api_key;
    private Inputs inputs;

    public RoboflowRequestBody(String apiKey, String imageUrl) {
        this.api_key = apiKey;
        this.inputs = new Inputs(imageUrl);
    }

    static class Inputs {
        private Image image;

        public Inputs(String imageUrl) {
            this.image = new Image(imageUrl);
        }

        static class Image {
            private String type = "url";
            private String value;

            public Image(String value) {
                this.value = value;
            }
        }
    }
}

