package com.example.demo.controller;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Quiz;
import com.example.demo.service.QuizService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class QuizControllerTest {

    @Mock
    private QuizService quizService;

    @InjectMocks
    private QuizController quizController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Quiz testQuiz;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(quizController)
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper();

        testQuiz = Quiz.builder()
                .id(1L)
                .title("Test Quiz")
                .description("A test quiz")
                .difficultyLevel(Quiz.DifficultyLevel.EASY)
                .timeLimitMinutes(30)
                .passingScorePercentage(70)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser
    void getAllQuizzes_ShouldReturnQuizzes() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Quiz> quizzes = Arrays.asList(testQuiz);
        Page<Quiz> quizPage = new PageImpl<>(quizzes, pageable, 1);
        when(quizService.getAllQuizzes(any(Pageable.class))).thenReturn(quizPage);

        // When & Then
        mockMvc.perform(get("/api/quizzes")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content[0].title").value("Test Quiz"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(quizService).getAllQuizzes(any(Pageable.class));
    }

    @Test
    @WithMockUser
    void getQuizById_WhenQuizExists_ShouldReturnQuiz() throws Exception {
        // Given
        when(quizService.getQuizById(1L)).thenReturn(Optional.of(testQuiz));

        // When & Then
        mockMvc.perform(get("/api/quizzes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Quiz"))
                .andExpect(jsonPath("$.difficultyLevel").value("EASY"));

        verify(quizService).getQuizById(1L);
    }

    @Test
    @WithMockUser
    void getQuizById_WhenQuizDoesNotExist_ShouldReturn404() throws Exception {
        // Given
        when(quizService.getQuizById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/quizzes/999"))
                .andExpect(status().isNotFound());

        verify(quizService).getQuizById(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createQuiz_WithValidData_ShouldReturnCreatedQuiz() throws Exception {
        // Given
        Quiz newQuiz = Quiz.builder()
                .title("New Quiz")
                .description("A new quiz")
                .difficultyLevel(Quiz.DifficultyLevel.MEDIUM)
                .build();

        when(quizService.createQuiz(any(Quiz.class))).thenReturn(testQuiz);

        // When & Then
        mockMvc.perform(post("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newQuiz)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Quiz"));

        verify(quizService).createQuiz(any(Quiz.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createQuiz_WithInvalidData_ShouldReturn400() throws Exception {
        // Given
        Quiz invalidQuiz = Quiz.builder()
                .title("") // Invalid: empty title
                .difficultyLevel(Quiz.DifficultyLevel.EASY)
                .build();

        // When & Then
        mockMvc.perform(post("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidQuiz)))
                .andExpect(status().isBadRequest());

        verify(quizService, never()).createQuiz(any(Quiz.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateQuiz_WithValidData_ShouldReturnUpdatedQuiz() throws Exception {
        // Given
        Quiz updateQuiz = Quiz.builder()
                .id(1L)
                .title("Updated Quiz")
                .description("Updated description")
                .difficultyLevel(Quiz.DifficultyLevel.HARD)
                .build();

        when(quizService.updateQuiz(any(Quiz.class))).thenReturn(updateQuiz);

        // When & Then
        mockMvc.perform(put("/api/quizzes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateQuiz)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Quiz"))
                .andExpect(jsonPath("$.difficultyLevel").value("HARD"));

        verify(quizService).updateQuiz(any(Quiz.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteQuiz_WhenQuizExists_ShouldReturn204() throws Exception {
        // Given
        doNothing().when(quizService).deleteQuiz(1L);

        // When & Then
        mockMvc.perform(delete("/api/quizzes/1"))
                .andExpect(status().isNoContent());

        verify(quizService).deleteQuiz(1L);
    }

    @Test
    @WithMockUser
    void getQuizzesByDifficultyLevel_ShouldReturnQuizzes() throws Exception {
        // Given
        List<Quiz> quizzes = Arrays.asList(testQuiz);
        when(quizService.getQuizzesByDifficultyLevel(Quiz.DifficultyLevel.EASY))
                .thenReturn(quizzes);

        // When & Then
        mockMvc.perform(get("/api/quizzes/difficulty/EASY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].difficultyLevel").value("EASY"));

        verify(quizService).getQuizzesByDifficultyLevel(Quiz.DifficultyLevel.EASY);
    }

    @Test
    @WithMockUser
    void getQuizByTitle_WhenQuizExists_ShouldReturnQuiz() throws Exception {
        // Given
        when(quizService.getQuizByTitle("Test Quiz"))
                .thenReturn(Optional.of(testQuiz));

        // When & Then
        mockMvc.perform(get("/api/quizzes/title/Test Quiz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Quiz"));

        verify(quizService).getQuizByTitle("Test Quiz");
    }

    @Test
    @WithMockUser
    void getQuizByTitle_WhenQuizDoesNotExist_ShouldReturn404() throws Exception {
        // Given
        when(quizService.getQuizByTitle("Non-existent Quiz"))
                .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/quizzes/title/Non-existent Quiz"))
                .andExpect(status().isNotFound());

        verify(quizService).getQuizByTitle("Non-existent Quiz");
    }

    @Test
    @WithMockUser
    void getActiveQuizzes_ShouldReturnActiveQuizzes() throws Exception {
        // Given
        List<Quiz> activeQuizzes = Arrays.asList(testQuiz);
        when(quizService.getActiveQuizzes()).thenReturn(activeQuizzes);

        // When & Then
        mockMvc.perform(get("/api/quizzes/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isActive").value(true));

        verify(quizService).getActiveQuizzes();
    }

    @Test
    void createQuiz_WithoutAdminRole_ShouldReturn403() throws Exception {
        // Given
        Quiz newQuiz = Quiz.builder()
                .title("New Quiz")
                .difficultyLevel(Quiz.DifficultyLevel.EASY)
                .build();

        // When & Then
        mockMvc.perform(post("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newQuiz)))
                .andExpect(status().isForbidden());

        verify(quizService, never()).createQuiz(any(Quiz.class));
    }

    @Test
    void updateQuiz_WithoutAdminRole_ShouldReturn403() throws Exception {
        // Given
        Quiz updateQuiz = Quiz.builder()
                .id(1L)
                .title("Updated Quiz")
                .difficultyLevel(Quiz.DifficultyLevel.EASY)
                .build();

        // When & Then
        mockMvc.perform(put("/api/quizzes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateQuiz)))
                .andExpect(status().isForbidden());

        verify(quizService, never()).updateQuiz(any(Quiz.class));
    }

    @Test
    void deleteQuiz_WithoutAdminRole_ShouldReturn403() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/quizzes/1"))
                .andExpect(status().isForbidden());

        verify(quizService, never()).deleteQuiz(anyLong());
    }
}