package com.steammarketanalyzer.dota2itemtracker.repository;

import com.steammarketanalyzer.dota2itemtracker.entities.SalesHistory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SalesHistoryRepository
        extends CrudRepository<SalesHistory, Long> {
    List<SalesHistory> findByItemId(Long itemId);
    List<SalesHistory> findByItemMarketHashName(String marketHashName);
}