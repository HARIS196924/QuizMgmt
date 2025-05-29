package com.quiz.controllers;

import com.quiz.entities.Quiz;
import com.quiz.services.QuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;


@RestController
@RequestMapping("/quiz")
public class QuizController {

    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    // Create
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Quiz quiz) {
        try {
            Quiz savedQuiz = quizService.add(quiz);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedQuiz);
        } catch (Exception e) {
            logger.error("Error creating quiz", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create quiz: " + e.getMessage());
        }
    }

    // Get all quizzes
    @GetMapping
    public ResponseEntity<?> get() {
        try {
            return ResponseEntity.ok(quizService.get());
        } catch (Exception e) {
            logger.error("Error fetching all quizzes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch quizzes: " + e.getMessage());
        }
    }

    // Get one quiz by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        try {
            Quiz quiz = quizService.get(id);
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            logger.error("Error fetching quiz with ID: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch quiz: " + e.getMessage());
        }
    }

    // Get quiz by username
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getQuizByUsername(@PathVariable String username) {
        try {
            Quiz quiz = quizService.getByUsername(username);
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            logger.error("Error fetching quiz by username: " + username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch quiz: " + e.getMessage());
        }
    }

    // Update quiz by ID
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Quiz quiz) {
        try {
            Quiz updatedQuiz = quizService.update(id, quiz);
            return ResponseEntity.ok(updatedQuiz);
        } catch (Exception e) {
            logger.error("Error updating quiz with ID: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update quiz: " + e.getMessage());
        }
    }

    // Partial update quiz title
    @PatchMapping("/{id}/title")
    public ResponseEntity<?> updateTitle(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            Quiz quiz = quizService.get(id);
            quiz.setTitle(payload.get("title"));
            Quiz updated = quizService.update(id, quiz);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Error partially updating title for quiz with ID: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update title: " + e.getMessage());
        }
    }

    // Delete quiz by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            quizService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting quiz with ID: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete quiz: " + e.getMessage());
        }
    }

    // Check if quiz exists
    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<?> exists(@PathVariable Long id) {
        try {
            quizService.get(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // List supported methods
    @RequestMapping(value = "", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAllow(Set.of(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.DELETE, HttpMethod.OPTIONS, HttpMethod.HEAD));
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    // Get total number of quizzes
    @GetMapping("/count")
    public ResponseEntity<?> getCount() {
        try {
            List<Quiz> quizzes = quizService.get();
            return ResponseEntity.ok(Map.of("count", quizzes.size()));
        } catch (Exception e) {
            logger.error("Error getting quiz count", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to count quizzes: " + e.getMessage());
        }
    }
}
