package com.example.fyp_clearcanvas;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RoboflowAPI {
    @POST("/infer/workflows/fyp-ytdnz/detect-count-and-visualize-3")
    Call<RoboflowResponse> analyzeImage(@Body RoboflowRequestBody requestBody);
}

