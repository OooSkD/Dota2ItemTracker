package com.steammarketanalyzer.dota2itemtracker.repository;

import com.steammarketanalyzer.dota2itemtracker.entities.Price;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PriceRepository
        extends CrudRepository<Price, Long> {
    List<Price> findByItemId(Long itemId);
    List<Price> findByItemMarketHashName(String marketHashName);
}