package kumtor.kg.PCMaintanence.api.reference;

import kumtor.kg.PCMaintanence.dto.reference.InternalReferenceDto;
import kumtor.kg.PCMaintanence.dto.ResponseDto;
import kumtor.kg.PCMaintanence.service.auth.AuthenticationService;
import kumtor.kg.PCMaintanence.service.reference.WrqReasonReferenceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.security.Principal;
import java.util.List;

@RestController
@Validated
@RequestMapping("/reference/internal/reason")
public class WrqReasonReferenceController {

    private final WrqReasonReferenceService wrqReasonReferenceService;
    private final AuthenticationService authService;

    public WrqReasonReferenceController(WrqReasonReferenceService wrqReasonReferenceService, AuthenticationService authService) {
        this.wrqReasonReferenceService = wrqReasonReferenceService;
        this.authService = authService;
    }

    @PostMapping("/")
    public ResponseEntity<ResponseDto> saveWrqReason(@RequestBody @Valid InternalReferenceDto referenceDto,
                                                     Principal principal){
        return new ResponseEntity<>(wrqReasonReferenceService.saveWrqReasonReference(referenceDto, authService.getUsername(principal)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<InternalReferenceDto>> getWrqReasonReference(){
        return new ResponseEntity<>(wrqReasonReferenceService.getWrqReasonReference(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateWrqReason(@PathVariable Integer id,
                                                       @RequestBody @Valid InternalReferenceDto referenceDto,
                                                       Principal principal){
        return new ResponseEntity<>(wrqReasonReferenceService.updateWrqReason(id, referenceDto, authService.getUsername(principal)), HttpStatus.OK);
    }
}
