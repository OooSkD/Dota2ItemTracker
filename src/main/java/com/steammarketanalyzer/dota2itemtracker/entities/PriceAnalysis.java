package com.steammarketanalyzer.dota2itemtracker.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

import java.util.Date;

@Entity
@Data
public class PriceAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "set_market_hash_name", nullable = false, unique = true)
    private String setMarketHashName;

    @Column(name = "item_list", nullable = false)
    private String itemList; // Список market_hash_name через запятую

    @Column(name = "total_item_price_latest", nullable = false)
    private BigDecimal totalItemPriceLatest;

    @Column(name = "total_item_price_median", nullable = false)
    private BigDecimal totalItemPriceMedian;

    @Column(name = "set_price_latest", nullable = false)
    private BigDecimal setPriceLatest;

    @Column(name = "set_price_median", nullable = false)
    private BigDecimal setPriceMedian;

    @Column(name = "price_difference_latest", nullable = false)
    private BigDecimal priceDifferenceLatest;

    @Column(name = "price_difference_median", nullable = false)
    private BigDecimal priceDifferenceMedian;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "is_archive", nullable = false)
    private boolean isArchive = false;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
}
