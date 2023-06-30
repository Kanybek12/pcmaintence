package kumtor.kg.PCMaintanence.service.reference;

import kumtor.kg.PCMaintanence.dto.*;

import java.util.List;

public interface StatusReferenceService {

    List<StatusDto> getRequestDetailStageReference();
    List<StatusDto> getRequestStatusReference();

}
