package kumtor.kg.PCMaintanence.api.reference;

import kumtor.kg.PCMaintanence.dto.reference.InternalReferenceDto;
import kumtor.kg.PCMaintanence.dto.ResponseDto;
import kumtor.kg.PCMaintanence.service.auth.AuthenticationService;
import kumtor.kg.PCMaintanence.service.reference.WrqOnHoldReferenceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@Validated
@RequestMapping("/reference/internal/on-hold")
public class WrqOnHoldReferenceController {

    private final WrqOnHoldReferenceService wrqOnHoldReferenceService;
    private final AuthenticationService authService;

    public WrqOnHoldReferenceController(WrqOnHoldReferenceService wrqOnHoldReferenceService, AuthenticationService authService) {
        this.wrqOnHoldReferenceService = wrqOnHoldReferenceService;
        this.authService = authService;
    }

    @PostMapping("/")
    public ResponseEntity<ResponseDto> saveWrqOnHold(@RequestBody @Valid InternalReferenceDto referenceDto,
                                                     Principal principal){
        return new ResponseEntity<>(wrqOnHoldReferenceService.saveWrqOnHoldReference(referenceDto, authService.getUsername(principal)), HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<List<InternalReferenceDto>> getWrqOnHoldReference(){
        return new ResponseEntity<>(wrqOnHoldReferenceService.getWrqOnHoldReference(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateWrqOnHold(@PathVariable Integer id,
                                                       @RequestBody @Valid InternalReferenceDto referenceDto,
                                                       Principal principal){
        return new ResponseEntity<>(wrqOnHoldReferenceService.updateWrqOnHold(id, referenceDto, authService.getUsername(principal)), HttpStatus.OK);
    }
}
