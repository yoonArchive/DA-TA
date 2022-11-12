/**
 * @author chaeyoon
 */
import { client } from "../utils/client";

/**
 * @description 오늘의 질문 답변모음 받아오기
 */ //수정 예정
export const getTodayQuestionAnswerList = async (listType) => {
  const result = await client
    .get(`/today/answer/${listType}`)
    .then((response) => response)
    .catch((error) => error);
  return result;
};

/**
 * @description 오늘의 질문 DB에서 가져오기
 */ //수정 예정
export const getTodayQuestion = async () => {
  const result = await client
    .get(`/today/question`)
    .then((response) => response)
    .catch((error) => error);
  return result;
};
