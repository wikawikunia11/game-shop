package com.gameshop.product.controller;

import com.gameshop.product.config.SecurityConfig;
import com.gameshop.product.dto.GameResponseDTO;
import com.gameshop.product.model.AgeRestriction;
import com.gameshop.product.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(GameController.class)
@Import(SecurityConfig.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService gameService;


    private GameResponseDTO createResponseDTOSample(){
        return GameResponseDTO.builder()
                .id(1L)
                .title("Cyberpunk 2077")
                .developer("CD PROJECT RED")
                .publisher("CD PROJECT RED")
                .ageRestriction(AgeRestriction.PEGI_18)
                .genres(Set.of("RPG"))
                .price(new BigDecimal("99.99"))
                .build();

    }

    @Test
    void getAllGames_ReturnsList() throws Exception {
        GameResponseDTO game = createResponseDTOSample();
        when(gameService.getAllGames()).thenReturn(List.of(game));

        ResultActions response = mockMvc.perform(get("/api/games"));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Cyberpunk 2077"));
    }

    @Test
    void getAllGames_EmptyList_ReturnsOk() throws Exception {
        when(gameService.getAllGames()).thenReturn(List.of());

        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void getGameByTitle_ReturnsOk() throws Exception{
        String gameTitle = "Cyberpunk 2077";
        GameResponseDTO game = createResponseDTOSample();
        when(gameService.getGameByTitle(gameTitle)).thenReturn(game);

        mockMvc.perform(get("/api/games/search/exact").param("title", gameTitle))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(gameTitle));
    }

    @Test
    void getGameById_ReturnsOk() throws Exception{
        Long id = 1L;
        GameResponseDTO game = createResponseDTOSample();
        when(gameService.getGameById(id)).thenReturn(game);

        mockMvc.perform(get("/api/games/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void searchGamesByPhrase_ReturnsOk() throws Exception {
        String phrase = "Cyber";
        GameResponseDTO game = createResponseDTOSample();
        when(gameService.searchGamesByPhrase(phrase)).thenReturn(List.of(game));

        mockMvc.perform(get("/api/games/search").param("phrase", phrase)) // @RequestParam
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Cyberpunk 2077"));
    }


    // ---------- SECURITY - ADMIN ACCESS ONLY -----------

    @Test
    void createGame_NoAuth_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteGame_NoAuth_ReturnsUnauthorized() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete("/api/games/{id}", id))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateGame_NoAuth_ReturnsUnauthorized() throws Exception {
        Long id = 1L;
        mockMvc.perform(put("/api/games/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------

    @Test
    void getByGenre_ReturnsOk() throws Exception {
        String genre = "RPG";
        GameResponseDTO game = createResponseDTOSample();
        when(gameService.getGamesByGenre(genre)).thenReturn(List.of(game));

        mockMvc.perform(get("/api/games/genre/{genre}", genre))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].genres", hasItems("RPG")));
    }

    @Test
    void getByDeveloper_ReturnsOk() throws Exception {
        String dev = "CD PROJECT RED";
        GameResponseDTO game = createResponseDTOSample();
        when(gameService.getGamesByDeveloper(dev)).thenReturn(List.of(game));

        mockMvc.perform(get("/api/games/developer/{developer}", dev))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].developer").value(dev));
    }

    @Test
    void getReleased_ReturnsOk() throws Exception {
        GameResponseDTO game = createResponseDTOSample();
        when(gameService.getReleasedGames()).thenReturn(List.of(game));

        mockMvc.perform(get("/api/games/released"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getByAllGenres_ReturnsOk() throws Exception {
        List<String> genres = List.of("RPG", "Action");
        GameResponseDTO game = createResponseDTOSample();
        Set<String> genreSet = Set.of("Action", "RPG", "Shooter");
        game.setGenres(genreSet);
        when(gameService.findByAllGenres(genres)).thenReturn(List.of(game));

        mockMvc.perform(get("/api/games/genres").param("genres", "RPG", "Action"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].genres").isNotEmpty())
                .andExpect(jsonPath("$[0].genres", hasItems("RPG", "Action")));
    }


    @Test
    void getByAgeRestriction_ReturnsOk() throws Exception {
        AgeRestriction restriction = AgeRestriction.PEGI_18;
        GameResponseDTO game = createResponseDTOSample();
        when(gameService.getGamesByAgeRestriction(restriction)).thenReturn(List.of(game));

        mockMvc.perform(get("/api/games/age/{restriction}", restriction))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ageRestriction").value("PEGI_18"));
    }


}
