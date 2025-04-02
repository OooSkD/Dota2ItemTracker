package com.steammarketanalyzer.dota2itemtracker.repository;

import com.steammarketanalyzer.dota2itemtracker.entities.PriceAnalysis;
import com.steammarketanalyzer.dota2itemtracker.entities.SalesHistory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PriceAnalysisRepository extends CrudRepository<PriceAnalysis, Long> {
    List<PriceAnalysis> findAll();

    Optional<PriceAnalysis> findBySetMarketHashName(String setMarketHashName);

    List<PriceAnalysis> findByIsArchiveFalse();
}
