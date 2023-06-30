package kumtor.kg.PCMaintanence.service.reference;

import kumtor.kg.PCMaintanence.dto.reference.AssetDto;
import kumtor.kg.PCMaintanence.dto.reference.ManasReferenceDto;

import java.util.List;

public interface ManasReferenceService {

    ManasReferenceDto getAssetType(String assetCode);
    List<ManasReferenceDto> getAssetTypeReference();
    List<ManasReferenceDto> getWrqReference(String assetCode);
    List<AssetDto> getAssetReference();
}
