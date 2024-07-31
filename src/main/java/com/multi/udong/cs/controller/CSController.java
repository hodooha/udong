package com.multi.udong.cs.controller;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.common.model.dto.TypeDTO;
import com.multi.udong.cs.model.dto.CSQuestionDTO;
import com.multi.udong.cs.service.CSService;
import com.multi.udong.member.controller.MemberController;
import com.multi.udong.member.model.dto.PageDTO;
import com.multi.udong.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

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
     * @since 2024 -08-01
     */
    @GetMapping("/csMyQue")
    public void csMyQue (@AuthenticationPrincipal CustomUserDetails c,
                         @RequestParam(value = "page", defaultValue = "1") int page,
                         @RequestParam(value = "searchWord", required = false) String searchWord,
                         Model model) {

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
    }

    /**
     * Cs all que.
     *
     * @since 2024 -08-01
     */
    @GetMapping("/csAllQue")
    public void csAllQue () {}

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
     * Insert que form.
     *
     * @param c             the c
     * @param file          the file
     * @param csQuestionDTO the cs question dto
     * @param model         the model
     * @return the string
     * @since 2024 -08-01
     */
    @PostMapping("/insertQueForm")
    public String insertQueForm (@AuthenticationPrincipal CustomUserDetails c,
                               @RequestParam(value = "file", required = false)MultipartFile file,
                               CSQuestionDTO csQuestionDTO,
                               Model model) {

        int memberNo = c.getMemberDTO().getMemberNo();
        csQuestionDTO.setWriterNo(memberNo);

        AttachmentDTO attachmentDTO = null;

        if (file != null && !file.isEmpty()) {

            attachmentDTO = memberController.settingFile(file);
            attachmentDTO.setTargetNo(csQuestionDTO.getCsNo());
            attachmentDTO.setTypeCode("CS");

        }

        csService.insertQueForm(csQuestionDTO, attachmentDTO);

        model.addAttribute("msg", "문의글 등록이 완료되었습니다.");
        return "redirect:csMyQue";
    }
}
