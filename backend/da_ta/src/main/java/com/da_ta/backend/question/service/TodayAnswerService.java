package com.da_ta.backend.question.service;

import com.da_ta.backend.account.user.domain.entity.User;
import com.da_ta.backend.common.domain.Message;
import com.da_ta.backend.common.domain.exception.NotFoundException;
import com.da_ta.backend.question.controller.dto.CreateTodayAnswerRequest;
import com.da_ta.backend.question.controller.dto.TodayAnswerResponse;
import com.da_ta.backend.question.domain.entity.TodayAnswer;
import com.da_ta.backend.question.domain.entity.TodayQuestion;
import com.da_ta.backend.question.domain.repository.TodayAnswerRepository;
import com.da_ta.backend.question.domain.repository.TodayQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.da_ta.backend.common.domain.ErrorCode.TODAY_QUESTION_NOT_FOUND;
import static com.da_ta.backend.common.domain.SuccessCode.*;

@RequiredArgsConstructor
@Service
public class TodayAnswerService {

    private final TodayAnswerRepository todayAnswerRepository;
    private final TodayQuestionRepository todayQuestionRepository;

    public Message createTodayAnswer(CreateTodayAnswerRequest createTodayAnswerRequest, User user) {
        TodayQuestion todayQuestion = todayQuestionRepository.findById(createTodayAnswerRequest.getTodayQuestionId())
                .orElseThrow(() -> new NotFoundException(TODAY_QUESTION_NOT_FOUND));
        TodayAnswer todayAnswer = TodayAnswer.builder()
                .answer(createTodayAnswerRequest.getAnswer())
                .user(user)
                .todayQuestion(todayQuestion)
                .build();
        todayAnswerRepository.save(todayAnswer);
        return new Message(TODAY_ANSWER_CREATED.getMessage());
    }

    public List<TodayAnswerResponse> findTodayAnswers() {
        return todayAnswerRepository.findAll()
                .stream()
                .map(todayAnswer -> TodayAnswerResponse.builder()
                        .todayAnswerId(todayAnswer.getId())
                        .answer(todayAnswer.getAnswer())
                        .userId(todayAnswer.getUser().getId())
                        .todayQuestionId(todayAnswer.getTodayQuestion().getId())
                        .build()
                ).collect(Collectors.toList());
    }
}