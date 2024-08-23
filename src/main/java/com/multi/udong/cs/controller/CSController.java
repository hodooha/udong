package com.multi.udong.cs.controller;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.common.model.dto.TypeDTO;
import com.multi.udong.cs.model.dto.CSAnswerDTO;
import com.multi.udong.cs.model.dto.CSQuestionDTO;
import com.multi.udong.cs.model.dto.FAQItem;
import com.multi.udong.cs.service.CSService;
import com.multi.udong.login.service.CustomUserDetails;
import com.multi.udong.member.controller.MemberController;
import com.multi.udong.member.model.dto.PageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
    public void csMain(@RequestParam(value = "page", defaultValue = "1") int page,
                       @RequestParam(value = "category", defaultValue = "all") String category,
                       Model model) {

        int pageSize = 5;

        FAQItem[] allFaqItems = FAQItem.values();

        List<FAQItem> filteredFaqItems = new ArrayList<>();
        for (FAQItem item : allFaqItems) {
            if (category.equals("all") || item.getCategory().equals(category)) {
                filteredFaqItems.add(item);
            }
        }


        int count = filteredFaqItems.size();
        int pages = (count + pageSize - 1) / pageSize; // 올림 연산

        // 페이지 범위 계산
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, count);

        // 범위 체크 추가
        if (start >= count) {
            start = 0;
            end = Math.min(pageSize, count);
            page = 1;
        }

        FAQItem[] paginatedFaqItems = filteredFaqItems.subList(start, end).toArray(new FAQItem[0]);

        model.addAttribute("faqItems", paginatedFaqItems);
        model.addAttribute("page", page);
        model.addAttribute("pages", pages);
        model.addAttribute("currentCategory", category);
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
                           RedirectAttributes redirectAttributes,
                           Model model) {

        if (c == null) {
            redirectAttributes.addFlashAttribute("alert","로그인이 필요한 기능입니다.");
            redirectAttributes.addFlashAttribute("alertType", "error");
            return "redirect:/cs/csMain";
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

        List<String> headers = Arrays.asList("문의유형", "제목", "작성일자", "답변여부");

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
     * @param page       the page
     * @param searchWord the search word
     * @param model      the model
     * @return the string
     * @since 2024 -08-01
     */
    @GetMapping("/csAllQue")
    public String csAllQue (@RequestParam(value = "page", defaultValue = "1") int page,
                            @RequestParam(value = "searchWord", required = false) String searchWord,
                            Model model) {

        PageDTO pageDTO = new PageDTO();
        pageDTO.setPage(page);
        pageDTO.setStartEnd(page);

        pageDTO.setSearchWord(searchWord);

        int count;
        int pages = 1;

        List<List<String>> data = csService.selectAllQue(pageDTO);

        if(!data.isEmpty()) {

            count = Integer.parseInt(data.get(0).get(data.get(0).size() - 1));
            pages = (count % 10 == 0) ? count / 10 : count / 10 + 1;

            for (List<String> list : data) {
                list.remove(list.size() - 1);
            }
        }

        List<String> headers = Arrays.asList("문의유형", "제목", "작성자", "작성일자", "답변여부");

        model.addAttribute("tableHeaders", headers);
        model.addAttribute("tableData", data);
        model.addAttribute("page", pageDTO.getPage());
        model.addAttribute("pages", pages);
        model.addAttribute("searchWord", searchWord);

        return "cs/csAllQue";
    }

    /**
     * Insert que form.
     *
     * @param c     the c
     * @param model the model
     * @return the string
     * @since 2024 -08-01
     */
    @GetMapping("/insertQueForm")
    public String insertQueForm (@AuthenticationPrincipal CustomUserDetails c,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        if (c == null) {
            redirectAttributes.addFlashAttribute("alert","로그인이 필요한 기능입니다.");
            redirectAttributes.addFlashAttribute("alertType", "error");
            return "redirect:/cs/csMain";
        }

        List<TypeDTO> csTypes = csService.getAllTypes();

        model.addAttribute("csTypes", csTypes);
        return "cs/insertQueForm";
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
    @GetMapping("/queDetail")
    public String queDetail (@AuthenticationPrincipal CustomUserDetails c,
                             @RequestParam("no") int csNo,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        int memberNo = c.getMemberDTO().getMemberNo();

        Map<String, Object> map = csService.getQueDetail(csNo);
        if (map == null) {
            redirectAttributes.addFlashAttribute("alert", "삭제된 문의입니다.");
            redirectAttributes.addFlashAttribute("alertType", "error");
            return "redirect:/cs/csMain";
        }

        CSQuestionDTO csQuestionDTO = (CSQuestionDTO) map.get("csQuestionDTO");

        if (memberNo != csQuestionDTO.getWriterNo() && !c.getMemberDTO().getAuthority().equals("ROLE_ADMIN")) {
            redirectAttributes.addFlashAttribute("alert", "잘못된 접근입니다.");
            redirectAttributes.addFlashAttribute("alertType", "error");
            return "redirect:/cs/csMain";
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
     * @param c                  the c
     * @param files              the files
     * @param csQuestionDTO      the cs question dto
     * @param redirectAttributes the redirect attributes
     * @return the string
     * @since 2024 -08-01
     */
    @PostMapping("/insertQueForm")
    public String insertQueForm (@AuthenticationPrincipal CustomUserDetails c,
                                 @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                 CSQuestionDTO csQuestionDTO,
                                 RedirectAttributes redirectAttributes) {

        int memberNo = c.getMemberDTO().getMemberNo();
        csQuestionDTO.setWriterNo(memberNo);

        List<AttachmentDTO> attachments = new ArrayList<>();

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    AttachmentDTO attachmentDTO = memberController.settingFile(file);
                    attachments.add(attachmentDTO);
                }
            }
        }

        csService.insertQueForm(csQuestionDTO, attachments);

        redirectAttributes.addFlashAttribute("alert", "문의 등록이 완료되었습니다.");
        redirectAttributes.addFlashAttribute("alertType", "success");
        return "redirect:/cs/csMyQue";
    }

    /**
     * Insert answer que response entity.
     *
     * @param c           the c
     * @param csAnswerDTO the cs answer dto
     * @return the response entity
     * @since 2024 -08-02
     */
    @PostMapping("/answerQue")
    @ResponseBody
    public ResponseEntity<?> insertAnswerQue(@AuthenticationPrincipal CustomUserDetails c,
                                             @RequestBody CSAnswerDTO csAnswerDTO) {
        try {
            String authority = c.getMemberDTO().getAuthority();
            int memberNo = c.getMemberDTO().getMemberNo();
            csAnswerDTO.setAnswererNo(memberNo);

            String createdAt  = csService.insertAnswerQue(csAnswerDTO, authority);
            csAnswerDTO.setCreatedAt(createdAt);

            Map<String, Object> response = new HashMap<>();
            response.put("answer", csAnswerDTO);
            response.put("nickname", c.getMemberDTO().getNickname());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred: " + e.getMessage());
        }
    }

    /**
     * Delete que string.
     *
     * @param csNo               the cs no
     * @param redirectAttributes the redirect attributes
     * @return the string
     * @since 2024 -08-14
     */
    @PostMapping("/deleteQue")
    public String deleteQue(@AuthenticationPrincipal CustomUserDetails c,
                            @RequestParam("csNo") int csNo, RedirectAttributes redirectAttributes) {

        String authority = c.getMemberDTO().getAuthority();

        String result = csService.deleteQue(csNo, authority);

        System.out.println("##result + " + result);

        if (result.equals("answered")) {
            redirectAttributes.addFlashAttribute("alert", "답변이 완료된 문의는 삭제할 수 없습니다.");
            redirectAttributes.addFlashAttribute("alertType", "error");
            return "redirect:/cs/queDetail?no=" + csNo;
        } else {
            redirectAttributes.addFlashAttribute("alert", "문의글이 삭제되었습니다.");
            redirectAttributes.addFlashAttribute("alertType", "error");
            return "redirect:/cs/csMyQue";
        }
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

        String path = Paths.get(System.getProperty("user.home"), "udongUploads").toAbsolutePath().normalize().toString() + File.separator;
        Path filePath =  Paths.get(path + attachmentDTO.getSavedName());
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
