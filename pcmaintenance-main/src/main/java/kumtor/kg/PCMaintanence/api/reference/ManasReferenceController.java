package kumtor.kg.PCMaintanence.api.reference;

import kumtor.kg.PCMaintanence.dto.reference.AssetDto;
import kumtor.kg.PCMaintanence.dto.reference.ManasReferenceDto;
import kumtor.kg.PCMaintanence.service.reference.ManasReferenceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reference/manas")
public class ManasReferenceController {

    private final ManasReferenceService manasReferenceService;

    public ManasReferenceController(ManasReferenceService manasReferenceService) {
        this.manasReferenceService = manasReferenceService;
    }

    @GetMapping("/asset")
    public ResponseEntity<List<AssetDto>> getAssetReference() {
        return new ResponseEntity<>(manasReferenceService.getAssetReference(), HttpStatus.OK);
    }

    @GetMapping("/wrq")
    public ResponseEntity<List<ManasReferenceDto>> getWrqReference(@RequestParam(value = "assetCode", required = false) String assetCode) {
        return new ResponseEntity<>(manasReferenceService.getWrqReference(assetCode), HttpStatus.OK);
    }

    @GetMapping("/asset-type")
    public ResponseEntity<List<ManasReferenceDto>> getAssetTypeReference(){
        return new ResponseEntity<>(manasReferenceService.getAssetTypeReference(), HttpStatus.OK);
    }

}
