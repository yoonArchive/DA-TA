package com.da_ta.backend.letter.service;

import com.da_ta.backend.account.user.domain.entity.User;
import com.da_ta.backend.account.user.domain.repository.UserRepository;
import com.da_ta.backend.common.domain.Age;
import com.da_ta.backend.common.domain.Message;
import com.da_ta.backend.common.domain.exception.BadRequestException;
import com.da_ta.backend.common.domain.exception.NotFoundException;
import com.da_ta.backend.letter.controller.dto.*;
import com.da_ta.backend.letter.controller.dto.common.*;
import com.da_ta.backend.letter.domain.entity.*;
import com.da_ta.backend.letter.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.stream.Collectors;

import static com.da_ta.backend.common.domain.ErrorCode.*;
import static com.da_ta.backend.common.domain.SuccessCode.*;

@RequiredArgsConstructor
@Service
public class LetterService {

    private final static int MAX_FLOAT_COUNT = 5;
    private final static String TYPE_TEXT = "Text";
    private final static String TYPE_IMAGE = "Image";

    private final CollectedLetterRepository collectedLetterRepository;
    private final FloatedLetterRepository floatedLetterRepository;
    private final FloatedLetterLogRepository floatedLetterLogRepository;
    private final ImageLetterRepository imageLetterRepository;
    private final LetterAccusationRepository letterAccusationRepository;
    private final LetterRepository letterRepository;
    private final ReplyRepository replyRepository;
    private final TextLetterRepository textLetterRepository;
    private final UserRepository userRepository;

    @Transactional
    public Message createTextLetter(TextLetterCreateRequest textLetterCreateRequest) {
        Option option = textLetterCreateRequest.getOption();
        TextLetterInfo textLetterInfo = textLetterCreateRequest.getTextLetterInfo();
        TextLetter textLetter = TextLetter.builder()
                .writer(findUserById(textLetterCreateRequest.getUserId()))
                .ageOption(option.getAgeOption())
                .replyOption(option.getReplyOption())
                .backgroundId(textLetterInfo.getBackgroundId())
                .fontId(textLetterInfo.getFontId())
                .title(textLetterInfo.getTitle())
                .content(textLetterInfo.getContent())
                .build();
        textLetterRepository.save(textLetter);
        floatLetter(textLetter);
        return new Message(TEXT_LETTER_FLOATED.getMessage());
    }

    @Transactional
    public Message createImageLetter(ImageLetterCreateRequest imageLetterCreateRequest) {
        Option option = imageLetterCreateRequest.getOption();
        ImageLetterInfo imageLetterInfo = imageLetterCreateRequest.getImageLetterInfo();
        ImageLetter imageLetter = ImageLetter.builder()
                .writer(findUserById(imageLetterCreateRequest.getUserId()))
                .ageOption(option.getAgeOption())
                .replyOption(option.getReplyOption())
                .backgroundId(imageLetterInfo.getBackgroundId())
                .title(imageLetterInfo.getTitle())
                .imageLetterUrl(imageLetterInfo.getImageLetterUrl())
                .build();
        imageLetterRepository.save(imageLetter);
        floatLetter(imageLetter);
        return new Message(IMAGE_LETTER_FLOATED.getMessage());
    }

    @Transactional
    public ReceiveFloatedLetterResponse receiveFloatedLetter(Long recipientId) {
        User recipient = findUserById(recipientId);
        FloatedLetter floatedLetter = findFloatedLetterByAge(recipientId, recipient.getAge());
        floatedLetter.updateRecipient(recipient);
        floatedLetterRepository.save(floatedLetter);
        floatedLetterLogRepository.save(FloatedLetterLog.builder()
                .loggedRecipientId(recipientId)
                .floatedLetter(floatedLetter)
                .build());
        long letterId = floatedLetter.getLetter().getId();
        Letter letter = findLetterById(letterId);
        if (letter.getLetterType().equals(TYPE_TEXT)) {
            TextLetter textLetter = findTextLetterById(letterId);
            return ReceiveFloatedLetterResponse.builder()
                    .writerId(textLetter.getWriter().getId())
                    .writerNickname(textLetter.getWriter().getNickname())
                    .floatedLetterId(floatedLetter.getId())
                    .letterInfo(LetterInfo.builder()
                            .letterId(textLetter.getId())
                            .title(textLetter.getTitle())
                            .content(textLetter.getContent())
                            .backgroundId(textLetter.getBackgroundId())
                            .fontId(textLetter.getFontId())
                            .writtenDate(textLetter.getCreatedDate())
                            .build())
                    .build();
        } else if (letter.getLetterType().equals(TYPE_IMAGE)) {
            ImageLetter imageLetter = findImageLetterById(letterId);
            return ReceiveFloatedLetterResponse.builder()
                    .writerId(imageLetter.getWriter().getId())
                    .writerNickname(imageLetter.getWriter().getNickname())
                    .floatedLetterId(floatedLetter.getId())
                    .letterInfo(LetterInfo.builder()
                            .letterId(imageLetter.getId())
                            .title(imageLetter.getTitle())
                            .imageLetterUrl(imageLetter.getImageLetterUrl())
                            .backgroundId(imageLetter.getBackgroundId())
                            .writtenDate(imageLetter.getCreatedDate())
                            .build())
                    .build();
        } else {
            throw new NotFoundException(LETTER_TYPE_NOT_FOUND);
        }
    }

    public Message createReply(Long letterId, CreateReplyRequset createReplyRequset) {
        TextLetterInfo textLetterInfo = createReplyRequset.getTextLetterInfo();
        TextLetter textLetter = TextLetter.builder()
                .title(textLetterInfo.getTitle())
                .content(textLetterInfo.getContent())
                .backgroundId(textLetterInfo.getBackgroundId())
                .fontId(textLetterInfo.getFontId())
                .build();
        textLetterRepository.save(textLetter);
        replyRepository.save(Reply.builder()
                .recipient(findUserById(createReplyRequset.getRecipientId()))
                .originLetterId(letterId)
                .replyLetter(textLetter)
                .build());
        return new Message(REPLY_SENT.getMessage());
    }

    public Message checkReplyReception(Long replyId) {
        Reply reply = findReplyById(replyId);
        reply.updateIsRead();
        replyRepository.save(reply);
        return new Message(REPLY_RECEPTION_CHECKED.getMessage());
    }

    public Message updateFloatedLetter(Long floatedLetterId) {
        FloatedLetter floatedLetter = findFloatedLetterById(floatedLetterId);
        if (floatedLetterLogRepository.countByFloatedLetterId(floatedLetter.getId()) == MAX_FLOAT_COUNT) {
            floatedLetter.deleteFloatedLetter();
        }
        floatedLetter.updateRecipient(null);
        floatedLetterRepository.save(floatedLetter);
        return new Message(LETTER_FLOATED_AGAIN.getMessage());
    }

    public Message collectLetter(Long userId, Long letterId) {
        Letter letter = findLetterById(letterId);
        if (letter.isReplyOption()) {
            throw new BadRequestException(COLLECT_LETTER_REJECTED);
        }
        collectedLetterRepository.save(CollectedLetter.builder()
                .letter(letter)
                .user(findUserById(userId))
                .build());
        return new Message(LETTER_COLLECTED.getMessage());
    }

    public Message createLetterAccusation(Long userId, Long letterId, AccuseLetterRequest accuseLetterRequest) {
        letterAccusationRepository.save(LetterAccusation.builder()
                .letter(findLetterById(letterId))
                .reporterId(userId)
                .reason(accuseLetterRequest.getReason())
                .build());
        return new Message(LETTER_ACCUSED.getMessage());
    }

    public void floatLetter(Letter letter) {
        floatedLetterRepository.save(FloatedLetter.builder()
                .letter(letter)
                .build());
    }

    public FindLetterCollectionResponse findLetterCollection(Long userId) {
        return FindLetterCollectionResponse.builder()
                .collection(collectedLetterRepository.findAllByUserIdAndIsActiveTrueOrderByCreatedDateDesc(userId)
                        .stream()
                        .map(collectedLetter ->
                                CollectionItem.builder()
                                        .letterId(collectedLetter.getLetter().getId())
                                        .letterTitle(collectedLetter.getLetter().getTitle())
                                        .writerId(collectedLetter.getLetter().getWriter().getId())
                                        .writerNickname(collectedLetter.getLetter().getWriter().getNickname())
                                        .writtenDate(collectedLetter.getLetter().getCreatedDate())
                                        .build()
                        ).collect(Collectors.toList()))
                .build();
    }

    public FindCollectedLetterDetailResponse findCollectedLetterDetail(Long letterId) {
        Letter letter = findLetterById(letterId);
        if (letter.getLetterType().equals(TYPE_TEXT)) {
            TextLetter textLetter = findTextLetterById(letterId);
            return FindCollectedLetterDetailResponse.builder()
                    .letterType(TYPE_TEXT)
                    .writerId(textLetter.getWriter().getId())
                    .writerNickname(textLetter.getWriter().getNickname())
                    .letterInfo(LetterInfo.builder()
                            .title(textLetter.getTitle())
                            .content(textLetter.getContent())
                            .backgroundId(textLetter.getBackgroundId())
                            .fontId(textLetter.getFontId())
                            .writtenDate(textLetter.getCreatedDate())
                            .build())
                    .build();
        } else if (letter.getLetterType().equals(TYPE_IMAGE)) {
            ImageLetter imageLetter = findImageLetterById(letterId);
            return FindCollectedLetterDetailResponse.builder()
                    .letterType(TYPE_IMAGE)
                    .writerId(imageLetter.getWriter().getId())
                    .writerNickname(imageLetter.getWriter().getNickname())
                    .letterInfo(LetterInfo.builder()
                            .title(imageLetter.getTitle())
                            .backgroundId(imageLetter.getBackgroundId())
                            .writtenDate(imageLetter.getCreatedDate())
                            .build())
                    .build();
        } else {
            throw new NotFoundException(LETTER_TYPE_NOT_FOUND);
        }
    }

    public Message deleteCollectedLetter(Long letterId) {
        CollectedLetter collectedLetter = findCollectedLetterByLetterId(letterId);
        collectedLetter.deleteCollectedLetter();
        collectedLetterRepository.save(collectedLetter);
        return new Message(COLLECTED_LETTER_DELETED.getMessage());
    }

    public FindUnreadReplyResponse checkUnreadReply(Long userId) {
        return FindUnreadReplyResponse.builder()
                .isUnreadReply(replyRepository.existsByIsReadTrueAndIsActiveTrueAndRecipientId(userId))
                .build();
    }

    public FindRepliesResponse findReplies(Long userId) {
        return FindRepliesResponse.builder()
                .replies(replyRepository.findAllByRecipientIdAndIsActiveTrueOrderByCreatedDateDesc(userId)
                        .stream()
                        .map(reply ->
                                ReplyItem.builder()
                                        .replyId(reply.getReplyLetter().getId())
                                        .replyTitle(reply.getReplyLetter().getTitle())
                                        .writerId(reply.getReplyLetter().getWriter().getId())
                                        .writerNickname(reply.getReplyLetter().getWriter().getNickname())
                                        .writtenDate(reply.getReplyLetter().getCreatedDate())
                                        .isRead(reply.isRead())
                                        .build())
                        .collect(Collectors.toList()))
                .build();
    }

    public FindReplyDetailResponse findReplyDetail(Long replyId) {
        Long originLetterId = findReplyById(replyId).getOriginLetterId();
        Letter originLetter = findLetterById(originLetterId);
        TextLetter replyLetter = findTextLetterById(replyId);
        ReplyInfo replyInfo = ReplyInfo.builder()
                .writerId(replyLetter.getWriter().getId())
                .writerNickname(replyLetter.getWriter().getNickname())
                .title(replyLetter.getTitle())
                .content(replyLetter.getContent())
                .backgroundId(replyLetter.getBackgroundId())
                .fontId(replyLetter.getFontId())
                .writtenDate(replyLetter.getCreatedDate())
                .build();
        if (originLetter.getLetterType().equals(TYPE_TEXT)) {
            TextLetter textLetter = findTextLetterById(originLetterId);
            return FindReplyDetailResponse.builder()
                    .originLetterInfo(LetterInfo.builder()
                            .letterId(originLetterId)
                            .title(textLetter.getTitle())
                            .backgroundId(textLetter.getBackgroundId())
                            .writtenDate(textLetter.getCreatedDate())
                            .content(textLetter.getContent())
                            .fontId(textLetter.getFontId())
                            .build())
                    .replyInfo(replyInfo)
                    .build();
        } else if (originLetter.getLetterType().equals(TYPE_IMAGE)) {
            ImageLetter imageLetter = findImageLetterById(originLetterId);
            return FindReplyDetailResponse.builder()
                    .originLetterInfo(LetterInfo.builder()
                            .letterId(originLetterId)
                            .title(imageLetter.getTitle())
                            .backgroundId(imageLetter.getBackgroundId())
                            .writtenDate(imageLetter.getCreatedDate())
                            .imageLetterUrl(imageLetter.getImageLetterUrl())
                            .build())
                    .replyInfo(replyInfo)
                    .build();
        } else {
            throw new NotFoundException(LETTER_TYPE_NOT_FOUND);
        }
    }

    public Message deleteReply(Long replyLetterId) {
        Reply reply = findReplyByReplyLetterId(replyLetterId);
        reply.deleteReplyLetter();
        replyRepository.save(reply);
        return new Message(REPLY_DELETED.getMessage());
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    private Letter findLetterById(Long LetterId) {
        return letterRepository.findById(LetterId)
                .orElseThrow(() -> new NotFoundException(LETTER_NOT_FOUND));
    }

    private TextLetter findTextLetterById(Long LetterId) {
        return textLetterRepository.findById(LetterId)
                .orElseThrow(() -> new NotFoundException(TEXT_LETTER_NOT_FOUND));
    }

    private ImageLetter findImageLetterById(Long LetterId) {
        return imageLetterRepository.findById(LetterId)
                .orElseThrow(() -> new NotFoundException(IMAGE_LETTER_NOT_FOUND));
    }

    private FloatedLetter findFloatedLetterById(Long floatedLetterId) {
        return floatedLetterRepository.findById(floatedLetterId)
                .orElseThrow(() -> new NotFoundException(FLOATED_LETTER_NOT_FOUND));
    }

    private FloatedLetter findFloatedLetterByAge(Long recipientId, Age age) {
        return floatedLetterRepository.findFloatedLetterByAgeOption(recipientId, age.toString())
                .orElseThrow(() -> new NotFoundException(FLOATED_LETTER_NOT_FOUND));
    }

    private Reply findReplyById(Long replyId) {
        return replyRepository.findById(replyId)
                .orElseThrow(() -> new NotFoundException(REPLY_NOT_FOUND));
    }

    private Reply findReplyByReplyLetterId(Long replyLetterId) {
        return replyRepository.findByReplyLetterId(replyLetterId)
                .orElseThrow(() -> new NotFoundException(REPLY_NOT_FOUND));
    }

    private CollectedLetter findCollectedLetterByLetterId(Long letterId) {
        return collectedLetterRepository.findByLetterId(letterId)
                .orElseThrow(() -> new NotFoundException(COLLECTED_LETTER_NOT_FOUND));
    }
}
