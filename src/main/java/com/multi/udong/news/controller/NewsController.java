package com.multi.udong.news.controller;

import com.multi.udong.club.model.dto.ReportDTO;
import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.common.model.dto.LocationDTO;
import com.multi.udong.login.service.CustomUserDetails;
import com.multi.udong.news.model.dto.*;
import com.multi.udong.news.service.NewsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }


    @RequestMapping("/newsMain")
    public String clubLog(@AuthenticationPrincipal CustomUserDetails c, FilterDTO filterDTO, Model model, RedirectAttributes redirectAttributes) {

        // 파라미터로 받은 page 값으로 시작 및 시작 index 설정
        filterDTO.setStartAndStartIndex(filterDTO.getPage());

        // 검색 조건 확인
        System.out.println("###### 검색하는 카테고리 코드: " + filterDTO.getCategoryCode());
        System.out.println("###### 검색하는 검색어: " + filterDTO.getSearchWord());

        // 로그인 유저의 locationCode를 filterDTO에 set
        long membersLocation = c.getMemberDTO().getMemAddressDTO().getLocationCode();
        filterDTO.setLocationCode(membersLocation);

        try {

            // 소식 리스트를 조회
            List<NewsDTO> newsList = newsService.selectNewsList(filterDTO);
            System.out.println("###### 가져온 소식 리스트: " + newsList);

            // 소식 개수로 페이지 수 계산
            int newsCount = newsService.selectNewsCount(filterDTO);
            System.out.println("###### 기록 총 개수: " + newsCount);

            int pages = 0;

            if (newsCount != 0) {

                pages = newsCount / 5;

                if (newsCount % 5 != 0) {
                    pages += 1;
                }

            }
            System.out.println("###### 페이지 개수: " + pages);

            model.addAttribute("newsList", newsList);
            model.addAttribute("pages", pages);
            model.addAttribute("filter", filterDTO);

            return "/news/newsMain";

        } catch (Exception e) {

            e.printStackTrace();
            model.addAttribute("msg", "소식 리스트 조회 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

    }

    @RequestMapping("/categoryList")
    @ResponseBody
    public List<CategoryDTO> categoryList() {

        try {

            List<CategoryDTO> categoryList = newsService.selectCategoryList();

            System.out.println("###### 가져온 카테고리 리스트: " + categoryList);

            return categoryList;

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }

    @RequestMapping("/newsInsertForm")
    public void newsInsertForm() {

    }


    @PostMapping("/insertNews")
    public String insertNews(@AuthenticationPrincipal CustomUserDetails c, NewsDTO newsDTO, CategoryDTO categoryDTO, @RequestParam("imgList") MultipartFile[] imgList, Model model) {

        // 로그인 유저를 작성자로 set
        MemberDTO writer = new MemberDTO();
        writer.setMemberNo(c.getMemberDTO().getMemberNo());
        newsDTO.setWriter(writer);

        // 로그인 유저의 동네를 set
        LocationDTO location = new LocationDTO();
        location.setLocationCode(c.getMemberDTO().getMemAddressDTO().getLocationCode());
        newsDTO.setLocation(location);

        // 카테고리 set
        newsDTO.setCategory(categoryDTO);

        // 저장 경로 설정
        String path = Paths.get(System.getProperty("user.home"), "udongUploads").toAbsolutePath().normalize().toString();
        String filePath = path + File.separator; // 운영 체제에 맞는 구분자 추가
        System.out.println("###### 파일 저장 경로: " + filePath);

        System.out.println("###### img 개수 >>>>> " + imgList.length);

        List<String> savedNameList = new ArrayList<>();

        if(imgList.length > 0) {

            // filePath에 폴더가 없으면 폴더 생성
            File mkdir = new File(filePath);
            if(!mkdir.exists()) {
                mkdir.mkdirs();
            }

            List<AttachmentDTO> attachmentList = new ArrayList<>();

            for(MultipartFile img : imgList) {

                if(!img.isEmpty()) {

                    // 첨부 파일의 이름을 가져와서
                    String originFileName = img.getOriginalFilename();
                    // 확장자만 잘라서 변수에 저장
                    String ext = originFileName.substring(originFileName.lastIndexOf("."));
                    // 랜덤한 파일이름을 생성한 후 뒤에 확장자 추가
                    String savedName = UUID.randomUUID().toString().replace("-", "") + ext;

                    AttachmentDTO attachment = new AttachmentDTO();
                    attachment.setOriginalName(originFileName);
                    attachment.setSavedName(savedName);
                    attachment.setTypeCode("NS");

                    attachmentList.add(attachment);
                    savedNameList.add(savedName);

                    try {

                        img.transferTo(new File(filePath + "/" + savedName));

                    } catch (Exception e) {

                        e.printStackTrace();

                        // 오류 발생 시 생성한 파일을 삭제
                        new File(filePath + "/" + savedName).delete();

                        model.addAttribute("msg", "이미지 업로드 과정에서 문제가 발생했습니다.");

                        return "common/errorPage";

                    }

                }

            }

            newsDTO.setAttachments(attachmentList);

        }

        System.out.println("###### 작성할 소식 >>>>> " + newsDTO);

        try {

            newsService.insertNews(newsDTO);
            int newsNo = newsDTO.getNewsNo();

            return "redirect:/news/newsDetail?newsNo=" + newsNo;

        } catch (Exception e) {

            e.printStackTrace();

            // 오류 발생 시 생성한 파일을 삭제
            for(String savedName : savedNameList) {

                new File(filePath + "/" + savedName).delete();

            }

            model.addAttribute("msg", "소식 작성 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

    }


    @RequestMapping("/newsDetail")
    public String newsDetail(@AuthenticationPrincipal CustomUserDetails c, RequestDTO requestDTO, Model model, RedirectAttributes redirectAttributes) {

        // 로그인 유저의 no를 requestDTO에 set
        int memberNo = c.getMemberDTO().getMemberNo();
        requestDTO.setMemberNo(memberNo);

        int newsNo = requestDTO.getNewsNo();

        // 삭제된 소식인지 검증
        if(!checkIsNewsDeleted(c, newsNo)) {
            redirectAttributes.addFlashAttribute("alert", "삭제됐거나 존재하지 않는 소식입니다.");
            redirectAttributes.addFlashAttribute("alertType", "error");
            return "redirect:/news/newsMain";
        }

        try {

            newsService.addNewsViews(newsNo);

            NewsDTO newsDetail = newsService.selectNewsDetail(requestDTO);

            System.out.println("###### 가져온 기록 상세 >>>>> " + newsDetail);

            model.addAttribute("newsDetail", newsDetail);

            return "news/newsDetail";

        } catch (Exception e) {

            e.printStackTrace();
            model.addAttribute("msg", "소식 상세 조회 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

    }

    @PostMapping("/insertNewsLike")
    public ResponseEntity<Void> insertNewsLike(@AuthenticationPrincipal CustomUserDetails c, LikeDTO likeDTO) {

        int memberNo = c.getMemberDTO().getMemberNo();
        likeDTO.setMemberNo(memberNo);

        // 삭제된 소식인지 검증
        if(!checkIsNewsDeleted(c, likeDTO.getNewsNo())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {

            newsService.insertNewsLike(likeDTO);

            return new ResponseEntity<>(HttpStatus.OK);


        } catch (Exception e) {

            e.printStackTrace();

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }

    }

    @PostMapping("/deleteNewsLike")
    public ResponseEntity<Void> deleteNewsLike(@AuthenticationPrincipal CustomUserDetails c, LikeDTO likeDTO) {

        int memberNo = c.getMemberDTO().getMemberNo();
        likeDTO.setMemberNo(memberNo);

        // 삭제된 소식인지 검증
        if(!checkIsNewsDeleted(c, likeDTO.getNewsNo())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {

            newsService.deleteNewsLike(likeDTO);

            return new ResponseEntity<>(HttpStatus.OK);


        } catch (Exception e) {

            e.printStackTrace();

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }

    }

    @PostMapping("/insertReply")
    public String insertReply(@AuthenticationPrincipal CustomUserDetails c, ReplyDTO replyDTO, Model model) {

        int memberNo = c.getMemberDTO().getMemberNo();
        int newsNo = replyDTO.getNewsNo();

        // 삭제된 소식인지 검증
        if(!checkIsNewsDeleted(c, newsNo)) {
            return "redirect:/news/newsMain";
        }

        MemberDTO writer = new MemberDTO();
        writer.setMemberNo(memberNo);
        replyDTO.setWriter(writer);

        try {

            newsService.insertReply(replyDTO);

            return "redirect:/news/newsDetail?newsNo=" + newsNo;

        } catch (Exception e) {

            e.printStackTrace();
            model.addAttribute("msg", "댓글 작성 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

    }

    @PostMapping("/updateReply")
    public String updateReply(@AuthenticationPrincipal CustomUserDetails c, ReplyDTO replyDTO, Model model, RedirectAttributes redirectAttributes) {

        int memberNo = c.getMemberDTO().getMemberNo();
        int newsNo = replyDTO.getNewsNo();

        // 삭제된 소식인지 검증
        if(!checkIsNewsDeleted(c, newsNo)) {
            return "redirect:/news/newsMain";
        }

        // 댓글 작성자인지 검증
        if(!checkReplyWriter(c, replyDTO.getReplyNo())) {
            return "redirect:/news/newsDetail?newsNo=" + newsNo;
        }

        MemberDTO writer = new MemberDTO();
        writer.setMemberNo(memberNo);
        replyDTO.setWriter(writer);

        try {

            int result = newsService.updateReply(replyDTO);

            if(result == 1) {
                redirectAttributes.addFlashAttribute("alert", "댓글 수정이 완료되었습니다.");
                redirectAttributes.addFlashAttribute("alertType", "success");
            }

            return "redirect:/news/newsDetail?newsNo=" + newsNo;

        } catch (Exception e) {

            e.printStackTrace();
            model.addAttribute("msg", "댓글 수정 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

    }

    @PostMapping("/deleteReply")
    public String deleteReply(@AuthenticationPrincipal CustomUserDetails c, ReplyDTO replyDTO, Model model, RedirectAttributes redirectAttributes) {

        int memberNo = c.getMemberDTO().getMemberNo();
        int newsNo = replyDTO.getNewsNo();

        // 삭제된 소식인지 검증
        if(!checkIsNewsDeleted(c, newsNo)) {
            return "redirect:/news/newsMain";
        }

        // 댓글 작성자인지 검증
        if(!checkReplyWriter(c, replyDTO.getReplyNo())) {
            return "redirect:/news/newsDetail?newsNo=" + newsNo;
        }

        MemberDTO writer = new MemberDTO();
        writer.setMemberNo(memberNo);
        replyDTO.setWriter(writer);

        try {

            int result = newsService.deleteReply(replyDTO);

            if(result == 1) {
                redirectAttributes.addFlashAttribute("alert", "댓글 삭제가 완료되었습니다.");
                redirectAttributes.addFlashAttribute("alertType", "success");
            }

            return "redirect:/news/newsDetail?newsNo=" + newsNo;

        } catch (Exception e) {

            e.printStackTrace();
            model.addAttribute("msg", "댓글 수정 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

    }

    @PostMapping("/insertReplyLike")
    public ResponseEntity<Void> insertReplyLike(@AuthenticationPrincipal CustomUserDetails c, LikeDTO likeDTO) {

        int memberNo = c.getMemberDTO().getMemberNo();
        likeDTO.setMemberNo(memberNo);

        // 삭제된 소식인지 검증
        if(!checkIsNewsDeleted(c, likeDTO.getNewsNo())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {

            newsService.insertReplyLike(likeDTO);

            return new ResponseEntity<>(HttpStatus.OK);


        } catch (Exception e) {

            e.printStackTrace();

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }

    }

    @PostMapping("/deleteReplyLike")
    public ResponseEntity<Void> deleteReplyLike(@AuthenticationPrincipal CustomUserDetails c, LikeDTO likeDTO) {

        int memberNo = c.getMemberDTO().getMemberNo();
        likeDTO.setMemberNo(memberNo);

        // 삭제된 소식인지 검증
        if(!checkIsNewsDeleted(c, likeDTO.getNewsNo())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {

            newsService.deleteReplyLike(likeDTO);

            return new ResponseEntity<>(HttpStatus.OK);


        } catch (Exception e) {

            e.printStackTrace();

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }

    }

    @RequestMapping("/newsReportForm")
    public String newsReportForm(@AuthenticationPrincipal CustomUserDetails c, RequestDTO requestDTO, Model model) {

        // 삭제된 소식인지 검증
        if(!checkIsNewsDeleted(c, requestDTO.getNewsNo())) {
            return "redirect:/news/newsMain";
        }

        try {

            NewsDTO newsDetail = newsService.selectNewsDetail(requestDTO);

            model.addAttribute("newsDetail", newsDetail);

            return "/news/newsReportForm";

        } catch (Exception e) {

            e.printStackTrace();
            model.addAttribute("msg", "댓글 수정 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

    }

    @PostMapping("/reportNews")
    public String reportNews(@AuthenticationPrincipal CustomUserDetails c, ReportDTO reportDTO, @RequestParam("customReason") String customReason, Model model, RedirectAttributes redirectAttributes) {

        int reportedNo = reportDTO.getReportedNo();

        // 삭제된 소식인지 검증
        if(!checkIsNewsDeleted(c, reportedNo)) {
            return "redirect:/news/newsMain";
        }

        // 로그인 유저의 no를 신고자로 reportDTO에 set
        reportDTO.setReporterMember(c.getMemberDTO().getMemberNo());

        // 신고 당하는 대상의 타입 코드를 reportDTO에 set
        reportDTO.setTypeCode("NS");

        // 신고 사유를 확인하여 custom이면 따로 파라미터로 받은 직접 입력 사유를 reportDTO에 set
        if(reportDTO.getReason().equals("custom")) {
            reportDTO.setReason(customReason);
        }
        System.out.println("###### 신고 사유: " + reportDTO.getReason());

        // 관리자 페이지에서 신고 대상 상세 조회로 갈 때 사용할 url을 reportDTO에 set
        String url = "/news/newsDetail?newsNo=" + reportedNo;
        reportDTO.setUrl(url);

        try {

            // 신고 당하는 기록의 작성자를 피신고자로 reportDTO에 set
            int reportedMemberNo = newsService.checkNewsWriter(reportedNo);
            reportDTO.setReportedMember(reportedMemberNo);

            int result = newsService.reportNews(reportDTO);

            if(result == 1) {

                redirectAttributes.addFlashAttribute("alert", "소식 신고가 완료되었습니다.");
                redirectAttributes.addFlashAttribute("alertType", "success");

            }

            return "redirect:/news/newsDetail?newsNo=" + reportedNo;

        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute("msg", "모임 신고 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

    }


    // ================= 공통 사용 메소드 =================

    public boolean checkIsNewsDeleted(@AuthenticationPrincipal CustomUserDetails c, int newsNo) {

        try {

            String isNewsDeleted = newsService.checkIsNewsDeleted(newsNo);

            if(isNewsDeleted == null || isNewsDeleted.equals("")) {

                return false;

            }

            if(isNewsDeleted.equals("N") || c.getMemberDTO().getAuthority().equals("ROLE_ADMIN")) {

                return true;

            }
            else {

                return false;

            }

        } catch (Exception e) {

            return false;

        }

    }

    public boolean checkNewsWriter(@AuthenticationPrincipal CustomUserDetails c, int newsNo) {

        try {

            int newsWriter = newsService.checkNewsWriter(newsNo);

            if(newsWriter == 0) {

                return false;

            }

            if(newsWriter == c.getMemberDTO().getMemberNo()) {

                return true;

            }
            else {

                return false;

            }

        } catch (Exception e) {

            return false;

        }

    }

    public boolean checkReplyWriter(@AuthenticationPrincipal CustomUserDetails c, int replyNo) {

        try {

            int replyWriter = newsService.checkReplyWriter(replyNo);

            if(replyWriter == 0) {

                return false;

            }

            if(replyWriter == c.getMemberDTO().getMemberNo()) {

                return true;

            }
            else {

                return false;

            }

        } catch (Exception e) {

            return false;

        }

    }



}
