package com.da_ta.backend.letter.controller;

import com.da_ta.backend.account.jwt.JwtTokenProvider;
import com.da_ta.backend.common.domain.Message;
import com.da_ta.backend.letter.controller.dto.*;
import com.da_ta.backend.letter.controller.dto.CheckImageLetterResponse;
import com.da_ta.backend.letter.service.LetterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/letters")
public class LetterController {

    private final LetterService letterService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/1")
    public ResponseEntity<Message> createTextLetter(@RequestHeader(AUTHORIZATION) String token,
                                                    @RequestBody TextLetterCreateRequest textLetterCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(letterService.createTextLetter(jwtTokenProvider.findUserByToken(token), textLetterCreateRequest));
    }

    @PostMapping("/2")
    public ResponseEntity<Message> createImageLetter(@RequestHeader(AUTHORIZATION) String token,
                                                     @RequestBody ImageLetterCreateRequest imageLetterCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(letterService.createImageLetter(jwtTokenProvider.findUserByToken(token), imageLetterCreateRequest));
    }

    @GetMapping("/count")
    public ResponseEntity<CountFloatedLetterResponse> countFloatedLetter() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(letterService.countFloatedLetter());
    }

    @GetMapping()
    public ResponseEntity<ReceiveFloatedLetterResponse> receiveFloatedLetter(@RequestHeader(AUTHORIZATION) String token) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(letterService.receiveFloatedLetter(jwtTokenProvider.findUserByToken(token)));
    }

    @PutMapping("/{letter_id}")
    public ResponseEntity<Message> refloatLetter(@RequestHeader(AUTHORIZATION) String token,
                                                 @PathVariable("letter_id") Long letterId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(letterService.updateFloatedLetter(jwtTokenProvider.findUserByToken(token), letterId));
    }

    @PostMapping("/replies/{origin_letter_id}")
    public ResponseEntity<Message> createReply(@RequestHeader(AUTHORIZATION) String token,
                                               @PathVariable("origin_letter_id") Long originLetterId,
                                               @RequestBody CreateReplyRequest createReplyRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(letterService.createReply(jwtTokenProvider.findUserByToken(token), originLetterId, createReplyRequest));
    }

    @PostMapping("/collect/{letter_id}")
    public ResponseEntity<Message> collectLetter(@RequestHeader(AUTHORIZATION) String token,
                                                 @PathVariable("letter_id") Long letterId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(letterService.collectLetter(jwtTokenProvider.findUserByToken(token), letterId));
    }

    @PostMapping("/accusation/{letter_id}")
    public ResponseEntity<Message> accuseLetter(@RequestHeader(AUTHORIZATION) String token,
                                                @PathVariable("letter_id") Long letterId,
                                                @RequestBody AccuseLetterRequest accuseLetterRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(letterService.createLetterAccusation(jwtTokenProvider.findUserByToken(token), letterId, accuseLetterRequest));
    }

    @GetMapping("/collection")
    public ResponseEntity<FindLetterCollectionResponse> findLetterCollection(@RequestHeader(AUTHORIZATION) String token) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(letterService.findLetterCollection(jwtTokenProvider.findUserByToken(token)));
    }

    @GetMapping("/collection/detail/{letter_id}")
    public ResponseEntity<FindCollectedLetterDetailResponse> findCollectedLetterDetail(@RequestHeader(AUTHORIZATION) String token,
                                                                                       @PathVariable("letter_id") Long letterId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(letterService.findCollectedLetterDetail(jwtTokenProvider.findUserByToken(token), letterId));
    }

    @DeleteMapping("/collection/{letter_id}")
    public ResponseEntity<Message> deleteCollectedLetter(@RequestHeader(AUTHORIZATION) String token,
                                                         @PathVariable("letter_id") Long letterId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(letterService.deleteCollectedLetter(jwtTokenProvider.findUserByToken(token), letterId));
    }

    @GetMapping("/replies/check")
    public ResponseEntity<FindUnreadReplyResponse> checkUnreadReply(@RequestHeader(AUTHORIZATION) String token) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(letterService.checkUnreadReply(jwtTokenProvider.findUserByToken(token)));
    }

    @GetMapping("/replies")
    public ResponseEntity<FindRepliesResponse> findReplies(@RequestHeader(AUTHORIZATION) String token) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(letterService.findReplies(jwtTokenProvider.findUserByToken(token)));
    }

    @GetMapping("/replies/detail/{replied_letter_id}")
    public ResponseEntity<FindReplyDetailResponse> findReplyDetail(@RequestHeader(AUTHORIZATION) String token,
                                                                   @PathVariable("replied_letter_id") Long repliedLetterId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(letterService.findReplyDetail(jwtTokenProvider.findUserByToken(token), repliedLetterId));
    }

    @DeleteMapping("/replies/{replied_letter_id}")
    public ResponseEntity<Message> deleteReply(@RequestHeader(AUTHORIZATION) String token,
                                               @PathVariable("replied_letter_id") Long repliedLetterId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(letterService.deleteReply(jwtTokenProvider.findUserByToken(token), repliedLetterId));
    }

    @PostMapping("/bad-words")
    public ResponseEntity<Message> createBadWords(@RequestHeader(AUTHORIZATION) String token,
                                                  @RequestBody CreateBadWordsRequest createBadWordsRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(letterService.createBadWordRedisSet(token, createBadWordsRequest));
    }

    @PostMapping("/check/1")
    public ResponseEntity<CheckTextHarmfulnessResponse> checkTextHarmfulness(@RequestHeader(AUTHORIZATION) String token,
                                                                             @RequestBody CheckTextHarmfulnessRequest checkTextHarmfulnessRequest) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(letterService.checkTextHarmfulness(token, checkTextHarmfulnessRequest));
    }

    @PostMapping("/check/2")
    public ResponseEntity<CheckImageLetterResponse> checkImageLetterHarmfulness(@RequestHeader(AUTHORIZATION) String token,
                                                                                @RequestBody CheckImageLetterRequest checkImageLetterRequest) throws IOException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(letterService.checkImageLetter(token, checkImageLetterRequest));
    }
}
