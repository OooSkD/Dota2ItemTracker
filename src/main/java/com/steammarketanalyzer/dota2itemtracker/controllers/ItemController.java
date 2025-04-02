package com.steammarketanalyzer.dota2itemtracker.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.steammarketanalyzer.dota2itemtracker.entities.Item;
import com.steammarketanalyzer.dota2itemtracker.entities.Price;
import com.steammarketanalyzer.dota2itemtracker.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/itemManagement")
public class ItemController {

    private final Gson gson = new Gson();

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // Получаем и сохраняем данные о предмете
    @GetMapping("/saveItemData")
    public String saveItemData(@RequestParam String marketHashName, @RequestParam Long classid) {
        itemService.saveItemData(marketHashName, classid);
        return "Data saved successfully!";
    }

    @PostMapping("/updatePriceItem")
    public String updatePriceItem(@RequestParam String marketHashName) throws InterruptedException {
        Price price = itemService.updatePriceItem(marketHashName);
        return gson.toJson(price);
    }

    @PostMapping("/updatePriceForAllItems")
    public String updatePriceForAllItems() throws InterruptedException {
        Integer count = itemService.updatePriceForAllItems();
        return "Обновлено строк" + count;
    }

    @PostMapping("/addItemByClassId")
    public ResponseEntity<Item> addItemByClassId(@RequestParam Long classid) throws JsonProcessingException {
        Item item = itemService.addItemByClassId(classid);
        if (item == null) {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @PostMapping("/updateItemByClassId")
    public ResponseEntity<Item> updateItemByClassId(@RequestParam Long classid) throws JsonProcessingException {
        Item item = itemService.updateItemByClassId(classid);
        if (item == null) {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @PostMapping("/addItemsByCount")
    public ResponseEntity<Item> addItemsByCount(@RequestParam Integer count) throws JsonProcessingException, InterruptedException {
        if (count == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        itemService.addItemsByCount(count);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/addAllItems")
    public ResponseEntity<Item> addAllItems() throws JsonProcessingException {
        Item item = itemService.addAllItems();
        if (item == null) {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
        return new ResponseEntity<>(item, HttpStatus.OK);
    }
}
