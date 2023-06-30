package kumtor.kg.PCMaintanence.api.reference;

import kumtor.kg.PCMaintanence.dto.reference.InternalReferenceDto;
import kumtor.kg.PCMaintanence.dto.ResponseDto;
import kumtor.kg.PCMaintanence.service.auth.AuthenticationService;
import kumtor.kg.PCMaintanence.service.reference.WrqPriorityReferenceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@Validated
@RequestMapping("/reference/internal/priority")
public class WrqPriorityReferenceController {

    private final WrqPriorityReferenceService wrqPriorityReferenceService;
    private final AuthenticationService authService;

    public WrqPriorityReferenceController(WrqPriorityReferenceService wrqPriorityReferenceService, AuthenticationService authService) {
        this.wrqPriorityReferenceService = wrqPriorityReferenceService;
        this.authService = authService;
    }

    @PostMapping("/")
    public ResponseEntity<ResponseDto> saveWrqPriority(@RequestBody @Valid InternalReferenceDto referenceDto, Principal principal) {
        return new ResponseEntity<>(wrqPriorityReferenceService.saveWrqPriorityReference(referenceDto, authService.getUsername(principal)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<InternalReferenceDto>> getWrqPriorityReference() {
        return new ResponseEntity<>(wrqPriorityReferenceService.getWrqPriorityReference(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateWrqPriority(@PathVariable Integer id, @RequestBody @Valid InternalReferenceDto referenceDto, Principal principal) {
        return new ResponseEntity<>(wrqPriorityReferenceService.updateWrqPriority(id, referenceDto, authService.getUsername(principal)), HttpStatus.OK);
    }
}
