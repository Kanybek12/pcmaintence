package kumtor.kg.PCMaintanence.service.reference;

import kumtor.kg.PCMaintanence.dto.reference.InternalReferenceDto;
import kumtor.kg.PCMaintanence.dto.ResponseDto;

import java.util.List;

public interface WrqOnHoldReferenceService {

    ResponseDto saveWrqOnHoldReference(InternalReferenceDto internalReferenceDto, String username);

    List<InternalReferenceDto> getWrqOnHoldReference();

    ResponseDto updateWrqOnHold(Integer id, InternalReferenceDto internalReferenceDto, String username);
}
