package com.gif.gify_project

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APICall {
    //api.giphy.com/v1/ gifs/search

    @GET("gifs/search?rating=pg-13&api_key=LnFwnlYPWjonUFehY1GksuNnqwfeAZix")
    fun getData(@Query("q") search:String, @Query("offset") pagination: Int): Call<GifHolder?>?
}