package com.steammarketanalyzer.dota2itemtracker.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.rest.core.annotation.RestResource;

@Entity
@Data
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String marketHashName;

    private Long classid;

    private String type;

    private Boolean isSet = false;

    private String setMarketHashName;

    @Lob
    private String description;

    private Boolean needUpdate = false;
}
