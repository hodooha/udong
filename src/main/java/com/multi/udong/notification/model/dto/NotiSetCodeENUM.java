package com.multi.udong.notification.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The enum Noti set code enum.
 */
@Getter
@AllArgsConstructor
public enum NotiSetCodeENUM {

    // 대여 및 나눔
    RENT_NEW_REQ("대여 신청 접수", "대여 게시글 {itemName}에 새로운 대여 신청이 접수되었습니다.", "/rent/detail?itemNo="),
    RENT_CONFIRM("대여 신청 확정", "신청하신 {itemName}의 대여가 확정되었습니다.", "/rent/detail?itemNo="),
    RENT_RETURN_BORROWER("대여 물품 반납 3일 전 알림(대여자)", "대여하신 물품 {itemName}의 반납 기한이 3일 후입니다.", "/rent/myRentals"),
    RENT_RETURN_OWNER("대여 물품 반납 3일 전 알림(소유자)", "대여해주신 물품 {itemName}의 반납 기한이 3일 후입니다.", "/rent/myItems"),
    ITEM_AVAILABLE("관심 물품 대여 가능", "찜한 물품 {itemName}이 대여 가능한 상태가 되었습니다.", "/rent/detail?itemNo="),
    GIVE_DRAW_REMIND("나눔 물품 추첨 24시간 전 알림(소유자)", "나눔하신 물품 {itemName}의 당첨자 추첨이 24시간 후에 진행됩니다.", "/give/detail?itemNo="),
    GIVE_WINNER_SELECTED("나눔 물품 당첨자 선정", "나눔하신 물품 {itemName}의 당첨자가 선정되었습니다.", "/give/detail?itemNo="),

    // 모임
    CL_JOIN_RESULT("모임 가입 신청 결과", "{clubName} 가입 신청이 {result}되었습니다.", "/club/detail?clubNo="),
    CL_POST_COMMENT("모임 게시글 새 댓글", "{clubName} 모임 게시글에 새 댓글이 달렸습니다: {comment}", "/club/post?postNo="),
    CL_NEW_SCHEDULE("모임 새 일정 등록", "{clubName}에 새 일정 \"{scheduleName}\"이 등록되었습니다.", "/club/schedule?scheduleNo="),
    CL_SCHEDULE_REMIND("모임 일정 시작 24시간 전 알림", "{scheduleName}이 24시간 후에 시작됩니다.", "/club/schedule?scheduleNo="),
    CL_NEW_JOIN_REQ("새 모임 가입 신청", "{clubName}에 새 회원 가입 신청이 있습니다.", "/club/members?clubNo="),

    // 소식
    NEWS_COMMENT("소식 게시글 새 댓글", "소식 게시글에 새 댓글이 달렸습니다: {comment}", "/news/detail?newsNo="),

    // 회원
    MEMBER_LEVEL_UP("회원 등급 상승", "회원님의 등급이 {newLevel}로 상승했습니다.", "/mypage"),
    CS_ANSWER("문의글 답변","작성하신 문의글에 답변이 등록되었습니다.", "/cs/queDetail?no="),

    // 관리자
    ADMIN_NEW_SELLER("새 판매자 가입 신청", "새로운 판매자 가입 신청이 접수되었습니다.", "/admin/sellerApplications"),
    ADMIN_NEW_REPORT("새 신고 접수", "새로운 신고가 접수되었습니다. 신고 유형: {reportType}", "/admin/reports"),

    // 판매자, 설정 불가
    SELLER_APP_RESULT("판매자 신청 결과", "판매자 신청이 {result}되었습니다.", "/mypage/seller");

    private final String title;
    private final String messageTemplate;
    private final String href;
}