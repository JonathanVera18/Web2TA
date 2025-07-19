package com.example.demo.controller;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Quiz;
import com.example.demo.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Quiz Management", description = "APIs for managing quizzes")
public class QuizController {

    private final QuizService quizService;

    @Operation(summary = "Get all quizzes", description = "Retrieve a paginated list of all quizzes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved quizzes",
            content = @Content(schema = @Schema(implementation = Quiz.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping
    @Cacheable(value = "quizzes", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public ResponseEntity<Page<Quiz>> getAllQuizzes(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        log.info("Fetching quizzes with pagination: {}", pageable);
        Page<Quiz> quizzes = quizService.getAllQuizzes(pageable);
        log.debug("Found {} quizzes", quizzes.getTotalElements());
        return ResponseEntity.ok(quizzes);
    }

    @Operation(summary = "Get quiz by ID", description = "Retrieve a specific quiz by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved quiz"),
        @ApiResponse(responseCode = "404", description = "Quiz not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{id}")
    @Cacheable(value = "quizzes", key = "#id")
    public ResponseEntity<Quiz> getQuizById(
            @Parameter(description = "Quiz ID") @PathVariable Long id) {
        log.info("Fetching quiz with ID: {}", id);
        Quiz quiz = quizService.getQuizById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));
        return ResponseEntity.ok(quiz);
    }

    @Operation(summary = "Create new quiz", description = "Create a new quiz")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Quiz created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid quiz data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "quizzes", allEntries = true)
    public ResponseEntity<Quiz> createQuiz(
            @Parameter(description = "Quiz to create") 
            @Valid @RequestBody Quiz quiz) {
        log.info("Creating new quiz: {}", quiz.getTitle());
        Quiz createdQuiz = quizService.createQuiz(quiz);
        log.info("Quiz created with ID: {}", createdQuiz.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuiz);
    }

    @Operation(summary = "Update quiz", description = "Update an existing quiz")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quiz updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid quiz data"),
        @ApiResponse(responseCode = "404", description = "Quiz not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "quizzes", allEntries = true)
    public ResponseEntity<Quiz> updateQuiz(
            @Parameter(description = "Quiz ID") @PathVariable Long id,
            @Parameter(description = "Updated quiz data") 
            @Valid @RequestBody Quiz quiz) {
        log.info("Updating quiz with ID: {}", id);
        quiz.setId(id);
        Quiz updatedQuiz = quizService.updateQuiz(quiz);
        log.info("Quiz updated successfully");
        return ResponseEntity.ok(updatedQuiz);
    }

    @Operation(summary = "Delete quiz", description = "Delete a quiz by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Quiz deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Quiz not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "quizzes", allEntries = true)
    public ResponseEntity<Void> deleteQuiz(
            @Parameter(description = "Quiz ID") @PathVariable Long id) {
        log.info("Deleting quiz with ID: {}", id);
        quizService.deleteQuiz(id);
        log.info("Quiz deleted successfully");
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get quizzes by difficulty", description = "Retrieve quizzes filtered by difficulty level")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved quizzes"),
        @ApiResponse(responseCode = "400", description = "Invalid difficulty level"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/difficulty/{level}")
    @Cacheable(value = "quizzes", key = "'difficulty_' + #level")
    public ResponseEntity<List<Quiz>> getQuizzesByDifficultyLevel(
            @Parameter(description = "Difficulty level") 
            @PathVariable Quiz.DifficultyLevel level) {
        log.info("Fetching quizzes with difficulty level: {}", level);
        List<Quiz> quizzes = quizService.getQuizzesByDifficultyLevel(level);
        log.debug("Found {} quizzes with difficulty {}", quizzes.size(), level);
        return ResponseEntity.ok(quizzes);
    }

    @Operation(summary = "Get quiz by title", description = "Retrieve a quiz by its title")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved quiz"),
        @ApiResponse(responseCode = "404", description = "Quiz not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/title/{title}")
    @Cacheable(value = "quizzes", key = "'title_' + #title")
    public ResponseEntity<Quiz> getQuizByTitle(
            @Parameter(description = "Quiz title") @PathVariable String title) {
        log.info("Fetching quiz with title: {}", title);
        Quiz quiz = quizService.getQuizByTitle(title)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with title: " + title));
        return ResponseEntity.ok(quiz);
    }

    @Operation(summary = "Get active quizzes", description = "Retrieve only active quizzes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved active quizzes"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/active")
    @Cacheable(value = "quizzes", key = "'active'")
    public ResponseEntity<List<Quiz>> getActiveQuizzes() {
        log.info("Fetching active quizzes");
        List<Quiz> quizzes = quizService.getActiveQuizzes();
        log.debug("Found {} active quizzes", quizzes.size());
        return ResponseEntity.ok(quizzes);
    }
}
