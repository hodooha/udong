package com.multi.udong.member.service;

import org.springframework.beans.factory.annotation.Value;

/**
 * The type Naver api key.
 *
 * @author : 재식
 * @since : 24. 7. 23.
 */
public class NaverAPIKey {
    //상수 ==> final, 변경불가능
    //스태틱, 정적 ==> static, 객체생성하지 않고 언제든지 접근 가능

    @Value("${naver.ocr.url}")
    public static String OCR_URL;

    @Value("${naver.ocr.secret}")
    public static String OCR_SECRET;
}
//https://guide.ncloud-docs.com/docs/ko/clovaocr-domain 참고
