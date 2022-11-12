/**
 * @author boyeon
 */
/**
 * @param LetterObject //ToDo: API명세서 보고 변수양식 맞추기
 */
// 나중에 API로 받아온 친구들을 props 해줘야함
import React from "react";
import styled from "styled-components";
import { media } from "../../../utils/styleUtil";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faTriangleExclamation,
  faTrashCan,
} from "@fortawesome/free-solid-svg-icons";
import { useSetRecoilState } from "recoil";
import { reportModalState } from "../../../recoil/Atoms";
import { readingLetterIdState } from "../../../recoil/Atoms";

const DateToString = (writtenDate) => {
  const ToDate = new Date(writtenDate);
  return `${ToDate.getFullYear()}년 ${
    ToDate.getMonth() + 1
  }월 ${ToDate.getDate()}일`;
};

export const MypageLetter = ({ letter }) => {
  const setModalToggle = useSetRecoilState(reportModalState);
  const setReadingLetterId = useSetRecoilState(readingLetterIdState);
  const writtenTime = DateToString(letter.writtenDate);

  return (
    <LetterDiv>
      <LetterWordsDiv>
        <LetterTitle>{letter.letterTitle}</LetterTitle>
        <LetterDate>{`${letter.writerNickname}, ${writtenTime}`}</LetterDate>
        <LetterDateWeb>{`${letter.writerNickname}`}</LetterDateWeb>
        <LetterDateWeb>{`${writtenTime}`}</LetterDateWeb>
      </LetterWordsDiv>
      <FontAwesomeIcon
        icon={faTriangleExclamation}
        style={{ margin: "0 15px 0 0", color: "#F44336", cursor: "pointer" }}
        size="lg"
        onClick={() => {
          setModalToggle(true);
          setReadingLetterId(letter.letterId);
          console.log(
            `${letter.id}번 글을 쓴 글쓴이 아이디 ${letter.writerId}를 신고버튼`
          );
        }}
      />
      <FontAwesomeIcon
        icon={faTrashCan}
        style={{ margin: "0 15px 0 0", cursor: "pointer" }}
        size="lg"
        onClick={() => {
          console.log(`${letter.id}번 글 삭제버튼`);
        }}
      />
    </LetterDiv>
  );
};

const LetterDiv = styled.div`
  display: flex;
  height: 70px;
  width: 620px;
  justify-content: center;
  align-items: center;
  background-color: #ffffff;
  border-radius: 5px;
  margin-bottom: 24px;

  ${media.tablet1`
    width: 90%;
    height: 87px;
    align-items: space-between;
  `}
`;

const LetterWordsDiv = styled.div`
  display: flex;
  flex-direction: column;
  width: 530px;
  justify-content: center;
  align-items: center;
  margin: 10px 0 10px 0;

  ${media.tablet1`
    width: 80%;
    margin-left: 15px;
  `}
`;

const LetterTitle = styled.p`
  font-size: 20px;
  width: 100%;
  height: 20px;
  text-align: start;
  cursor: pointer;
  ${media.tablet1`
    margin-bottom: 8px;
  `}
`;

const LetterDate = styled.p`
  font-size: 14px;
  width: 100%;
  height: 17px;
  color: #8f8f8f;
  margin-top: 5px;
  text-align: start;

  ${media.tablet1`
    display: none;
  `}
`;

const LetterDateWeb = styled(LetterDate)`
  display: none;
  margin: 0;
  ${media.tablet1`
    display: inline;
  `}
`;
