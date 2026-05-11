package com.gameshop.product.controller;

import com.gameshop.product.dto.GameRequestDTO;
import com.gameshop.product.dto.GameResponseDTO;
import com.gameshop.product.model.AgeRestriction;
import com.gameshop.product.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping
    public ResponseEntity<List<GameResponseDTO>> getAllGames(){
        return new ResponseEntity<>(
                gameService.getAllGames(),
                HttpStatus.OK
        );
    }

    // URL .../search?title=a b c - can have whitespaces
    @GetMapping("/search/exact")
    public ResponseEntity<GameResponseDTO> getGameByTitle(@RequestParam String title){
        return new ResponseEntity<>(
          gameService.getGameByTitle(title),
          HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameResponseDTO> getGameById(@PathVariable Long id) {
        return new ResponseEntity<>(
                gameService.getGameById(id),
                HttpStatus.OK
        );
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchGamesByPhrase(@RequestParam String phrase){
        try {
            return new ResponseEntity<>(
                    gameService.searchGamesByPhrase(phrase),
                    HttpStatus.OK
            );
        }catch (RuntimeException e){
            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @PostMapping
    public ResponseEntity<GameResponseDTO> createGame(@RequestBody GameRequestDTO gameRequest){
        return new ResponseEntity<>(
          gameService.createGame(gameRequest),
          HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameResponseDTO> updateGame(
            @PathVariable Long id,
            @RequestBody GameRequestDTO gameRequest) {
        return new ResponseEntity<>(
                gameService.updateGame(id, gameRequest),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return new ResponseEntity<>(
            HttpStatus.NO_CONTENT
        );
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<GameResponseDTO>> getByGenre(@PathVariable String genre) {
        return new ResponseEntity<>(
                gameService.getGamesByGenre(genre),
                HttpStatus.OK
        );
    }

    @GetMapping("/developer/{developer}")
    public ResponseEntity<List<GameResponseDTO>> getByDeveloper(@PathVariable String developer) {
        return new ResponseEntity<>(
                gameService.getGamesByDeveloper(developer),
                HttpStatus.OK
        );
    }

    @GetMapping("/released")
    public ResponseEntity<List<GameResponseDTO>> getReleased() {
        return new ResponseEntity<>(
                gameService.getReleasedGames(),
                HttpStatus.OK
        );
    }

    @GetMapping("/genres")
    public ResponseEntity<List<GameResponseDTO>> getByAllGenres(@RequestParam List<String> genres) {
        return new ResponseEntity<>(
                gameService.findByAllGenres(genres),
                HttpStatus.OK
        );
    }

    @GetMapping("/age/{restriction}")
    public ResponseEntity<List<GameResponseDTO>> getByAgeRestriction(
            @PathVariable AgeRestriction restriction) {
        return new ResponseEntity<>(
                gameService.getGamesByAgeRestriction(restriction),
                HttpStatus.OK
        );
    }
}
