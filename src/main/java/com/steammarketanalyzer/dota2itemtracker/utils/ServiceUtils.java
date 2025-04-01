package com.steammarketanalyzer.dota2itemtracker.utils;

import com.steammarketanalyzer.dota2itemtracker.dto.ItemInfoDto;
import com.steammarketanalyzer.dota2itemtracker.entities.Item;
import com.steammarketanalyzer.dota2itemtracker.entities.Price;
import com.steammarketanalyzer.dota2itemtracker.entities.SalesHistory;

import java.sql.Timestamp;

public class ServiceUtils {
    public static Double parsePriceFromString(String price) {
        if (price == null) {
            return 0.0;
        }
        price = price.replace(",", "");
        return Double.parseDouble(price.substring(1));
    }

    public static Timestamp getNow() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static SalesHistory map(Price price) {
        SalesHistory result = new SalesHistory();
        result.setItem(price.getItem());
        result.setCurrency(price.getCurrency());
        result.setPrice(price.getPrice());
        result.setMedianPrice(price.getMedianPrice());
        result.setLowestPrice(price.getLowestPrice());
        result.setTimestamp(price.getTimestamp());
        result.setVolumeDay(price.getVolumeDay());
        return result;
    }

    public static Item map(ItemInfoDto itemInfoDto) {
        Item result = new Item();
        result.setClassid(Long.parseLong(itemInfoDto.getClassid()));
        result.setName(itemInfoDto.getName());
        result.setMarketHashName(itemInfoDto.getMarketHashName());

        result.setType(getTypeByTag(itemInfoDto));
        if (result.getType() != null) {
            result.setIsSet(result.getType().toLowerCase().contains("set"));
        }
        return result;
    }

    public static String getTypeByTag(ItemInfoDto itemInfoDto) {
        ItemInfoDto.Tag typeTag = itemInfoDto.getTags().values().stream()
                .filter(tag -> tag.getCategory().equals("Type")).findFirst().orElse(null);
        //TODO: добавить критерии, чтобы сохранять только вещи и сеты (пропускать варды и прочее)
        if (typeTag != null) {
            return typeTag.getName().trim();
        }
        return null;
    }
}
