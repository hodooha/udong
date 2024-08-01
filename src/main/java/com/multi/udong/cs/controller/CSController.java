package com.multi.udong.cs.controller;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.common.model.dto.TypeDTO;
import com.multi.udong.cs.model.dto.CSAnswerDTO;
import com.multi.udong.cs.model.dto.CSQuestionDTO;
import com.multi.udong.cs.service.CSService;
import com.multi.udong.member.controller.MemberController;
import com.multi.udong.member.model.dto.PageDTO;
import com.multi.udong.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The type Cs controller.
 *
 * @author 김재식
 * @since 2024 -07-31
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/cs")
public class CSController {

    private final CSService csService;
    private final MemberController memberController;


    /**
     * Cs main.
     *
     * @param model the model
     * @since 2024 -08-01
     */
    @GetMapping("/csMain")
    public void csMain (Model model) {
        model.addAttribute("msg", "고객센터입니다.");
    }

    /**
     * Cs my que.
     *
     * @param c          the c
     * @param page       the page
     * @param searchWord the search word
     * @param model      the model
     * @return the string
     * @since 2024 -08-01
     */
    @GetMapping("/csMyQue")
    public String csMyQue (@AuthenticationPrincipal CustomUserDetails c,
                           @RequestParam(value = "page", defaultValue = "1") int page,
                           @RequestParam(value = "searchWord", required = false) String searchWord,
                           Model model) {

        if (c == null) {
            model.addAttribute("msg","로그인이 필요한 기능입니다.");
            return "cs/csMain";
        }

        int memberNo = c.getMemberDTO().getMemberNo();
        PageDTO pageDTO = new PageDTO();
        pageDTO.setPage(page);
        pageDTO.setMemberNo(memberNo);
        pageDTO.setStartEnd(page);

        pageDTO.setSearchWord(searchWord);

        int count;
        int pages = 1;

        List<List<String>> data = csService.selectQue(pageDTO);

        if(!data.isEmpty()) {

            count = Integer.parseInt(data.get(0).get(data.get(0).size() - 1));
            pages = (count % 10 == 0) ? count / 10 : count / 10 + 1;

            for (List<String> list : data) {
                list.remove(list.size() - 1);
            }
        }

        List<String> headers = Arrays.asList("문의유형", "제목", "내용", "작성일자", "답변여부");

        model.addAttribute("tableHeaders", headers);
        model.addAttribute("tableData", data);
        model.addAttribute("page", pageDTO.getPage());
        model.addAttribute("pages", pages);
        model.addAttribute("searchWord", searchWord);

        return "cs/csMyQue";
    }

    /**
     * Cs all que.
     *
     * @since 2024 -08-01
     */
    @GetMapping("/csAllQue")
    public String csAllQue (@AuthenticationPrincipal CustomUserDetails c,
                            @RequestParam(value = "page", defaultValue = "1") int page,
                            @RequestParam(value = "searchWord", required = false) String searchWord,
                            Model model) {

        if (c == null) {
            model.addAttribute("msg","로그인이 필요한 기능입니다.");
            return "cs/csMain";
        }

        int memberNo = c.getMemberDTO().getMemberNo();
        PageDTO pageDTO = new PageDTO();
        pageDTO.setPage(page);
        pageDTO.setMemberNo(memberNo);
        pageDTO.setStartEnd(page);

        pageDTO.setSearchWord(searchWord);

        int count;
        int pages = 1;

        List<List<String>> data = csService.selectQue(pageDTO);

        if(!data.isEmpty()) {

            count = Integer.parseInt(data.get(0).get(data.get(0).size() - 1));
            pages = (count % 10 == 0) ? count / 10 : count / 10 + 1;

            for (List<String> list : data) {
                list.remove(list.size() - 1);
            }
        }

        List<String> headers = Arrays.asList("문의유형", "제목", "내용", "작성일자", "답변여부");

        model.addAttribute("tableHeaders", headers);
        model.addAttribute("tableData", data);
        model.addAttribute("page", pageDTO.getPage());
        model.addAttribute("pages", pages);
        model.addAttribute("searchWord", searchWord);

        return "cs/csMyQue";
    }

    /**
     * Insert que form.
     *
     * @param model the model
     * @since 2024 -08-01
     */
    @GetMapping("/insertQueForm")
    public void insertQueForm (Model model) {

        List<TypeDTO> csTypes = csService.getAllTypes();

        model.addAttribute("csTypes", csTypes);
    }

    /**
     * Cs all que.
     *
     * @param c     the c
     * @param csNo  the cs no
     * @param model the model
     * @return the string
     * @since 2024 -08-01
     */
    @GetMapping("/detail/{csNo}")
    public String queDetail (@AuthenticationPrincipal CustomUserDetails c,
                             @PathVariable("csNo") int csNo,
                             Model model) {

        int memberNo = c.getMemberDTO().getMemberNo();

        Map<String, Object> map = csService.getQueDetail(csNo);

        CSQuestionDTO csQuestionDTO = (CSQuestionDTO) map.get("csQuestionDTO");

        if (memberNo != csQuestionDTO.getWriterNo() && !c.getMemberDTO().getAuthority().equals("ROLE_ADMIN")) {
            model.addAttribute("msg", "권한이 부족합니다.");
            return "cs/csMain";
        }

        List<AttachmentDTO> attachments = (List<AttachmentDTO>) map.get("attachments");
        List<CSAnswerDTO> answers = (List<CSAnswerDTO>) map.get("answers");

        model.addAttribute("que", csQuestionDTO);
        model.addAttribute("attachments", attachments);
        model.addAttribute("answers", answers);
        return "cs/queDetail";
    }

    /**
     * Insert que form.
     *
     * @param c             the c
     * @param files         the files
     * @param csQuestionDTO the cs question dto
     * @param model         the model
     * @return the string
     * @since 2024 -08-01
     */
    @PostMapping("/insertQueForm")
    public String insertQueForm (@AuthenticationPrincipal CustomUserDetails c,
                                 @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                 CSQuestionDTO csQuestionDTO,
                                 Model model) {

        int memberNo = c.getMemberDTO().getMemberNo();
        csQuestionDTO.setWriterNo(memberNo);

        List<AttachmentDTO> attachments = new ArrayList<>();

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    AttachmentDTO attachmentDTO = memberController.settingFile(file);
                    attachmentDTO.setTypeCode("CS");
                    attachments.add(attachmentDTO);
                }
            }
        }

        csService.insertQueForm(csQuestionDTO, attachments);

        model.addAttribute("msg", "문의글 등록이 완료되었습니다.");
        return "redirect:csMyQue";
    }

    @PostMapping("/answerQue")
    public String insertAnswerQue (@AuthenticationPrincipal CustomUserDetails c,
                                   CSAnswerDTO csAnswerDTO,
                                   Model model) {

        int memberNo = c.getMemberDTO().getMemberNo();

        csAnswerDTO.setAnswererNo(memberNo);

        System.out.println("csAnswerDTO : " + csAnswerDTO);

        csService.insertAnswerQue(csAnswerDTO);

        int csNo = csAnswerDTO.getCsNo();

        return "redirect:cs/detail/" + csNo;
    }

    /**
     * Download file response entity.
     *
     * @param fileNo the file no
     * @return the response entity
     * @throws IOException the io exception
     * @since 2024 -08-01
     */
    @GetMapping("/download/{fileNo}")
    public ResponseEntity<Resource> downloadFile (@PathVariable("fileNo") int fileNo) throws IOException {
        AttachmentDTO attachmentDTO = csService.getAttachment(fileNo);

        Path filePath = Paths.get("C:\\workspace\\local\\udong\\src\\main\\resources\\static\\uploadFiles\\" + attachmentDTO.getSavedName());
        Resource resource = new UrlResource(filePath.toUri());

        if(!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachmentDTO; filename=\"" + attachmentDTO.getOriginalName() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }
}