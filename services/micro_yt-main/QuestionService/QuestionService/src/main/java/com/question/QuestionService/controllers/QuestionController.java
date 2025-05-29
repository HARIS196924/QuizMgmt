package com.question.QuestionService.controllers;

import com.question.QuestionService.entities.Question;
import com.question.QuestionService.services.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/question")
public class QuestionController {

    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    // Insert Data
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Question question) {
        try {
            return ResponseEntity.ok(questionService.create(question));
        } catch (Exception e) {
            logger.error("Error creating question", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create question: " + e.getMessage());
        }
    }

    // Create Question By Username
    @PostMapping("/username/{username}")
    public ResponseEntity<?> createQuestionByUsername(@PathVariable String username, @RequestBody Question questionRequest) {
        try {
            Question createdQuestion = questionService.createQuestionByUsername(username, questionRequest);
            return new ResponseEntity<>(createdQuestion, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating question by username: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create question for username '" + username + "': " + e.getMessage());
        }
    }

    // Get All Questions
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(questionService.get());
        } catch (Exception e) {
            logger.error("Error fetching all questions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch questions: " + e.getMessage());
        }
    }

    // Fetch Question With Id
    @GetMapping("/{questionId}")
    public ResponseEntity<?> getOne(@PathVariable Long questionId) {
        try {
            return ResponseEntity.ok(questionService.getOne(questionId));
        } catch (Exception e) {
            logger.error("Error fetching question with ID: {}", questionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch question with ID " + questionId + ": " + e.getMessage());
        }
    }

    // Update Question With Id
    @PutMapping("/{questionId}")
    public ResponseEntity<?> update(@PathVariable Long questionId, @RequestBody Question updatedQuestion) {
        try {
            return ResponseEntity.ok(questionService.update(questionId, updatedQuestion));
        } catch (Exception e) {
            logger.error("Error updating question with ID: {}", questionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update question with ID " + questionId + ": " + e.getMessage());
        }
    }

    // Delete Question By Id
    @DeleteMapping("/{questionId}")
    public ResponseEntity<?> delete(@PathVariable Long questionId) {
        try {
            questionService.delete(questionId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting question with ID: {}", questionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete question with ID " + questionId + ": " + e.getMessage());
        }
    }

    // Get Questions Of Quiz ID
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<?> getQuestionsOfQuiz(@PathVariable Long quizId) {
        try {
            return ResponseEntity.ok(questionService.getQuestionsOfQuiz(quizId));
        } catch (Exception e) {
            logger.error("Error fetching questions of quiz with ID: {}", quizId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch questions for quiz ID " + quizId + ": " + e.getMessage());
        }
    }

    //Check if a question exists
    @RequestMapping(value = "/{questionId}", method = RequestMethod.HEAD)
    public ResponseEntity<?> exists(@PathVariable Long questionId) {
        try {
            questionService.getOne(questionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Discover available methods
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<?> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAllow(Set.of(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.OPTIONS, HttpMethod.HEAD, HttpMethod.PATCH));
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    //Count questions
    @GetMapping("/count")
    public ResponseEntity<?> getCount() {
        try {
            List<Question> questions = questionService.get();
            return ResponseEntity.ok(Map.of("count", questions.size()));
        } catch (Exception e) {
            logger.error("Error getting question count", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to count questions: " + e.getMessage());
        }
    }

    //Update content only
    @PatchMapping("/{questionId}/content")
    public ResponseEntity<?> updateContent(@PathVariable Long questionId, @RequestBody Map<String, String> body) {
        try {
            Question question = questionService.getOne(questionId);
            question.setContent(body.get("content"));
            return ResponseEntity.ok(questionService.update(questionId, question));
        } catch (Exception e) {
            logger.error("Error updating content for question with ID: {}", questionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update content: " + e.getMessage());
        }
    }
}
