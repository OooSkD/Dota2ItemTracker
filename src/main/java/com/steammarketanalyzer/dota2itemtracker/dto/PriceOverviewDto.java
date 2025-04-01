package com.steammarketanalyzer.dota2itemtracker.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class PriceOverviewDto {
    private boolean success;

    @SerializedName("lowest_price")
    private String lowestPrice;

    private String volume;

    @SerializedName("median_price")
    private String medianPrice;
}
