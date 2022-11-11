/**
 * @author chaeyoon
 */
import { client } from "../utils/client";

/**
 * @description 오늘의 편지 답변 쓰기
 */ //수정 예정
export const saveTextAnswer = async (answer) => {
  const body = {
    textAnswerInfo: {
      answer: answer,
      userId: userId,
      todayQuestionId: todayQuestionId,
    },
  };

  console.log(body);

  const result = await client
    .post(`/today/answer`, body)
    .then((response) => response)
    .catch((error) => error);
  return result;
};