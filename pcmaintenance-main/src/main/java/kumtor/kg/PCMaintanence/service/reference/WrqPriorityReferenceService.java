package kumtor.kg.PCMaintanence.service.reference;

import kumtor.kg.PCMaintanence.dto.reference.InternalReferenceDto;
import kumtor.kg.PCMaintanence.dto.ResponseDto;

import java.util.List;

public interface WrqPriorityReferenceService {

    ResponseDto saveWrqPriorityReference(InternalReferenceDto internalReferenceDto, String username);

    List<InternalReferenceDto> getWrqPriorityReference();

    ResponseDto updateWrqPriority(Integer id, InternalReferenceDto internalReferenceDto, String username);
}
