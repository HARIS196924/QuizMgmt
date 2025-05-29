package com.question.QuestionService.services.impl;

import com.question.QuestionService.entities.Question;
import com.question.QuestionService.repositories.QuestionRepository;
import com.question.QuestionService.services.QuestionService;
import com.question.QuestionService.services.QuizClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class QuestionServiceImpl implements QuestionService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionServiceImpl.class);

    private final QuestionRepository questionRepository;
    private final QuizClient quizClient;

    public QuestionServiceImpl(QuestionRepository questionRepository, QuizClient quizClient) {
        this.questionRepository = questionRepository;
        this.quizClient = quizClient;
    }

    @Override
    public Question create(Question question) {
        try {
            return questionRepository.save(question);
        } catch (Exception e) {
            logger.error("Error creating question", e);
            throw new RuntimeException("Failed to create question", e);
        }
    }

    @Override
    public List<Question> get() {
        try {
            return questionRepository.findAll();
        } catch (Exception e) {
            logger.error("Error fetching questions", e);
            throw new RuntimeException("Failed to fetch questions", e);
        }
    }

    @Override
    public Question getOne(Long id) {
        try {
            return questionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));
        } catch (Exception e) {
            logger.error("Error fetching question with id: " + id, e);
            throw new RuntimeException("Failed to fetch question with id: " + id, e);
        }
    }

    @Override
    public List<Question> getQuestionsOfQuiz(Long quizId) {
        try {
            return questionRepository.findByQuizId(quizId);
        } catch (Exception e) {
            logger.error("Error fetching questions for quizId: " + quizId, e);
            throw new RuntimeException("Failed to fetch questions for quizId: " + quizId, e);
        }
    }

    @Override
    public Question update(Long id, Question updatedQuestion) {
        try {
            Question existing = getOne(id);
            existing.setQuestion(updatedQuestion.getQuestion());
            existing.setQuizId(updatedQuestion.getQuizId());
            return questionRepository.save(existing);
        } catch (Exception e) {
            logger.error("Error updating question with id: " + id, e);
            throw new RuntimeException("Failed to update question with id: " + id, e);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            Question existing = getOne(id);
            questionRepository.delete(existing);
        } catch (Exception e) {
            logger.error("Error deleting question with id: " + id, e);
            throw new RuntimeException("Failed to delete question with id: " + id, e);
        }
    }

    @Override
    public Question createQuestionByUsername(String username, Question question) {
        try {
            Map<String, Object> quiz = quizClient.getQuizByUsername(username);
            if (quiz == null || quiz.get("id") == null) {
                throw new RuntimeException("Quiz not found or incomplete for username: " + username);
            }

            Long quizId = Long.parseLong(quiz.get("id").toString());

            question.setQuizId(quizId);
            return questionRepository.save(question);
        } catch (Exception e) {
            logger.error("Error creating question by username: " + username, e);
            throw new RuntimeException("Failed to create question for username: " + username, e);
        }
    }
}
