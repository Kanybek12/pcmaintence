package kumtor.kg.PCMaintanence.service.request;

import kumtor.kg.PCMaintanence.dto.CreatePMRequestDto;
import kumtor.kg.PCMaintanence.dto.PMRequestDto;
import kumtor.kg.PCMaintanence.dto.ResponseDto;


import java.util.List;
import java.util.Map;

public interface PMRequestService {
    ResponseDto savePMRequest(CreatePMRequestDto pmRequisitionDto, String username) throws Exception;
    ResponseDto updatePMRequest(Long requestCode, CreatePMRequestDto pmRequisitionDto, String username) throws Exception;
    ResponseDto deletePMRequest(Long requestCode, String username) throws Exception;
    Map<String, Object> getAllPmRequests(String username, int page, int size, List<String> assetTypeCodeList, List<Integer> statusCodeList, String assetCode, Integer wrqReasonId, String wrqNo, String wrqTitle, Integer wrqPriorityId) throws Exception;
    PMRequestDto getPmRequestById(String username, Long requestId);
}
