package com.gameshop.product.integration;

import com.gameshop.product.dto.GameRequestDTO;
import com.gameshop.product.model.AgeRestriction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // application-test.properties
public class GameControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    // new in: Jackson 3  (default for Spring Boot 4.x.x)
    private JsonMapper jsonMapper;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    private GameRequestDTO createRequestDTOSample() {
        return GameRequestDTO.builder()
                .title("Cyberpunk 2077")
                .description("Open world RPG")
                .price(new BigDecimal("99.99"))
                .developer("CD PROJECT RED")
                .publisher("CD PROJECT RED")
                .ageRestriction(AgeRestriction.PEGI_18)
                .genres(Set.of())
                .build();
    }

    // ------- SECURITY - ADMIN ONLY ACCESS ------

    @Test
    void createGame_NoAuth_ReturnsUnauthorized() throws Exception {
        GameRequestDTO request = createRequestDTOSample();
        mockMvc.perform(post("/api/games")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createGame_AdminAuth_ReturnsCreated() throws Exception {
        GameRequestDTO request = createRequestDTOSample();
        mockMvc.perform(post("/api/games")
                .with(httpBasic(adminUsername, adminPassword))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

}
