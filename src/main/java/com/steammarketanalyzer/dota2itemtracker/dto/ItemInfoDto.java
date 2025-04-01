package com.steammarketanalyzer.dota2itemtracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Игнорируем неизвестные поля
public class ItemInfoDto {
    @JsonProperty("icon_url")
    private String iconUrl;

    @JsonProperty("icon_url_large")
    private String iconUrlLarge;

    @JsonProperty("icon_drag_url")
    private String iconDragUrl;

    private String name;

    @JsonProperty("market_hash_name")
    private String marketHashName;

    @JsonProperty("market_name")
    private String marketName;

    @JsonProperty("name_color")
    private String nameColor;

    private String type;
    private String tradable;
    private String marketable;
    private String commodity;

    @JsonProperty("market_tradable_restriction")
    private String marketTradableRestriction;

    @JsonProperty("market_marketable_restriction")
    private String marketMarketableRestriction;

    @JsonProperty("background_color")
    private String backgroundColor;

    @JsonProperty("owner_descriptions")
    private String ownerDescriptions;

    private Map<String, Description> descriptions;

    private Map<String, Tag> tags;
    private String classid;

    @Data
    public static class Tag {
        @JsonProperty("internal_name")
        private String internalName;

        private String name;
        private String category;
        private String color;

        @JsonProperty("category_name")
        private String categoryName;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Description {
        private String type;
        private String value;
        private String color;

        @JsonProperty("name")
        private String attributeName;

        @JsonProperty("app_data")
        @JsonSetter(nulls = Nulls.SKIP)
        private AppData appData; // Может быть строкой или объектом

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public class AppData {
            public AppData() {}

            @JsonProperty("def_index")
            private String defIndex;

            @JsonProperty("is_itemset_name")
            private String isItemSetName;
        }
    }
}

