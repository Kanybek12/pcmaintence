package kumtor.kg.PCMaintanence.service.request;

import kumtor.kg.PCMaintanence.dto.*;

import java.util.List;


public interface PMRequestDetailService {
    ResponseDto saveRequestDetail(Long requestId, CreatePMRequestStageDto pmRequestDetailDto, String username) throws Exception;
    ResponseDto updateRequestDetail(Long requestId, Long stageId, UpdatePMRequestDetailDto pmRequestDetailDto, String username) throws Exception;
    ResponseDto deleteRequestDetail(Long requestId, Long stageId, CancelStageDto cancelStage, String username) throws Exception;
    List<PMRequestStageDto> getRequestDetails(Long requestId, String username);
}
