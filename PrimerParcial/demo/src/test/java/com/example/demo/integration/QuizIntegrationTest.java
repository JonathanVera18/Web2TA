package com.example.demo.integration;

import com.example.demo.model.Quiz;
import com.example.demo.repository.QuizRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@ActiveProfiles("test")
@Transactional
class QuizIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        
        // Clear database before each test
        quizRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createQuiz_ShouldPersistToDatabase() throws Exception {
        // Given
        Quiz quiz = Quiz.builder()
                .title("Integration Test Quiz")
                .description("A quiz for integration testing")
                .difficultyLevel(Quiz.DifficultyLevel.MEDIUM)
                .timeLimitMinutes(45)
                .passingScorePercentage(75)
                .build();

        // When & Then
        mockMvc.perform(post("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quiz)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Integration Test Quiz"))
                .andExpect(jsonPath("$.difficultyLevel").value("MEDIUM"))
                .andExpect(jsonPath("$.isActive").value(true));

        // Verify it was saved to database
        assert quizRepository.count() == 1;
        Quiz savedQuiz = quizRepository.findAll().get(0);
        assert savedQuiz.getTitle().equals("Integration Test Quiz");
        assert savedQuiz.getDifficultyLevel() == Quiz.DifficultyLevel.MEDIUM;
    }

    @Test
    @WithMockUser
    void getQuizById_ShouldReturnQuizFromDatabase() throws Exception {
        // Given
        Quiz quiz = Quiz.builder()
                .title("Test Quiz")
                .description("Test description")
                .difficultyLevel(Quiz.DifficultyLevel.EASY)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Quiz savedQuiz = quizRepository.save(quiz);

        // When & Then
        mockMvc.perform(get("/api/quizzes/" + savedQuiz.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedQuiz.getId()))
                .andExpect(jsonPath("$.title").value("Test Quiz"))
                .andExpect(jsonPath("$.difficultyLevel").value("EASY"));
    }

    @Test
    @WithMockUser
    void getQuizById_WhenQuizDoesNotExist_ShouldReturn404() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/quizzes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateQuiz_ShouldUpdateInDatabase() throws Exception {
        // Given
        Quiz originalQuiz = Quiz.builder()
                .title("Original Quiz")
                .description("Original description")
                .difficultyLevel(Quiz.DifficultyLevel.EASY)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Quiz savedQuiz = quizRepository.save(originalQuiz);

        Quiz updateData = Quiz.builder()
                .id(savedQuiz.getId())
                .title("Updated Quiz")
                .description("Updated description")
                .difficultyLevel(Quiz.DifficultyLevel.HARD)
                .build();

        // When & Then
        mockMvc.perform(put("/api/quizzes/" + savedQuiz.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Quiz"))
                .andExpect(jsonPath("$.difficultyLevel").value("HARD"));

        // Verify it was updated in database
        Quiz updatedQuiz = quizRepository.findById(savedQuiz.getId()).orElse(null);
        assert updatedQuiz != null;
        assert updatedQuiz.getTitle().equals("Updated Quiz");
        assert updatedQuiz.getDifficultyLevel() == Quiz.DifficultyLevel.HARD;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteQuiz_ShouldRemoveFromDatabase() throws Exception {
        // Given
        Quiz quiz = Quiz.builder()
                .title("Quiz to Delete")
                .description("This quiz will be deleted")
                .difficultyLevel(Quiz.DifficultyLevel.EASY)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Quiz savedQuiz = quizRepository.save(quiz);
        assert quizRepository.count() == 1;

        // When & Then
        mockMvc.perform(delete("/api/quizzes/" + savedQuiz.getId()))
                .andExpect(status().isNoContent());

        // Verify it was deleted from database
        assert quizRepository.count() == 0;
        assert quizRepository.findById(savedQuiz.getId()).isEmpty();
    }

    @Test
    @WithMockUser
    void getQuizzesByDifficultyLevel_ShouldReturnFilteredResults() throws Exception {
        // Given
        Quiz easyQuiz = Quiz.builder()
                .title("Easy Quiz")
                .difficultyLevel(Quiz.DifficultyLevel.EASY)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Quiz hardQuiz = Quiz.builder()
                .title("Hard Quiz")
                .difficultyLevel(Quiz.DifficultyLevel.HARD)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        quizRepository.save(easyQuiz);
        quizRepository.save(hardQuiz);

        // When & Then
        mockMvc.perform(get("/api/quizzes/difficulty/EASY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].difficultyLevel").value("EASY"))
                .andExpect(jsonPath("$[0].title").value("Easy Quiz"));
    }

    @Test
    @WithMockUser
    void getActiveQuizzes_ShouldReturnOnlyActiveQuizzes() throws Exception {
        // Given
        Quiz activeQuiz = Quiz.builder()
                .title("Active Quiz")
                .difficultyLevel(Quiz.DifficultyLevel.EASY)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Quiz inactiveQuiz = Quiz.builder()
                .title("Inactive Quiz")
                .difficultyLevel(Quiz.DifficultyLevel.EASY)
                .isActive(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        quizRepository.save(activeQuiz);
        quizRepository.save(inactiveQuiz);

        // When & Then
        mockMvc.perform(get("/api/quizzes/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isActive").value(true))
                .andExpect(jsonPath("$[0].title").value("Active Quiz"));
    }
}