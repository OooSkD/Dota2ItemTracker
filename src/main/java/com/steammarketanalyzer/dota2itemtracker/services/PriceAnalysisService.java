package com.steammarketanalyzer.dota2itemtracker.services;

import com.steammarketanalyzer.dota2itemtracker.entities.Item;
import com.steammarketanalyzer.dota2itemtracker.entities.Price;
import com.steammarketanalyzer.dota2itemtracker.entities.PriceAnalysis;
import com.steammarketanalyzer.dota2itemtracker.repository.ItemRepository;
import com.steammarketanalyzer.dota2itemtracker.repository.PriceAnalysisRepository;
import com.steammarketanalyzer.dota2itemtracker.repository.PriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class PriceAnalysisService {
    private final ItemRepository itemRepository;
    private final PriceRepository priceRepository;
    private final PriceAnalysisRepository priceAnalysisRepository;

    public PriceAnalysisService(ItemRepository itemRepository, PriceRepository priceRepository, PriceAnalysisRepository priceAnalysisRepository) {
        this.itemRepository = itemRepository;
        this.priceRepository = priceRepository;
        this.priceAnalysisRepository = priceAnalysisRepository;
    }

    @Transactional
    public void fullUpdatePriceAnalysis() {
        priceAnalysisRepository.deleteAll(); // Удаляем все записи
        updatePriceAnalysis(); // Заполняем заново
    }

    public void updatePriceAnalysis() {
        List<Item> sets = itemRepository.findAllByIsSetTrue();

        for (Item set : sets) {
            List<Item> itemsInSet = itemRepository.findAllBySetMarketHashName(set.getMarketHashName());

            BigDecimal totalItemPriceLatest = BigDecimal.ZERO;
            BigDecimal totalItemPriceMedian = BigDecimal.ZERO;

            for (Item item : itemsInSet) {
                Price price = priceRepository.findLatestByItem(item);
                if (price != null) {
                    totalItemPriceLatest = totalItemPriceLatest.add(BigDecimal.valueOf(price.getLowestPrice()));
                    totalItemPriceMedian = totalItemPriceMedian.add(BigDecimal.valueOf(price.getMedianPrice()));
                }
            }

            Price setPrice = priceRepository.findLatestByItem(set);
            BigDecimal setPriceLatest = setPrice != null ? BigDecimal.valueOf(setPrice.getLowestPrice()) : BigDecimal.ZERO;
            BigDecimal setPriceMedian = setPrice != null ? BigDecimal.valueOf(setPrice.getMedianPrice()) : BigDecimal.ZERO;

            BigDecimal priceDifferenceLatest = totalItemPriceLatest.subtract(setPriceLatest);
            BigDecimal priceDifferenceMedian = totalItemPriceMedian.subtract(setPriceMedian);

            PriceAnalysis analysis = new PriceAnalysis();
            analysis.setSetMarketHashName(set.getMarketHashName());
            analysis.setItemList(itemsInSet.stream().map(Item::getMarketHashName).collect(Collectors.joining(", ")));
            analysis.setTotalItemPriceLatest(totalItemPriceLatest);
            analysis.setTotalItemPriceMedian(totalItemPriceMedian);
            analysis.setSetPriceLatest(setPriceLatest);
            analysis.setSetPriceMedian(setPriceMedian);
            analysis.setPriceDifferenceLatest(priceDifferenceLatest);
            analysis.setPriceDifferenceMedian(priceDifferenceMedian);
            analysis.setCreatedAt(Timestamp.from(Instant.now()));
            analysis.setArchive(false);

            priceAnalysisRepository.save(analysis);
        }
    }

    // Получение всех записей из таблицы PriceAnalysis
    public List<PriceAnalysis> getAllPriceAnalysis() {
        return priceAnalysisRepository.findAll();
    }

    // Получение записи по market_hash_name сета
    public Optional<PriceAnalysis> getPriceAnalysisBySetName(String setMarketHashName) {
        return priceAnalysisRepository.findBySetMarketHashName(setMarketHashName);
    }

    // Обновление только цен для тех позиций, где isArchive = false
    @Transactional
    public void updatePriceForNonArchived() {
        List<PriceAnalysis> priceAnalysisList = priceAnalysisRepository.findByIsArchiveFalse(); // Получаем записи без архива

        for (PriceAnalysis priceAnalysis : priceAnalysisList) {
            // Получаем актуальные данные из таблицы Price для каждого сета
            List<Price> prices = priceRepository.findByItemMarketHashName(priceAnalysis.getSetMarketHashName());

            // Подсчитываем суммы для set_price
            BigDecimal totalSetPriceLatest = BigDecimal.ZERO;
            BigDecimal totalSetPriceMedian = BigDecimal.ZERO;
            BigDecimal individualItemsPriceLatest = BigDecimal.ZERO;
            BigDecimal individualItemsPriceMedian = BigDecimal.ZERO;

            for (Price price : prices) {
                // Для каждого предмета из сета добавляем цену к суммам
                totalSetPriceLatest = totalSetPriceLatest.add(BigDecimal.valueOf(price.getLowestPrice()));
                totalSetPriceMedian = totalSetPriceMedian.add(BigDecimal.valueOf(price.getMedianPrice()));

                // Для расчета индивидуальных цен
                individualItemsPriceLatest = individualItemsPriceLatest.add(BigDecimal.valueOf(price.getLowestPrice()));
                individualItemsPriceMedian = individualItemsPriceMedian.add(BigDecimal.valueOf(price.getMedianPrice()));
            }

            // Обновляем цены в PriceAnalysis
            priceAnalysis.setSetPriceLatest(totalSetPriceLatest);
            priceAnalysis.setSetPriceMedian(totalSetPriceMedian);

            // Считаем разницу
            priceAnalysis.setPriceDifferenceLatest(individualItemsPriceLatest.subtract(totalSetPriceLatest));
            priceAnalysis.setPriceDifferenceMedian(individualItemsPriceMedian.subtract(totalSetPriceMedian));

            // Сохраняем обновленную запись
            priceAnalysisRepository.save(priceAnalysis);
        }
    }

}
