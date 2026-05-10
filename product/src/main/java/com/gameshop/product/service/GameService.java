package com.gameshop.product.service;

import com.gameshop.product.dto.GameRequestDTO;
import com.gameshop.product.dto.GameResponseDTO;
import com.gameshop.product.model.Game;
import com.gameshop.product.model.Genre;
import com.gameshop.product.repository.GameRepository;
import com.gameshop.product.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;

    public static GameResponseDTO toGameResponseDTO(Game game){
        return new GameResponseDTO(
                game.getId(),
                game.getTitle(),
                game.getDescription(),
                game.getPrice(),
                game.getDeveloper(),
                game.getPublisher(),
                game.getReleaseDate(),
                game.getAgeRestriction(),
                game.getGenres()
                        .stream()
                        // lambda replaced - IntelliJ suggestion
                        .map(Genre::getName)
                        .collect(Collectors.toSet())
        );
    }

    public List<GameResponseDTO> getAllGames(){
        List<Game> games = gameRepository.findAll();

        return games.stream()
                .map(GameService::toGameResponseDTO)
                .toList();
    }

    public GameResponseDTO getGameById(Long id) {
        return gameRepository.findById(id)
                .map(GameService::toGameResponseDTO)
                .orElseThrow(() -> new RuntimeException("Game not found: " + id));
    }

    public GameResponseDTO getGameByTitle(String title) {
        return gameRepository.findByTitle(title)
            .map(GameService::toGameResponseDTO)
                .orElseThrow(() -> new RuntimeException("Game not found: " + title));
    }

    public GameResponseDTO createGame(GameRequestDTO gameRequest){
        Game game = new Game();
        game.setTitle(gameRequest.getTitle());
        game.setDescription(gameRequest.getDescription());
        game.setPrice(gameRequest.getPrice());
        game.setDeveloper(gameRequest.getDeveloper());
        game.setPublisher(gameRequest.getPublisher());
        game.setReleaseDate(gameRequest.getReleaseDate());
        game.setAgeRestriction(gameRequest.getAgeRestriction());

        Set<Genre> genres = new HashSet<>(genreRepository.findAllById(gameRequest.getGenres()));
        game.setGenres(genres);
        return toGameResponseDTO(gameRepository.save(game));
    }

    public GameResponseDTO updateGame(Long id, GameRequestDTO gameRequest){
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found: " + id));
        game.setTitle(gameRequest.getTitle());
        game.setDescription(gameRequest.getDescription());
        game.setPrice(gameRequest.getPrice());
        game.setDeveloper(gameRequest.getDeveloper());
        game.setPublisher(gameRequest.getPublisher());
        game.setReleaseDate(gameRequest.getReleaseDate());
        game.setAgeRestriction(gameRequest.getAgeRestriction());

        Set<Genre> genres = new HashSet<>(genreRepository.findAllById(gameRequest.getGenres()));
        game.setGenres(genres);

        return toGameResponseDTO(gameRepository.save(game));
    }

    public void deleteGame(Long id){
        if (!gameRepository.existsById(id)){
            throw new RuntimeException("Game not found: " + id);
        }
        gameRepository.deleteById(id);
    }

    public List<GameResponseDTO> searchGamesByPhrase(String phrase){
         List<Game> games = gameRepository.findAllByTitleContainingIgnoreCase(phrase);

         if(games.isEmpty()){
             throw new RuntimeException("No Game found containing: " + phrase);
         }

        return games.stream()
                .map(GameService::toGameResponseDTO)
                .toList();
    }

}
