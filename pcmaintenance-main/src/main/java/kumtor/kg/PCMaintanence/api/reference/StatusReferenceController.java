package kumtor.kg.PCMaintanence.api.reference;

import kumtor.kg.PCMaintanence.dto.StatusDto;
import kumtor.kg.PCMaintanence.service.reference.StatusReferenceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reference/internal")
public class StatusReferenceController {
    private final StatusReferenceService statusReferenceService;

    public StatusReferenceController(StatusReferenceService statusReferenceService) {
        this.statusReferenceService = statusReferenceService;
    }

    @GetMapping("/status")
    public ResponseEntity<List<StatusDto>> getRequestStatusReference(){
        return new ResponseEntity<>(statusReferenceService.getRequestStatusReference(), HttpStatus.OK);
    }

    @GetMapping("/stage")
    public ResponseEntity<List<StatusDto>> getRequestDetailStageReference(){
        return new ResponseEntity<>(statusReferenceService.getRequestDetailStageReference(), HttpStatus.OK);
    }
}
