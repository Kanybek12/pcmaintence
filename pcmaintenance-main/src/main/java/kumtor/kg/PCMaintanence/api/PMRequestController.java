package kumtor.kg.PCMaintanence.api;

import kumtor.kg.PCMaintanence.dto.CreatePMRequestDto;
import kumtor.kg.PCMaintanence.dto.PMRequestDto;
import kumtor.kg.PCMaintanence.dto.ResponseDto;
import kumtor.kg.PCMaintanence.service.auth.AuthenticationService;
import kumtor.kg.PCMaintanence.service.request.PMRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import javax.validation.constraints.Max;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pm-requests")
@Validated
public class PMRequestController {

    private final PMRequestService pmRequisitionService;
    private final AuthenticationService authService;

    public PMRequestController(PMRequestService pmRequisitionService, AuthenticationService authService) {
        this.pmRequisitionService = pmRequisitionService;
        this.authService = authService;
    }

    @PostMapping("/")
    public ResponseEntity<ResponseDto> savePMRequest(@RequestBody @Valid CreatePMRequestDto pmRequisitionDto,
                                                     Principal principal) throws Exception {
        return new ResponseEntity<>(pmRequisitionService.savePMRequest(pmRequisitionDto, authService.getUsername(principal)), HttpStatus.OK);
    }

    @PutMapping("/{requestId}")
    public ResponseEntity<ResponseDto> updatePMRequest(@PathVariable(value = "requestId") Long requestId,
                                                       @RequestBody @Valid CreatePMRequestDto pmRequestDto,
                                                       Principal principal) throws Exception {
        return new ResponseEntity<>(pmRequisitionService.updatePMRequest(requestId, pmRequestDto, authService.getUsername(principal)), HttpStatus.OK);
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<ResponseDto> deletePMRequest(@PathVariable(value = "requestId") Long requestId, Principal principal) throws Exception {
        return new ResponseEntity<>(pmRequisitionService.deletePMRequest(requestId, authService.getUsername(principal)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> filterPmRequests(Principal principal,
                                                                @RequestParam(value = "page", defaultValue = "1") int page,
                                                                @RequestParam(value = "size", defaultValue = "10") @Max(value = 30) int size,
                                                                @RequestParam(value = "assetTypeCode", required = false) List<String> assetTypeCode,
                                                                @RequestParam(value = "statusCode", required = false) List<Integer> statusCodeList,
                                                                @RequestParam(value = "assetCode", required = false) String assetCode,
                                                                @RequestParam(value = "wrqReasonId", required = false) Integer wrqReasonId,
                                                                @RequestParam(value = "wrqNo", required = false) String wrqNo,
                                                                @RequestParam(value = "wrqTitle", required = false) String wrqTitle,
                                                                @RequestParam(value = "wrqPriorityId", required = false) Integer wrqPriorityId) throws Exception {
        return new ResponseEntity<>(pmRequisitionService.getAllPmRequests(authService.getUsername(principal), page, size, assetTypeCode, statusCodeList, assetCode, wrqReasonId, wrqNo, wrqTitle, wrqPriorityId), HttpStatus.OK);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<PMRequestDto> getPmRequest(Principal principal, @PathVariable(value = "requestId") Long requestId){
        return new ResponseEntity<>(pmRequisitionService.getPmRequestById(authService.getUsername(principal), requestId), HttpStatus.OK);
    }
}
