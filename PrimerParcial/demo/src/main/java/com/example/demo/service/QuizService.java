package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Quiz;
import com.example.demo.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuizService {

    private final QuizRepository quizRepository;

    @Cacheable(value = "quizzes", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Quiz> getAllQuizzes(Pageable pageable) {
        log.debug("Fetching quizzes with pagination: {}", pageable);
        Page<Quiz> quizzes = quizRepository.findAll(pageable);
        log.info("Retrieved {} quizzes from database", quizzes.getTotalElements());
        return quizzes;
    }

    @Cacheable(value = "quizzes", key = "#id")
    public Optional<Quiz> getQuizById(Long id) {
        log.debug("Fetching quiz with ID: {}", id);
        Optional<Quiz> quiz = quizRepository.findById(id);
        if (quiz.isPresent()) {
            log.debug("Found quiz: {}", quiz.get().getTitle());
        } else {
            log.warn("Quiz not found with ID: {}", id);
        }
        return quiz;
    }

    @CacheEvict(value = "quizzes", allEntries = true)
    public Quiz createQuiz(Quiz quiz) {
        log.info("Creating new quiz: {}", quiz.getTitle());
        
        // Set default values
        quiz.setCreatedAt(LocalDateTime.now());
        quiz.setUpdatedAt(LocalDateTime.now());
        quiz.setIsActive(true);
        
        Quiz savedQuiz = quizRepository.save(quiz);
        log.info("Quiz created successfully with ID: {}", savedQuiz.getId());
        return savedQuiz;
    }

    @CacheEvict(value = "quizzes", allEntries = true)
    public Quiz updateQuiz(Quiz quiz) {
        log.info("Updating quiz with ID: {}", quiz.getId());
        
        Quiz existingQuiz = quizRepository.findById(quiz.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quiz.getId()));
        
        // Preserve creation date
        quiz.setCreatedAt(existingQuiz.getCreatedAt());
        quiz.setUpdatedAt(LocalDateTime.now());
        
        Quiz updatedQuiz = quizRepository.save(quiz);
        log.info("Quiz updated successfully: {}", updatedQuiz.getTitle());
        return updatedQuiz;
    }

    @CacheEvict(value = "quizzes", allEntries = true)
    public void deleteQuiz(Long id) {
        log.info("Deleting quiz with ID: {}", id);
        
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));
        
        quizRepository.delete(quiz);
        log.info("Quiz deleted successfully: {}", quiz.getTitle());
    }

    @Cacheable(value = "quizzes", key = "'difficulty_' + #level")
    public List<Quiz> getQuizzesByDifficultyLevel(Quiz.DifficultyLevel level) {
        log.debug("Fetching quizzes with difficulty level: {}", level);
        List<Quiz> quizzes = quizRepository.findByDifficultyLevel(level);
        log.info("Found {} quizzes with difficulty level: {}", quizzes.size(), level);
        return quizzes;
    }

    @Cacheable(value = "quizzes", key = "'title_' + #title")
    public Optional<Quiz> getQuizByTitle(String title) {
        log.debug("Fetching quiz with title: {}", title);
        Optional<Quiz> quiz = quizRepository.findByTitleIgnoreCase(title);
        if (quiz.isPresent()) {
            log.debug("Found quiz: {}", quiz.get().getTitle());
        } else {
            log.warn("Quiz not found with title: {}", title);
        }
        return quiz;
    }

    @Cacheable(value = "quizzes", key = "'active'")
    public List<Quiz> getActiveQuizzes() {
        log.debug("Fetching active quizzes");
        List<Quiz> quizzes = quizRepository.findByIsActiveTrue();
        log.info("Found {} active quizzes", quizzes.size());
        return quizzes;
    }

    @Cacheable(value = "quizzes", key = "'count'")
    public long getQuizCount() {
        log.debug("Counting total quizzes");
        long count = quizRepository.count();
        log.debug("Total quizzes count: {}", count);
        return count;
    }

    @Cacheable(value = "quizzes", key = "'active_count'")
    public long getActiveQuizCount() {
        log.debug("Counting active quizzes");
        long count = quizRepository.countByIsActiveTrue();
        log.debug("Active quizzes count: {}", count);
        return count;
    }

    public boolean existsById(Long id) {
        log.debug("Checking if quiz exists with ID: {}", id);
        boolean exists = quizRepository.existsById(id);
        log.debug("Quiz exists: {}", exists);
        return exists;
    }

    public boolean existsByTitle(String title) {
        log.debug("Checking if quiz exists with title: {}", title);
        boolean exists = quizRepository.existsByTitleIgnoreCase(title);
        log.debug("Quiz exists: {}", exists);
        return exists;
    }

    @CacheEvict(value = "quizzes", allEntries = true)
    public void deactivateQuiz(Long id) {
        log.info("Deactivating quiz with ID: {}", id);
        
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));
        
        quiz.setIsActive(false);
        quiz.setUpdatedAt(LocalDateTime.now());
        quizRepository.save(quiz);
        log.info("Quiz deactivated successfully: {}", quiz.getTitle());
    }

    @CacheEvict(value = "quizzes", allEntries = true)
    public void activateQuiz(Long id) {
        log.info("Activating quiz with ID: {}", id);
        
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));
        
        quiz.setIsActive(true);
        quiz.setUpdatedAt(LocalDateTime.now());
        quizRepository.save(quiz);
        log.info("Quiz activated successfully: {}", quiz.getTitle());
    }
}
