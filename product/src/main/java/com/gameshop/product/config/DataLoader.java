package com.gameshop.product.config;

import com.gameshop.product.model.Genre;
import com.gameshop.product.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final GenreRepository genreRepository;

    @Override
    public void run(String... args){
       genreRepository.save(new Genre("Action"));
       genreRepository.save(new Genre("Survival"));
       genreRepository.save(new Genre("RPG"));
       genreRepository.save(new Genre("Strategy"));
    }
}
