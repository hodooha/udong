package com.multi.udong.sale.controller;

import com.multi.udong.sale.model.dto.SaleDTO;
import com.multi.udong.sale.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Controller
@RequestMapping("/sale")
public class SaleController {

    private final SaleService saleService;

    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping("/saleMain")
    public String saleMain(Model model) {
        return "sale/saleMain";
    }

    @RequestMapping("saleInsertForm")
    public void saleInsertForm() {

    }

    @PostMapping("/insert")
    public String insertSale(@ModelAttribute SaleDTO saleDTO,
                             @RequestParam(value = "startedAtCombined", required = false) String startedAtCombined,
                             @RequestParam(value = "endedAtCombined", required = false) String endedAtCombined) {


        saleDTO.setWriter(1);  // 임의의 사용자 ID

        if (startedAtCombined != null && endedAtCombined != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                LocalDateTime startedAt = LocalDateTime.parse(startedAtCombined, formatter);
                LocalDateTime endedAt = LocalDateTime.parse(endedAtCombined, formatter);

                saleDTO.setStartedAt(startedAt);
                saleDTO.setEndedAt(endedAt);
                System.out.println("StartedAt set to: " + startedAt);
                System.out.println("EndedAt set to: " + endedAt);
            } catch (DateTimeParseException e) {
                System.err.println("Error parsing dates: " + e.getMessage());
                return "redirect:/error";
            }
        }

        try {
            saleService.insertSale(saleDTO);
            System.out.println("Sale inserted successfully");
        } catch (Exception e) {
            System.err.println("Error inserting sale: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/error";
        }
        return "redirect:/sale/saleMain";
    }
}