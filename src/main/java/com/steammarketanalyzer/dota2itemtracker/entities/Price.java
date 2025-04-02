package com.steammarketanalyzer.dota2itemtracker.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "prices")
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    private String currency;

    private Double price;

    private Double lowestPrice;

    private Double medianPrice;

    private Long volumeDay;

    private java.sql.Timestamp timestamp;

}
