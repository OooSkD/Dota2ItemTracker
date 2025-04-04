package com.steammarketanalyzer.dota2itemtracker.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import com.steammarketanalyzer.dota2itemtracker.dto.FullItemsListDto;
import com.steammarketanalyzer.dota2itemtracker.dto.ItemInfoDto;
import com.steammarketanalyzer.dota2itemtracker.dto.SteamResponseDto;
import com.steammarketanalyzer.dota2itemtracker.dto.PriceOverviewDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class SteamApiService {

    private final Gson gson = new Gson();

    private final WebClient webClient;

    @Value("${steam.api.key}")
    private String steamApiKey;  // ключ для API Steam

    public SteamApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    // Метод для получения подробной информации о предмете
    public ItemInfoDto getItemInfo(Long classid) throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl("https://api.steampowered.com/ISteamEconomy/GetAssetClassInfo/v1/")
                .queryParam("key", steamApiKey)
                .queryParam("appid", 570)
                .queryParam("class_count", 1)
                .queryParam("classid0", classid)
                .toUriString();

        try {
            // Используем WebClient для отправки запроса
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(5)) // Таймаут в 5 секунд
                    .retry(3) // Повторяет запрос 3 раза в случае ошибки
                    .block();  // Ожидаем ответа синхронно


            int startIndex = response.indexOf("\"success\"");
            String modifiedJsonString = response.substring(0, startIndex - 1) + "}}";

            // Теперь парсим эту строку
            ObjectMapper objectMapper = new ObjectMapper();
            SteamResponseDto steamResponseDto = objectMapper.readValue(modifiedJsonString, SteamResponseDto.class);

            if (steamResponseDto.getResult() != null) {
                return steamResponseDto.getResult().values().stream().findFirst().orElse(null);
            }
        } catch (WebClientResponseException e) {
            // Ошибка от сервера Steam (например, 4xx или 5xx)
            System.err.println("Ошибка при запросе к Steam API: " + e.getMessage());
        } catch (Exception e) {
            // Другие ошибки (например, таймаут или проблемы парсинга)
            System.err.println("Не удалось получить данные: " + e.getMessage());
        }

        //TODO: поменять на возвращение ошибки
        return null;
    }

    public FullItemsListDto getItemsByCount(int start, Integer count) throws JsonProcessingException {
        String url = UriComponentsBuilder.fromHttpUrl("https://steamcommunity.com/market/search/render/")
                .queryParam("appid", 570)
                .queryParam("norender", 1)
                .queryParam("count", count)
                .queryParam("start", start)
                .toUriString();

        String response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))  // Устанавливаем таймаут 5 секунд
                .retry(3)  // Повторяем запрос 3 раза при ошибках
                .block();  // Ожидаем ответа синхронно
        System.out.println(response);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response, FullItemsListDto.class);
    }

    public String getAllItemsFromMarket() {
        int start = 0;
        int count = 100; // Максимальное количество элементов в одном запросе

        /*
        // Цикл, пока мы не получим все предметы
        while (true) {
            String url = UriComponentsBuilder.fromHttpUrl("https://steamcommunity.com/market/search/render/")
                    .queryParam("appid", 570)
                    .queryParam("norender", 1)
                    .queryParam("count", count)
                    .queryParam("start", start)
                    .toUriString();

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();  // Ожидаем ответа синхронно
            System.out.println(response);


            // Преобразуем ответ в нужный формат (например, список предметов)
            // Например, парсим JSON
            List<Item> itemsFromApi = parseItemsFromResponse(response);

            if (itemsFromApi.isEmpty()) {
                break; // Если список пуст, значит, мы загрузили все данные
            }

            allItems.addAll(itemsFromApi);
            start += count; // Увеличиваем start для следующего запроса
        }*/

        return "";
    }

}
