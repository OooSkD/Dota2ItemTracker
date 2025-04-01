package com.steammarketanalyzer.dota2itemtracker.repository;

import org.springframework.data.repository.CrudRepository;
import com.steammarketanalyzer.dota2itemtracker.entities.Item;

import java.util.List;

public interface ItemRepository
        extends CrudRepository<Item, Long> {
    Item findByMarketHashName(String marketHashName);
    Item findByClassid(Long classid);
    List<Item>  findByNeedUpdateTrue();
    boolean existsByMarketHashName(String marketHashName);
}
