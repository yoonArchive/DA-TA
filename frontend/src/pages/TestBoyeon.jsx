import React from "react";
import styled from "styled-components";
import { BackgroundGradient } from "../components/atoms/BackgroundGradient";
import { Wave } from "../components/atoms/Wave";

//Todo : 해수면 높이 올리기
const TestBoyeon = () => (
  <>
    <Wraper>
      <MypageContents>실험용 찍찍</MypageContents>
    </Wraper>
    <Wave opacity={0.5} frequency={16} isRight={true}></Wave>
    <Wave opacity={0.3} frequency={8} isRight={true}></Wave>
    <Wave opacity={0.4} frequency={13} isRight={false}></Wave>
    <BackgroundGradient start={"E2AAFD"} end={"FFDFC2"} />
  </>
);

const Wraper = styled.div`
  display: flex;
  min-height: 100vh;
  justify-content: center;
  align-items: center;
`;

const MypageContents = styled.div`
  display: flex;
  height: 656px;
  width: 900px;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
  z-index: 1;
  border-radius: 20px;
`;

export default TestBoyeon;