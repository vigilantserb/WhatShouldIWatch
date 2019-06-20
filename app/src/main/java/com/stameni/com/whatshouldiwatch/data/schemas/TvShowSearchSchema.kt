package com.stameni.com.whatshouldiwatch.data.schemas


import com.google.gson.annotations.SerializedName

data class TvShowSearchSchema(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<TvShowSchema>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)