package com.gameshop.product.repository;

import com.gameshop.product.model.AgeRestriction;
import com.gameshop.product.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByTitle(String title);
    List<Game> findAllByTitleContainingIgnoreCase(String phrase);
    List<Game> findByGenres_NameIgnoreCase(String genre);
    List<Game> findByDeveloperIgnoreCase(String developer);
    List<Game> findByReleaseDateBefore(LocalDate date);
    List<Game> findByAgeRestriction(AgeRestriction ageRestriction);

    //  g2 = g need to remember to count only this games genres !!
    @Query("""
          SELECT g FROM Game g
          WHERE(
              SELECT COUNT(genr) FROM Game g2
              JOIN g2.genres genr
              WHERE g2 = g
              AND genr.name IN :names
          ) = :size
    """)
    // add size param because needs names.size() to validate query
    List<Game> findByAllGenres(@Param("names") List<String> names, @Param("size") long size);
}
