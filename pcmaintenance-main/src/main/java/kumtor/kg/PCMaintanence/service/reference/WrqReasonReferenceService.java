package kumtor.kg.PCMaintanence.service.reference;

import kumtor.kg.PCMaintanence.dto.reference.InternalReferenceDto;
import kumtor.kg.PCMaintanence.dto.ResponseDto;

import java.util.List;

public interface WrqReasonReferenceService {

    ResponseDto saveWrqReasonReference(InternalReferenceDto internalReferenceDto, String username);

    List<InternalReferenceDto> getWrqReasonReference();

    ResponseDto updateWrqReason(Integer id, InternalReferenceDto internalReferenceDto, String username);
}
