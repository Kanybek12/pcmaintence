package kumtor.kg.PCMaintanence.api;

import kumtor.kg.PCMaintanence.dto.*;
import kumtor.kg.PCMaintanence.service.auth.AuthenticationService;
import kumtor.kg.PCMaintanence.service.request.PMRequestDetailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/pm-requests")
@Validated
public class PMRequestDetailController {

    private final PMRequestDetailService pmRequestDetailService;
    private final AuthenticationService authService;

    public PMRequestDetailController(PMRequestDetailService pmRequestDetailService,
                                     AuthenticationService authService) {
        this.pmRequestDetailService = pmRequestDetailService;
        this.authService = authService;
    }

    @PostMapping("/{requestId}/details/")
    public ResponseEntity<ResponseDto> saveRequestDetail(@PathVariable(value = "requestId") Long requestId,
                                                         @RequestBody @Valid CreatePMRequestStageDto pmRequestDetailDto,
                                                         Principal principal) throws Exception {
        return new ResponseEntity<>(pmRequestDetailService.saveRequestDetail(requestId, pmRequestDetailDto, authService.getUsername(principal)), HttpStatus.OK);
    }

    @PutMapping("/{requestId}/details/{stageId}")
    public ResponseEntity<ResponseDto> updateRequestDetail(@PathVariable(value = "requestId") Long requestId,
                                                           @PathVariable(value = "stageId") Long stageId,
                                                           @RequestBody @Valid UpdatePMRequestDetailDto pmRequestDetailDto,
                                                           Principal principal) throws Exception {
        return new ResponseEntity<>(pmRequestDetailService.updateRequestDetail(requestId, stageId, pmRequestDetailDto, authService.getUsername(principal)), HttpStatus.OK);
    }

    @DeleteMapping("/{requestId}/details/{stageId}")
    public ResponseEntity<ResponseDto> deleteRequestDetail(
            Principal principal,
            @PathVariable(value = "requestId") Long requestId,
            @PathVariable(value = "stageId") Long stageId,
            @RequestBody CancelStageDto cancelStageDto) throws Exception {
        return new ResponseEntity<>(pmRequestDetailService.deleteRequestDetail(requestId, stageId, cancelStageDto, authService.getUsername(principal)), HttpStatus.OK);
    }

    @GetMapping("/{requestId}/details")
    public ResponseEntity<List<PMRequestStageDto>> getPmRequestDetails(Principal principal,
                                                                       @PathVariable(value = "requestId") Long requestId
    ) {
        return new ResponseEntity<>(pmRequestDetailService.getRequestDetails(requestId, authService.getUsername(principal)), HttpStatus.OK);
    }
}
