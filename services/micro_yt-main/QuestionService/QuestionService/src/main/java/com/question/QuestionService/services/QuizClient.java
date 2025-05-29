package com.question.QuestionService.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "QUIZ-SERVICE")
public interface QuizClient {

    @GetMapping("/quiz/username/{username}")
    Map<String, Object> getQuizByUsername(@PathVariable String username);
}
