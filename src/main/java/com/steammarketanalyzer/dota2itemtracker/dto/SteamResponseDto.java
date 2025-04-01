package com.steammarketanalyzer.dota2itemtracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;


@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Игнорируем неизвестные поля
public class SteamResponseDto {
    private Map<String, ItemInfoDto> result;
}
