package com.gameshop.product.controller;

import com.gameshop.product.dto.GameRequestDTO;
import com.gameshop.product.dto.GameResponseDTO;
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
}
