package com.example.fyp_clearcanvas;


import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ProductServiceApi {
    @POST("api/v1/product")
    Call<List<Product>> scrapeProduct();
}
