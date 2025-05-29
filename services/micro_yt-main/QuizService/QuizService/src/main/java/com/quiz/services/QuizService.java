package com.quiz.services;

import com.quiz.entities.Quiz;

import java.util.List;

public interface QuizService {

    Quiz add(Quiz quiz);

    List<Quiz> get();

    Quiz get(Long id);

    Quiz update(Long id, Quiz quiz);

    void delete(Long id);

    Quiz getByUsername(String username);
}
