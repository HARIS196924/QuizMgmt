package com.quiz.services.impl;

import com.quiz.entities.Quiz;
import com.quiz.repositories.QuizRepository;
import com.quiz.services.QuestionClient;
import com.quiz.services.QuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizServiceImpl implements QuizService {

    private static final Logger logger = LoggerFactory.getLogger(QuizServiceImpl.class);

    private final QuizRepository quizRepository;
    private final QuestionClient questionClient;

    public QuizServiceImpl(QuizRepository quizRepository, QuestionClient questionClient) {
        this.quizRepository = quizRepository;
        this.questionClient = questionClient;
    }

    @Override
    public Quiz add(Quiz quiz) {
        try {
            Optional<Quiz> existing = quizRepository.findByUsername(quiz.getUsername());
            if (existing.isPresent()) {
                throw new RuntimeException("Quiz already exists with username: " + quiz.getUsername());
            }

            return quizRepository.save(quiz);
        } catch (Exception e) {
            logger.error("Error while adding quiz", e);
            throw new RuntimeException("Failed to add quiz", e);
        }
    }
    @Override
    public List<Quiz> get() {
        try {
            List<Quiz> quizzes = quizRepository.findAll();
            return quizzes.stream().map(quiz -> {
                try {
                    quiz.setQuestions(questionClient.getQuestionOfQuiz(quiz.getId()));
                } catch (Exception e) {
                    logger.warn("Failed to fetch questions for quiz id: {}", quiz.getId(), e);
                    quiz.setQuestions(List.of()); // fallback to empty list
                }
                return quiz;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error while retrieving all quizzes", e);
            throw new RuntimeException("Failed to retrieve quizzes", e);
        }
    }

    @Override
    public Quiz get(Long id) {
        try {
            Quiz quiz = quizRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
            try {
                quiz.setQuestions(questionClient.getQuestionOfQuiz(quiz.getId()));
            } catch (Exception e) {
                logger.warn("Failed to fetch questions for quiz id: {}", quiz.getId(), e);
                quiz.setQuestions(List.of());
            }
            return quiz;
        } catch (Exception e) {
            logger.error("Error while fetching quiz with id: " + id, e);
            throw new RuntimeException("Failed to fetch quiz with id: " + id, e);
        }
    }

    @Override
    public Quiz update(Long id, Quiz updatedQuiz) {
        try {
            Quiz existingQuiz = quizRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
            existingQuiz.setTitle(updatedQuiz.getTitle());
            existingQuiz.setUsername(updatedQuiz.getUsername());
            return quizRepository.save(existingQuiz);
        } catch (Exception e) {
            logger.error("Error while updating quiz with id: " + id, e);
            throw new RuntimeException("Failed to update quiz with id: " + id, e);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            Quiz quiz = quizRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
            quizRepository.delete(quiz);
        } catch (Exception e) {
            logger.error("Error while deleting quiz with id: " + id, e);
            throw new RuntimeException("Failed to delete quiz with id: " + id, e);
        }
    }

    @Override
    public Quiz getByUsername(String username) {
        try {
            Quiz quiz = quizRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Quiz not found for username: " + username));
            try {
                quiz.setQuestions(questionClient.getQuestionOfQuiz(quiz.getId()));
            } catch (Exception e) {
                logger.warn("Failed to fetch questions for quiz with username: {}", username, e);
                quiz.setQuestions(List.of());
            }
            return quiz;
        } catch (Exception e) {
            logger.error("Error while fetching quiz with username: " + username, e);
            throw new RuntimeException("Failed to fetch quiz with username: " + username, e);
        }
    }
}
