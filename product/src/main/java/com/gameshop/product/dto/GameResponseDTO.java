package com.gameshop.product.dto;

import com.gameshop.product.model.AgeRestriction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class GameResponseDTO {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private String developer;
    private String publisher;
    private LocalDate releaseDate;
    private AgeRestriction ageRestriction;
    private Set<String> genres;
}
