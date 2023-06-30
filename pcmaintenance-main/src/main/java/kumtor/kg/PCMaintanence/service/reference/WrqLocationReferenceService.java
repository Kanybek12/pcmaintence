package kumtor.kg.PCMaintanence.service.reference;

import kumtor.kg.PCMaintanence.dto.reference.InternalReferenceDto;
import kumtor.kg.PCMaintanence.dto.ResponseDto;

import java.util.List;

public interface WrqLocationReferenceService {

    ResponseDto saveWrqLocation(InternalReferenceDto internalReferenceDto, String username);

    List<InternalReferenceDto> getWrqLocations();

    ResponseDto updateWrqLocation(Integer id, InternalReferenceDto internalReferenceDto, String username);
}
