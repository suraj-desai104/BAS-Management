package com.bas.controller;


import com.bas.ao.services.AOService;
import com.bas.authentication.dto.EBillDTO;
import com.bas.authentication.dto.EBillReductionDTO;
import com.bas.model.EBill;
import com.bas.service.EBillReductionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ebill")
public class EBillController {

    private final EBillReductionService reductionService;
    
    @Autowired
    private AOService aoService;

    public EBillController(EBillReductionService reductionService) {
        this.reductionService = reductionService;
    }

    @PostMapping("/applyReduction")
    public EBillDTO applyReduction(@RequestBody EBillReductionDTO dto) throws JsonMappingException, JsonProcessingException {
        return reductionService.applyReduction(
                dto.getBillId(),
                dto.getTotalReduction(),
                dto.getCgst(),
                dto.getSgst(),
                dto.getRoyalty()
        );
    }


}
