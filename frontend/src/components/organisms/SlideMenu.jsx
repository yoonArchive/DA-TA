import React, { useEffect, useState } from "react";
import styled from "styled-components";
import { faX, faHouse, faPenToSquare, faEnvelopeOpenText, faGear, faHammer } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useNavigate } from "react-router-dom";
import Button from "./../atoms/Button";
import { useRecoilValue } from "recoil";
import { userState } from "../../recoil/Atoms";
import { RoleTypes } from "./../../constants/Roles";

const SlideMenu = ({ show, setSlideMenuToggle, handleLogin, handleLogout, isLogin }) => {
	const navigate = useNavigate();

	const nickname = useRecoilValue(userState).nickname;
	const role = useRecoilValue(userState).role;

	useEffect(() => {
		document.body.style.overflow = show ? "hidden" : "auto";
	}, [show]);

	const handleMenuClick = (goto) => {
		setSlideMenuToggle(false);
		navigate(goto);
	};

	return (
		<>
			<Container show={show}>
				<MenuContent jc={false}>
					<FaWrapper onClick={() => setSlideMenuToggle(false)}>
						<FontAwesomeIcon icon={faX} size="lg" />
					</FaWrapper>
				</MenuContent>
				{isLogin ? (
					<>
						<MenuContent jc={false}>
							안녕하세요!
							<br />
							{nickname}님
						</MenuContent>
						<MenuContent jc={false}>
							<Button hoverBgOpacity="0.3" fontSize="1.2rem" height="3rem" width="11rem" onClick={() => handleLogout()}>
								로그아웃
							</Button>
						</MenuContent>
					</>
				) : (
					<>
						<MenuContent jc={false}>로그인 해주세요</MenuContent>
						<MenuContent jc={false}>
							<Button margin="0 3rem 0 0" onClick={() => handleLogin()}>
								<img src={`${process.env.PUBLIC_URL}/assets/images/auth/kakao_login.png`} />
							</Button>
						</MenuContent>
					</>
				)}

				<Spacer />
				<MenuContent jc={true} onClick={() => handleMenuClick("/")}>
					홈
					<FaWrapper>
						<FontAwesomeIcon icon={faHouse} />
					</FaWrapper>
				</MenuContent>
				<MenuContent jc={true} onClick={() => handleMenuClick("/write")}>
					편지 쓰기
					<FaWrapper>
						<FontAwesomeIcon icon={faPenToSquare} />
					</FaWrapper>
				</MenuContent>
				<MenuContent jc={true} onClick={() => handleMenuClick("/get")}>
					편지 받기
					<FaWrapper>
						<FontAwesomeIcon icon={faEnvelopeOpenText} />
					</FaWrapper>
				</MenuContent>
				<MenuContent jc={true} onClick={() => handleMenuClick("/mypage")}>
					마이페이지
					<FaWrapper>
						<FontAwesomeIcon icon={faGear} />
					</FaWrapper>
				</MenuContent>
				{role === RoleTypes.ROLE_ADMIN && (
					<MenuContent jc={true} onClick={() => handleMenuClick("/admin")}>
						관리자 페이지
						<FaWrapper>
							<FontAwesomeIcon icon={faHammer} />
						</FaWrapper>
					</MenuContent>
				)}
			</Container>
		</>
	);
};

const Container = styled.div`
	display: flex;
	flex-direction: column;
	position: fixed;
	top: 0;
	z-index: 1001;
	right: ${({ show }) => {
		return show ? "0vw" : "-100vw";
	}};
	transition: 0.5s ease-out;
	height: 100%;
	width: 70vw;
	background: linear-gradient(to bottom right, #958dd6, #5778ec);
	align-items: center;
	padding: 1rem 0 1rem 0;
`;

const MenuContent = styled.div`
	display: flex;
	width: 80%;
	height: 1.5rem;
	border-bottom: ${(props) => (props.jc ? "1px solid navy" : "0")};
	padding: 1.5rem 10% 1.5rem 10%;
	align-items: center;
	justify-content: ${(props) => (props.jc ? "start" : "end")};
	text-align: end;
	cursor: pointer;
`;

const Spacer = styled.div`
	display: flex;
	width: 100%;
	height: 0.5rem;
	background-color: rgba(250, 250, 250, 0.5);
`;

const FaWrapper = styled.div`
	display: flex;
	position: absolute;
	padding: 1rem;
	right: 1rem;
	align-items: center;
	justify-content: center;
`;

export default SlideMenu;
