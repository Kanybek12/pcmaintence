package kumtor.kg.PCMaintanence.api.reference;

import kumtor.kg.PCMaintanence.dto.reference.InternalReferenceDto;
import kumtor.kg.PCMaintanence.dto.ResponseDto;
import kumtor.kg.PCMaintanence.service.auth.AuthenticationService;
import kumtor.kg.PCMaintanence.service.reference.WrqLocationReferenceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@Validated
@RequestMapping("/reference/internal/location")
public class WrqLocationReferenceController {

    private final WrqLocationReferenceService wrqLocationReferenceService;
    private final AuthenticationService authService;

    public WrqLocationReferenceController(WrqLocationReferenceService wrqLocationReferenceService, AuthenticationService authService) {
        this.wrqLocationReferenceService = wrqLocationReferenceService;
        this.authService = authService;
    }

    @PostMapping("/")
    public ResponseEntity<ResponseDto> saveWrqLocation(@RequestBody @Valid InternalReferenceDto referenceDto,
                                                       Principal principal){
        return new ResponseEntity<>(wrqLocationReferenceService.saveWrqLocation(referenceDto, authService.getUsername(principal)), HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<List<InternalReferenceDto>> getWrqLocationReference(){
        return new ResponseEntity<>(wrqLocationReferenceService.getWrqLocations(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateWrqLocation(@PathVariable Integer id,
                                                         @RequestBody @Valid InternalReferenceDto referenceDto,
                                                         Principal principal){
        return new ResponseEntity<>(wrqLocationReferenceService.updateWrqLocation(id, referenceDto, authService.getUsername(principal)), HttpStatus.OK);
    }
}
