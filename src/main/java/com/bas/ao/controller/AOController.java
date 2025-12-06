package com.bas.ao.controller;




import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bas.ao.services.AOService;
import com.bas.authentication.dto.EBillDTO;
import com.bas.authentication.dto.WorkResponseDTO;
import com.bas.model.EBill;
import com.bas.model.EBillStatus;
import com.bas.repository.WorkRepository;

@RestController
@RequestMapping("/account")
public class AOController {

	@Autowired
    private AOService aoService;
	
	@Autowired
	  private  WorkRepository workRepository;

    // 1️⃣ Get Work Details (Items + Qty + Total)
    @GetMapping("/work/{workId}")
    public WorkResponseDTO getWorkDetails(@PathVariable String workId) {
        return aoService.getWorkDetails(workId);
    }

    // 2️⃣ Save E-Bill (After AO reviews)
    @PostMapping("/ebill")
    public String saveEBill(@RequestBody WorkResponseDTO workDTO) {
        return aoService.saveEBill(workDTO);
    }
    
    @PostMapping("/ebill/{workId}")
    public ResponseEntity<?> getEBill(@PathVariable String workId) {
        return  ResponseEntity.ok( aoService.getApproved(workId)
);
    }
    
    @GetMapping("/ebill")
public List<EBillDTO> getAllEbill() {
    	
    	return  aoService.getAllEbill();
    }
    
    @PostMapping("/approve/{workId}")
    public ResponseEntity<?> approveBill(@PathVariable String workId) {
        return ResponseEntity.ok(aoService.approveBill(workId));
    }

    
    @PostMapping("/reject/{workId}")
    public ResponseEntity<?> rejectBill(
            @PathVariable String workId) {

    	
    	
        return ResponseEntity.ok(aoService.rejectBill(workId));
    }
    
    @PostMapping("/checkworkid/{username}")
    public ResponseEntity<?> findByContractorName(@PathVariable String username){System.out.println(username);
    	return ResponseEntity.ok(workRepository.findWorkIdsWhereUsernameMatchesContractorName(username));
    }
}
