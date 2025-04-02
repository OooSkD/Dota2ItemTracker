package com.steammarketanalyzer.dota2itemtracker.repository;

import org.springframework.data.repository.CrudRepository;
import com.steammarketanalyzer.dota2itemtracker.entities.Item;

import java.util.List;

public interface ItemRepository
        extends CrudRepository<Item, Long> {
    Item findByMarketHashName(String marketHashName);

    Item findByClassid(Long classid);

    List<Item> findByNeedUpdateTrue();

    List<Item> findAllByIsSetTrue();

    List<Item> findAllBySetMarketHashName(String marketHashName);

    boolean existsByMarketHashName(String marketHashName);

    Item findByMarketHashNameOrClassid(String marketHashName, Long classid);
}
