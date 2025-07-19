package com.example.demo.repository;

import com.example.demo.model.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByDifficultyLevel(Quiz.DifficultyLevel difficultyLevel);
    
    Optional<Quiz> findByTitleIgnoreCase(String title);
    
    boolean existsByTitleIgnoreCase(String title);
    
    List<Quiz> findByIsActiveTrue();
    
    long countByIsActiveTrue();
    
    @Query("SELECT q FROM Quiz q WHERE q.isActive = true AND q.difficultyLevel = :level")
    List<Quiz> findActiveByDifficultyLevel(@Param("level") Quiz.DifficultyLevel level);
    
    @Query("SELECT q FROM Quiz q WHERE q.title LIKE %:keyword% OR q.description LIKE %:keyword%")
    Page<Quiz> findByTitleOrDescriptionContaining(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT q FROM Quiz q WHERE q.isActive = true AND (q.title LIKE %:keyword% OR q.description LIKE %:keyword%)")
    Page<Quiz> findActiveByTitleOrDescriptionContaining(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.difficultyLevel = :level")
    long countByDifficultyLevel(@Param("level") Quiz.DifficultyLevel level);
    
    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.isActive = true AND q.difficultyLevel = :level")
    long countActiveByDifficultyLevel(@Param("level") Quiz.DifficultyLevel level);
}

