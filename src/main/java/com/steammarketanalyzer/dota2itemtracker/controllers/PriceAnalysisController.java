package com.steammarketanalyzer.dota2itemtracker.controllers;

import com.steammarketanalyzer.dota2itemtracker.entities.PriceAnalysis;
import com.steammarketanalyzer.dota2itemtracker.services.PriceAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/price-analysis")
@RequiredArgsConstructor
public class PriceAnalysisController {

    private final PriceAnalysisService priceAnalysisService;

    // Полное обновление таблицы (удаление и повторное заполнение)
    @PostMapping("/full-update")
    public ResponseEntity<String> fullUpdatePriceAnalysis() {
        priceAnalysisService.fullUpdatePriceAnalysis();
        return ResponseEntity.ok("Полное обновление завершено");
    }

    // Обновление цен только для позиций с isArchive = false
    @PostMapping("/update-prices")
    public ResponseEntity<String> updatePricesForNonArchived() {
        priceAnalysisService.updatePriceForNonArchived();
        return ResponseEntity.ok("Цены обновлены для всех позиций с isArchive = false");
    }

    // Получение всех записей
    @GetMapping("/all")
    public ResponseEntity<List<PriceAnalysis>> getAllPriceAnalysis() {
        List<PriceAnalysis> result = priceAnalysisService.getAllPriceAnalysis();
        return ResponseEntity.ok(result);
    }

    // Получение конкретного сета по market_hash_name
    @GetMapping("/{setMarketHashName}")
    public ResponseEntity<PriceAnalysis> getPriceAnalysis(@PathVariable String setMarketHashName) {
        return priceAnalysisService.getPriceAnalysisBySetName(setMarketHashName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

