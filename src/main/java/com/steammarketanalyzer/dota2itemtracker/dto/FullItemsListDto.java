package com.steammarketanalyzer.dota2itemtracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FullItemsListDto {
    private boolean success;
    private int start;
    private int pagesize;
    private int totalCount;
    @JsonProperty("searchdata")
    private SearchData searchData;
    private List<Item> results;

    @Data
    public static class SearchData {
        private String query;

        @JsonProperty("search_descriptions")
        private boolean searchDescriptions;

        private int total_count;
        private int pagesize;
        private String prefix;

        @JsonProperty("class_prefix")
        private String classPrefix;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private String name;

        @JsonProperty("hash_name")
        private String hashName;

        @JsonProperty("sell_listings")
        private int sellListings;

        @JsonProperty("sell_price")
        private int sellPrice;

        @JsonProperty("sell_price_text")
        private String sellPriceText;

        @JsonProperty("app_icon")
        private String appIcon;

        @JsonProperty("app_name")
        private String appName;

        @JsonProperty("asset_description")
        private AssetDescription assetDescription;

        @JsonProperty("sale_price_text")
        private String salePriceText;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class AssetDescription {
            private int appid;
            private String classid;
            private String instanceid;

            @JsonProperty("background_color")
            private String backgroundColor;

            @JsonProperty("icon_url")
            private String iconUrl;

            private int tradable;
            private String name;

            @JsonProperty("name_color")
            private String nameColor;

            private String type;

            @JsonProperty("market_name")
            private String marketName;

            @JsonProperty("market_hash_name")
            private String marketHashName;

            private int commodity;
        }
    }
}
