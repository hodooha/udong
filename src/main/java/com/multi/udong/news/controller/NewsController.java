package com.multi.udong.news.controller;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.common.model.dto.LocationDTO;
import com.multi.udong.login.service.CustomUserDetails;
import com.multi.udong.news.model.dto.CategoryDTO;
import com.multi.udong.news.model.dto.FilterDTO;
import com.multi.udong.news.model.dto.MemberDTO;
import com.multi.udong.news.model.dto.NewsDTO;
import com.multi.udong.news.service.NewsService;
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

            return "redirect:/news/newsMain";
            // return "redirect:/news/newsDetail?newsNo=" + newsNo;

        } catch (Exception e) {

            e.printStackTrace();

            // 오류 발생 시 생성한 파일을 삭제
            for(String savedName : savedNameList) {

                new File(filePath + "/" + savedName).delete();

            }

            model.addAttribute("msg", "우동 소식 작성 과정에서 문제가 발생했습니다.");

            return "common/errorPage";

        }

    }

}
