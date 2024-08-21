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
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }


    @RequestMapping("/newsMain")
    public String clubLog(@AuthenticationPrincipal CustomUserDetails c, FilterDTO filterDTO, Model model, RedirectAttributes redirectAttributes) {

        if(c.getMemberDTO().getMemAddressDTO().getLocationCode() == null) {

            redirectAttributes.addFlashAttribute("alert", "먼저 동네를 등록해 주세요.");
            redirectAttributes.addFlashAttribute("alertType", "noLocation");

            return "redirect:/";

        }

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


            List<NewsDTO> adList = newsService.selectAdList(filterDTO);
            System.out.println("##### 가져온 광고 리스트: " + adList);

            NewsDTO ad = new NewsDTO();

            if (!adList.isEmpty()) {
                Random random = new Random();
                int randomIndex = random.nextInt(adList.size());
                ad = adList.get(randomIndex);
            }
            System.out.println("##### 랜덤 선택된 광고: " + ad);

            model.addAttribute("ad", ad);

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
            model.addAttribute("msg", "댓글 삭제 과정에서 문제가 발생했습니다.");

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
            model.addAttribute("msg", "소식 신고폼 이동 과정에서 문제가 발생했습니다.");

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

            model.addAttribute("msg", "소식 신고 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

    }

    @RequestMapping("/newsUpdateForm")
    public String newsUpdateForm(@AuthenticationPrincipal CustomUserDetails c, RequestDTO requestDTO, Model model) {

        int memberNo = c.getMemberDTO().getMemberNo();
        requestDTO.setMemberNo(memberNo);

        // 삭제된 소식인지 검증
        if(!checkIsNewsDeleted(c, requestDTO.getNewsNo())) {
            return "redirect:/news/newsMain";
        }

        // 소식 작성자인지 체크
        if(!checkNewsWriter(c, requestDTO.getNewsNo())) {
            return "redirect:/news/newsMain";
        }

        try {

            NewsDTO newsDetail = newsService.selectNewsDetail(requestDTO);

            model.addAttribute("newsDetail", newsDetail);

            return "/news/newsUpdateForm";

        } catch (Exception e) {

            e.printStackTrace();
            model.addAttribute("msg", "소식 수정폼 이동 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

    }


    @PostMapping("/updateNews")
    public String updateNews(@AuthenticationPrincipal CustomUserDetails c, CategoryDTO categoryDTO ,NewsDTO newsDTO, @RequestParam("imgList") MultipartFile[] imgList, @RequestParam("status[]") int[] status, @RequestParam("fileNo[]") int[] fileNo, Model model, RedirectAttributes redirectAttributes) {

        int memberNo = c.getMemberDTO().getMemberNo();

        int newsNo = newsDTO.getNewsNo();

        // 삭제된 소식인지 검증
        if(!checkIsNewsDeleted(c,newsNo)) {
            return "redirect:/news/newsMain";
        }

        // 소식 작성자인지 체크
        if(!checkNewsWriter(c, newsNo)) {
            return "redirect:/news/newsMain";
        }

        try {

            newsDTO.setCategory(categoryDTO);

            int newsUpdateResult = newsService.updateNews(newsDTO);

            if(newsUpdateResult == 1) {

                System.out.println("###### fileNo 배열: " + Arrays.toString(fileNo));
                System.out.println("###### status 배열: " + Arrays.toString(status));

                // 저장 경로 설정
                String path = Paths.get(System.getProperty("user.home"), "udongUploads").toAbsolutePath().normalize().toString();
                String filePath = path + File.separator; // 운영 체제에 맞는 구분자 추가
                System.out.println("###### 파일 저장 경로: " + filePath);

                for(int i = 0; i < 5; i++) {

                    // 기존 파일이 있어 fileNo가 0보다 크고 status가 -2로 새 이미지를 업로드한 경우
                    // 기존 fileNo를 update
                    if(fileNo[i] > 0 && status[i] == -2) {

                        // update 성공 시 기존 이미지 제거를 위해 select
                        AttachmentDTO beforeImg = newsService.selectAttachment(fileNo[i]);
                        String beforeSavedName = beforeImg.getSavedName();

                        String originFileName = imgList[i].getOriginalFilename();
                        String ext = originFileName.substring(originFileName.lastIndexOf("."));
                        String savedName = UUID.randomUUID().toString().replace("-", "") + ext;

                        AttachmentDTO newImg = new AttachmentDTO();
                        newImg.setOriginalName(originFileName);
                        newImg.setSavedName(savedName);
                        newImg.setFileNo(fileNo[i]);
                        newImg.setTypeCode("NS");
                        newImg.setTargetNo(newsNo);

                        try {

                            imgList[i].transferTo(new File(filePath + "/" + savedName));

                            int updateAttachmentResult = newsService.updateAttachment(newImg);

                            if(updateAttachmentResult == 1) {

                                // update 성공 시 기존 이미지 제거
                                new File(filePath + "/" + beforeSavedName).delete();

                            }
                            else {

                                // update 실패 시 새로 생성한 이미지 제거
                                new File(filePath + "/" + savedName).delete();

                            }

                        } catch (Exception e) {

                            e.printStackTrace();

                            // 오류 발생 시 생성한 파일을 삭제
                            new File(filePath + "/" + savedName).delete();

                            model.addAttribute("msg", "소식 수정 과정에서 문제가 발생했습니다.");

                            return "common/errorPage";

                        }


                    }
                    // 기존 파일이 있어 fileNo가 0보다 크고 status가 -3으로 이미지를 삭제한 경우
                    // 기존 fileNo를 delete
                    else if(fileNo[i] > 0 && status[i] == -3) {

                        // delete 성공 시 기존 이미지 제거를 위해 select
                        AttachmentDTO beforeImg = newsService.selectAttachment(fileNo[i]);
                        String beforeSavedName = beforeImg.getSavedName();

                        AttachmentDTO deletedImg = new AttachmentDTO();

                        deletedImg.setFileNo(fileNo[i]);
                        deletedImg.setTypeCode("NS");
                        deletedImg.setTargetNo(newsNo);

                        int deleteAttachmentResult = newsService.deleteAttachment(deletedImg);

                        if(deleteAttachmentResult == 1) {

                            // delete 성공 시 기존 이미지 제거
                            new File(filePath + "/" + beforeSavedName).delete();

                        }

                    }
                    // 기존 파일이 없이 fileNo가 0이고 status가 -2로 새 이미지를 업로드한 경우
                    // 새 이미지를 insert
                    else if(fileNo[i] == 0 && status[i] == -2) {

                        String originFileName = imgList[i].getOriginalFilename();
                        String ext = originFileName.substring(originFileName.lastIndexOf("."));
                        String savedName = UUID.randomUUID().toString().replace("-", "") + ext;

                        AttachmentDTO newImg = new AttachmentDTO();
                        newImg.setOriginalName(originFileName);
                        newImg.setSavedName(savedName);
                        newImg.setTypeCode("NS");
                        newImg.setTargetNo(newsNo);

                        try {

                            imgList[i].transferTo(new File(filePath + "/" + savedName));

                            int insertAttachmentResult = newsService.insertAttachment(newImg);

                            if(insertAttachmentResult != 1) {

                                // insert 실패 시 생성한 이미지 제거
                                new File(filePath + "/" + savedName).delete();

                            }

                        } catch (Exception e) {

                            e.printStackTrace();

                            // 오류 발생 시 생성한 파일을 삭제
                            new File(filePath + "/" + savedName).delete();

                            model.addAttribute("msg", "소식 수정 과정에서 문제가 발생했습니다.");

                            return "common/errorPage";

                        }

                    }

                }

                redirectAttributes.addFlashAttribute("alert", "소식 수정이 완료되었습니다.");
                redirectAttributes.addFlashAttribute("alertType", "success");

            }

        } catch (Exception e) {

            e.printStackTrace();
            model.addAttribute("msg", "소식 수정 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

        return "redirect:/news/newsDetail?newsNo=" + newsNo;

    }

    @PostMapping("/deleteNews")
    public String deleteNews(@AuthenticationPrincipal CustomUserDetails c, @RequestParam("newsNo") int newsNo, Model model, RedirectAttributes redirectAttributes) {

        // 삭제된 소식인지 검증
        if(!checkIsNewsDeleted(c, newsNo)) {
            return "redirect:/news/newsMain";
        }

        // 소식 작성자나 관리자인지 체크
        if(!checkNewsWriter(c, newsNo) && !c.getMemberDTO().getAuthority().equals("ROLE_ADMIN")) {
            return "redirect:/news/newsDetail?newsNo=" + newsNo;
        }

        try {

            int result = newsService.deleteNews(newsNo);

            if(result == 1) {

                redirectAttributes.addFlashAttribute("alert", "소식 삭제가 완료되었습니다.");
                redirectAttributes.addFlashAttribute("alertType", "success");

            }

        } catch (Exception e) {

            e.printStackTrace();
            model.addAttribute("msg", "소식 삭제 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

        return "redirect:/news/newsMain";

    }


    @RequestMapping("/hotNews")
    public String hotNews(@AuthenticationPrincipal CustomUserDetails c, Model model, RedirectAttributes redirectAttributes) {

        FilterDTO filterDTO = new FilterDTO();

        // 로그인 유저의 locationCode를 filterDTO에 set
        long membersLocation = c.getMemberDTO().getMemAddressDTO().getLocationCode();
        filterDTO.setLocationCode(membersLocation);

        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime recentlyTime = timeNow.minusDays(7);

        filterDTO.setRecentlyTime(recentlyTime);

        try {

            List<NewsDTO> hotNewsList = newsService.selectHotNewsList(filterDTO);
            System.out.println("###### 가져온 소식 리스트: " + hotNewsList);

            model.addAttribute("hotNewsList", hotNewsList);

            return "/news/hotNews";

        } catch (Exception e) {

            e.printStackTrace();
            model.addAttribute("msg", "인기글 조회 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

    }

    @RequestMapping("/adInsertForm")
    public String adInsertForm(@AuthenticationPrincipal CustomUserDetails c) {

        if(!c.getMemberDTO().getAuthority().equals("ROLE_ADMIN")) {
            return "redirect:/news/newsMain";
        }

        return "/news/adInsertForm";

    }

    @RequestMapping("/locationList")
    @ResponseBody
    public List<LocationDTO> locationList() {

        try {

            List<LocationDTO> locationList = newsService.selectLocationList();

            for(LocationDTO one : locationList) {

                one.setLocationName(one.getSiDoName() + " " + one.getSiGunGuName() + " " + one.getEupMyeonDongName());

            }

            return locationList;

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }

    @PostMapping("/insertAd")
    public String insertAd(@AuthenticationPrincipal CustomUserDetails c, NewsDTO newsDTO, LocationDTO locationDTO, @RequestParam("img") MultipartFile img, Model model) {

        if(!c.getMemberDTO().getAuthority().equals("ROLE_ADMIN")) {
            return "redirect:/news/newsMain";
        }

        // 로그인 유저를 작성자로 set
        MemberDTO writer = new MemberDTO();
        writer.setMemberNo(c.getMemberDTO().getMemberNo());
        newsDTO.setWriter(writer);

        newsDTO.setLocation(locationDTO);

        // 저장 경로 설정
        String path = Paths.get(System.getProperty("user.home"), "udongUploads").toAbsolutePath().normalize().toString();
        String filePath = path + File.separator; // 운영 체제에 맞는 구분자 추가
        System.out.println("###### 파일 저장 경로: " + filePath);

        List<String> savedNameList = new ArrayList<>();

        String savedName = "";

        if(!img.isEmpty()) {

            // filePath에 폴더가 없으면 폴더 생성
            File mkdir = new File(filePath);
            if(!mkdir.exists()) {
                mkdir.mkdirs();
            }

            // 첨부 파일의 이름을 가져와서
            String originFileName = img.getOriginalFilename();
            // 확장자만 잘라서 변수에 저장
            String ext = originFileName.substring(originFileName.lastIndexOf("."));
            // 랜덤한 파일이름을 생성한 후 뒤에 확장자 추가
            savedName = UUID.randomUUID().toString().replace("-", "") + ext;

            // attachmentDTO에 원래 파일명과 변경한 파일명을 set
            AttachmentDTO attachmentDTO = new AttachmentDTO();
            attachmentDTO.setOriginalName(originFileName);
            attachmentDTO.setSavedName(savedName);
            attachmentDTO.setTypeCode("AD");

            // 리스트에 그 attachmentDTO를 add
            // 이미지 한 개라 index는 0 밖에 없음
            List<AttachmentDTO> attachmentList = new ArrayList<>();
            attachmentList.add(attachmentDTO);

            // attachmentList를 clubDTO에 set
            newsDTO.setAttachments(attachmentList);

            try {

                // 이미지 파일을 생성
                img.transferTo(new File(filePath + "/" + savedName));

            } catch (Exception e) {

                e.printStackTrace();

                // 오류 발생 시 생성한 파일을 삭제
                new File(filePath + "/" + savedName).delete();

                model.addAttribute("msg", "이미지 업로드 과정에서 문제가 발생했습니다.");

                return "common/errorPage";

            }

        }

        System.out.println("###### 작성할 광고 >>>>> " + newsDTO);

        try {

            newsService.insertAd(newsDTO);
            int adNo = newsDTO.getNewsNo();

            return "redirect:/news/adDetail?adNo=" + adNo;

        } catch (Exception e) {

            e.printStackTrace();

            // 오류 발생 시 생성한 파일을 삭제
            new File(filePath + "/" + savedName).delete();

            model.addAttribute("msg", "광고 작성 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

    }

    @RequestMapping("/adDetail")
    public String adDetail(@AuthenticationPrincipal CustomUserDetails c, @RequestParam("adNo") int adNo, Model model, RedirectAttributes redirectAttributes) {

        // 삭제된 광고인지 검증
        if(!checkIsAdDeleted(c, adNo)) {
            redirectAttributes.addFlashAttribute("alert", "삭제됐거나 존재하지 않는 광고입니다.");
            redirectAttributes.addFlashAttribute("alertType", "error");
            return "redirect:/news/newsMain";
        }

        try {

            newsService.addAdViews(adNo);

            NewsDTO adDetail = newsService.selectAdDetail(adNo);

            System.out.println("###### 가져온 광고 상세 >>>>> " + adDetail);

            model.addAttribute("adDetail", adDetail);

            return "news/adDetail";

        } catch (Exception e) {

            e.printStackTrace();
            model.addAttribute("msg", "광고 상세 조회 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

    }

    @PostMapping("/deleteAd")
    public String deleteAd(@AuthenticationPrincipal CustomUserDetails c, @RequestParam("adNo") int adNo, Model model, RedirectAttributes redirectAttributes) {

        // 삭제된 광고인지 검증
        if(!checkIsAdDeleted(c, adNo)) {
            return "redirect:/news/newsMain";
        }

        // 관리자인지 체크
        if(!c.getMemberDTO().getAuthority().equals("ROLE_ADMIN")) {
            return "redirect:/news/adDetail?adNo=" + adNo;
        }

        try {

            int result = newsService.deleteAd(adNo);

            if(result == 1) {

                redirectAttributes.addFlashAttribute("alert", "광고 삭제가 완료되었습니다.");
                redirectAttributes.addFlashAttribute("alertType", "success");

            }

        } catch (Exception e) {

            e.printStackTrace();
            model.addAttribute("msg", "광고 삭제 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

        return "redirect:/news/newsMain";

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

    public boolean checkIsAdDeleted(@AuthenticationPrincipal CustomUserDetails c, int adNo) {

        try {

            String isAdDeleted = newsService.checkIsAdDeleted(adNo);

            if(isAdDeleted == null || isAdDeleted.equals("")) {

                return false;

            }

            if(isAdDeleted.equals("N") || c.getMemberDTO().getAuthority().equals("ROLE_ADMIN")) {

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
