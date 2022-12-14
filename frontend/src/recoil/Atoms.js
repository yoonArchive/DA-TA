/**
 * @author mingyu
 */
import { atom } from "recoil";
import { recoilPersist } from "recoil-persist";

const { persistAtom } = recoilPersist();

/**
 * @desciption 로그인 여부
 * @return {boolean} true or false를 반환함
 * @example true false
 */
export const loginState = atom({
  key: "loginState",
  default: false,
  effects_UNSTABLE: [persistAtom],
});

/**
 * @description 사용자 정보
 */
export const userState = atom({
  key: "userState",
  default: {
    userId: null,
    nickname: null,
    role: null,
  },
  effects_UNSTABLE: [persistAtom],
});

/**
 * @description 마이페이지의 현재 nested router 위치
 * @return {number} 0~2
 */
export const mypageRouterState = atom({
  key: "mypageRouterState",
  default: 2,
  effects_UNSTABLE: [persistAtom],
});

/**
 * @description 로딩스피너
 */
export const loadingState = atom({
  key: "loading",
  default: false,
});

/**
 * @description 편지 신고하기 모달 toggle
 * @return {boolean}
 */
export const reportModalState = atom({
  key: "reportModalState",
  default: false,
});

/**
 * @description 읽고있는 편지 id
 * @return {number}
 */
export const readingLetterIdState = atom({
  key: "readingLetterIdState",
  default: null,
});

/**
 * @description 받은 편지 내용 보관 (새로고침 대응하기)
 * @return {object}
 */
export const letterState = atom({
  key: "letterState",
  default: false,
  effects_UNSTABLE: [persistAtom],
});

/**
 * @description 오늘의 질문
 */
export const todayQuestionState = atom({
  key: "todayQuestionState",
  default: {
    date: null,
    question: null,
    todayQuestionId: null,
  },
});

/**
 * @description 오늘의 질문 답변
 */
export const todayAnswerState = atom({
  key: "todayAnswerState",
  default: {
    todayAnswerId: null,
    answer: null,
    userId: null,
    todayQuestionId: null,
  },
});
