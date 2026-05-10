package com.gameshop.product.dto;

import com.gameshop.product.model.AgeRestriction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameRequestDTO {
    private String title;
    private String description;
    private BigDecimal price;
    private String developer;
    private String publisher;
    private LocalDate releaseDate;
    private AgeRestriction ageRestriction;
    private Set<Long> genres; // id's
}
