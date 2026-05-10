package com.gameshop.product.service;
import com.gameshop.product.dto.GameRequestDTO;
import com.gameshop.product.dto.GameResponseDTO;
import com.gameshop.product.model.AgeRestriction;
import com.gameshop.product.model.Game;
import com.gameshop.product.repository.GameRepository;
import com.gameshop.product.repository.GenreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GameService gameService;

    // helper
    private Game createGameSample(Long id, String title){
        return Game.builder()
                .id(id)
                .title(title)
                .developer("CD PROJECT RED")
                .publisher("CD PROJECT RED")
                .ageRestriction(AgeRestriction.PEGI_18)
                .genres(new HashSet<>())
                .price(new BigDecimal("99.99"))
                .build();
    }


    @Test
    public void getAllGames_ReturnsEmptyList(){
        // GIVEN
        when(gameRepository.findAll()).thenReturn(List.of());

        // WHEN
        List<GameResponseDTO> result = gameService.getAllGames();

        //THEN
        assertThat(result).isEmpty();
    }

    @Test
    public void getAllGames_ReturnsListOfGameResponseDTO(){
        // GIVEN
        List<Game> games = List.of(
                createGameSample(1L, "Game 1"),
                createGameSample(2L, "Game 2")
        );
        when(gameRepository.findAll()).thenReturn(games);

        // WHEN
        List<GameResponseDTO> result = gameService.getAllGames();

        //THEN
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Game 1");
        assertThat(result.get(1).getTitle()).isEqualTo("Game 2");
    }

    @Test
    public void getGameById_WhenGameExists_ReturnsGameResponseDTO(){
        Long game_id = 1L;
        Game game = createGameSample(game_id,"Game");
        // JPRepository findById gives Optional!!
        when(gameRepository.findById(game_id)).thenReturn(Optional.of(game));

        GameResponseDTO result = gameService.getGameById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Game");
        assertThat(result.getId()).isEqualTo(game_id);
    }

    @Test
    public void getGameById_ThrowsRuntimeExceptionError(){
        Long game_id = 1L;
        when(gameRepository.findById(game_id)).thenReturn(Optional.empty());
        String expected_mess =  "Game not found: 1";

        assertThatThrownBy(() -> gameService.getGameById(game_id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(expected_mess);
    }

    @Test
    public void getGameByTitle_WhenGameExists_ReturnsGameResponseDTO(){
        String game_title = "Game 1";
        Game game = createGameSample(1L,"Game 1");
        when(gameRepository.findByTitle(game_title)).thenReturn(Optional.of(game));

        GameResponseDTO result = gameService.getGameByTitle(game_title);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Game 1");
    }

    @Test
    public void getGameByTitle_ThrowsRuntimeException(){
        String game_title = "Game 1";
        when(gameRepository.findByTitle(game_title)).thenReturn(Optional.empty());
        String expected_mess =  "Game not found: Game 1";

        assertThatThrownBy(() -> gameService.getGameByTitle(game_title))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(expected_mess);
    }

    @Test
    public void createGame_SavesGame_ReturnsGameResponseDTO(){
        GameRequestDTO request = GameRequestDTO.builder()
                .title("Witcher 3")
                .build();
        Game gameSaved = createGameSample(1L, "Witcher 3");
        when(gameRepository.save(Mockito.any(Game.class))).thenReturn(gameSaved);

        GameResponseDTO result = gameService.createGame(request);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Witcher 3");
        Mockito.verify(gameRepository).save(Mockito.any(Game.class));
    }

    @Test
    public void updateGame_ReturnsUpdatedGameResponseDTO(){
        Long game_id = 1L;
        Game gameExisting = createGameSample(game_id, "Wither 3");
        GameRequestDTO request = GameRequestDTO.builder()
                .title("Cyberpunk 2077")
                .developer("CD PROJECT RED")
                .publisher("CD PROJECT RED")
                .ageRestriction(AgeRestriction.PEGI_18)
                .genres(new HashSet<>())
                .price(new BigDecimal("99.99"))
                .build();

        when(gameRepository.findById(game_id)).thenReturn(Optional.of(gameExisting));
        // answer based on actual arguments
        when(gameRepository.save(Mockito.any(Game.class))).thenAnswer(input -> input.getArgument(0));

        GameResponseDTO result = gameService.updateGame(game_id, request);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Cyberpunk 2077");
        Mockito.verify(gameRepository).save(Mockito.any(Game.class));
    }

    @Test
    public void updateGame_ThrowsRuntimeException(){
        Long game_id = 1L;
        GameRequestDTO request = GameRequestDTO.builder()
                .title("Cyberpunk 2077")
                .developer("CD PROJECT RED")
                .publisher("CD PROJECT RED")
                .ageRestriction(AgeRestriction.PEGI_18)
                .genres(new HashSet<>())
                .price(new BigDecimal("99.99"))
                .build();

        when(gameRepository.findById(game_id)).thenReturn(Optional.empty());

       assertThatThrownBy(() -> gameService.updateGame(game_id, request))
               .isInstanceOf(RuntimeException.class)
               .hasMessage("Game not found: " + game_id);
    }

    @Test
    public void deleteGame_CallsDeleteById(){
        Long game_id = 1L;
        when(gameRepository.existsById(game_id)).thenReturn(true);

        gameService.deleteGame(game_id);

        Mockito.verify(gameRepository).deleteById(game_id);
    }

    @Test
    public void deleteGame_ThrowsRuntimeException(){
        Long game_id = 1L;
        when(gameRepository.existsById(game_id)).thenReturn(false);
        String expected_mess = "Game not found: " + game_id;

        assertThatThrownBy(() -> gameService.deleteGame(game_id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(expected_mess);
    }

    @Test
    public void searchGameByPhrase_ReturnsList(){
        String phrase = "WiTch";
        Game game1 = createGameSample(1L, "The Witcher 3");
        when(gameRepository.findAllByTitleContainingIgnoreCase(phrase))
                .thenReturn(List.of(game1));

        List<GameResponseDTO> result = gameService.searchGamesByPhrase(phrase);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle()).isEqualTo("The Witcher 3");
        Mockito.verify(gameRepository).findAllByTitleContainingIgnoreCase(phrase);
    }

    @Test
    void searchGamesByPhrase_ReturnsEmptyList() {
        String phrase = "cyberpunk";
        when(gameRepository.findAllByTitleContainingIgnoreCase(phrase))
                .thenReturn(List.of());

        List<GameResponseDTO> result = gameService.searchGamesByPhrase(phrase);

        assertThat(result).isEmpty();
    }
}
