package com.steammarketanalyzer.dota2itemtracker.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.steammarketanalyzer.dota2itemtracker.dto.FullItemsListDto;
import com.steammarketanalyzer.dota2itemtracker.dto.ItemInfoDto;
import com.steammarketanalyzer.dota2itemtracker.entities.Item;
import com.steammarketanalyzer.dota2itemtracker.entities.Price;
import com.steammarketanalyzer.dota2itemtracker.entities.SalesHistory;
import com.steammarketanalyzer.dota2itemtracker.repository.ItemRepository;
import com.steammarketanalyzer.dota2itemtracker.repository.PriceRepository;
import com.steammarketanalyzer.dota2itemtracker.repository.SalesHistoryRepository;
import com.steammarketanalyzer.dota2itemtracker.dto.PriceOverviewDto;
import com.steammarketanalyzer.dota2itemtracker.utils.ServiceUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static com.steammarketanalyzer.dota2itemtracker.utils.ServiceUtils.parsePriceFromString;

@Service
public class ItemService {

    private final SteamApiService steamApiService;
    private final ItemRepository itemRepository;
    private final PriceRepository priceRepository;
    private final SalesHistoryRepository salesHistoryRepository;

    public ItemService(SteamApiService steamApiService, ItemRepository itemRepository,
                       PriceRepository priceRepository, SalesHistoryRepository salesHistoryRepository) {
        this.steamApiService = steamApiService;
        this.itemRepository = itemRepository;
        this.priceRepository = priceRepository;
        this.salesHistoryRepository = salesHistoryRepository;
    }

    // Метод для получения данных по предмету и сохранения их в базу
    public void saveItemData(String marketHashName, Long classid) {
        // 1. Получаем цену предмета
        PriceOverviewDto priceData = steamApiService.getItemPriceOverview(marketHashName);

        // 2. Получаем информацию о предмете
        //String itemInfo = steamApiService.getItemInfo(classid);

        // 3. Сохраняем предмет в базу данных, если его там нет
        if (!itemRepository.existsByMarketHashName(marketHashName)) {
            Item item = new Item();
            item.setMarketHashName(marketHashName);
            item.setClassid(classid);
            // TODO: здесь добавим другие необходимые данные из classInfo или priceData

            itemRepository.save(item);
        }

        // 4. Преобразуем полученные данные в объект цены и сохраняем его в базу
        // TODO:  нужно найти существующую и переписать
        Price price = new Price();
        price.setItem(itemRepository.findByMarketHashName(marketHashName));
        price.setCurrency("USD");  // TODO: сделать динамическим в зависимости от ответа
        // TODO:  Здесь надо разобрать priceData и присвоить данные

        priceRepository.save(price);

        // 5. Преобразуем полученные данные о продажах и сохраняем их в SalesHistory
        SalesHistory salesHistory = new SalesHistory();
        salesHistory.setItem(itemRepository.findByMarketHashName(marketHashName));
        salesHistory.setCurrency("USD");  // TODO: сделать динамическим в зависимости от ответа
        // TODO: Здесь тоже нужно разобрать данные

        salesHistoryRepository.save(salesHistory);
    }

    public Price updatePriceItem(String marketHashName) {
        PriceOverviewDto priceData = steamApiService.getItemPriceOverview(marketHashName);
        List<Price> priceByItem = priceRepository.findByItemMarketHashName(marketHashName);
        Price price;
        if (priceByItem == null || priceByItem.isEmpty()) {
            price = new Price();
            price.setItem(itemRepository.findByMarketHashName(marketHashName));
            price.setCurrency("USD");
        } else {
            price = priceByItem.get(0);
            salesHistoryRepository.save(ServiceUtils.map(price));
        }
        price.setLowestPrice(parsePriceFromString(priceData.getLowestPrice()));
        price.setMedianPrice(parsePriceFromString(priceData.getMedianPrice()));
        price.setVolumeDay(priceData.getVolume() == null ? null : Long.parseLong(priceData.getVolume()));
        price.setTimestamp(ServiceUtils.getNow());
        return priceRepository.save(price);
    }

    public Integer updatePriceForAllItems() {
        List<Item> itemsToUpdate = itemRepository.findByNeedUpdateTrue(); // Найти все предметы, которые нужно обновить
        int updatedCount = 0;

        for (Item item : itemsToUpdate) {
            Price updatedPrice = updatePriceItem(item.getMarketHashName()); // Обновить цену предмета
            if (updatedPrice != null) {
                updatedCount++;
            }
            item.setNeedUpdate(false);
            itemRepository.save(item);
        }

        return updatedCount;
    }

    public Item addItemByClassId(Long classid) throws JsonProcessingException {
        Item item = itemRepository.findByClassid(classid);
        if (item != null) {
            return null;
        }
        ItemInfoDto itemData = steamApiService.getItemInfo(classid);
        if (itemData != null) {
            return itemRepository.save(ServiceUtils.map(itemData));
        }
        //TODO: поменять на возвращение ошибки
        return null;
    }

    public Item updateItemByClassId(Long classid) throws JsonProcessingException {
        Item item = itemRepository.findByClassid(classid);
        if (item == null) {
            return null;
        }
        ItemInfoDto itemData = steamApiService.getItemInfo(classid);
        if (itemData != null) {
            item.setType(ServiceUtils.getTypeByTag(itemData));
            if (item.getType() != null) {
                item.setIsSet(item.getType().toLowerCase().contains("set"));
            }
            return itemRepository.save(item);
        }
        //TODO: поменять на возвращение ошибки
        return null;
    }

    public void addItemsByCount(Integer quantity) throws JsonProcessingException {
        int start = 0;
        int count = 100;
        while (start < quantity) {
            if (quantity - start < 100) {
                count = quantity - start;
            }
            FullItemsListDto itemData = steamApiService.getItemsByCount(start, count);

            for (FullItemsListDto.Item item : itemData.getResults()) {
                Long classIdItemFromApi = Long.parseLong(item.getAssetDescription().getClassid());
                Item itemFromDB = itemRepository.findByClassid(classIdItemFromApi);
                if (itemFromDB != null) {
                    continue;
                }
                Item newItem = new Item();
                newItem.setName(item.getName());
                newItem.setMarketHashName(item.getAssetDescription().getMarketHashName());
                newItem.setClassid(classIdItemFromApi);
                setAdditionalInfoItem(newItem);

                boolean isWearableOrBundle = newItem.getType() == null ? false : newItem.getType().toLowerCase().equals("wearable") ||
                        newItem.getType().toLowerCase().equals("bundle");
                if (isWearableOrBundle) {
                    itemRepository.save(newItem);
                }
            }
            start += count;
        }
    }

    public void setAdditionalInfoItem(Item item) throws JsonProcessingException {
        ItemInfoDto itemData = steamApiService.getItemInfo(item.getClassid());
        if (itemData != null) {
            item.setType(ServiceUtils.getTypeByTag(itemData));
            if (item.getType() != null) {
                String type = item.getType().toLowerCase();
                item.setIsSet(type.contains("set") || type.contains("bundle"));
                item.setNeedUpdate(item.getIsSet());
            }

            ItemInfoDto.Description setData = itemData.getDescriptions().values().stream()
                    .filter(description ->
                            description != null
                                    && description.getAppData() != null
                                    && description.getAppData().getIsItemSetName() != null
                                    && description.getAppData().getIsItemSetName().equals("1"))
                    .findFirst().orElse(null);
            if (setData != null) {
                item.setSetMarketHashName(setData.getValue());
                item.setNeedUpdate(true);
            }
        }
    }

    public Item addAllItems() throws JsonProcessingException {
        String itemData = steamApiService.getAllItemsFromMarket();

        return null;
    }
}
