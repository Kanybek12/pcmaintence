package kumtor.kg.PCMaintanence.service.reference.impl;

import kumtor.kg.PCMaintanence.dto.*;

import kumtor.kg.PCMaintanence.enums.RequestDetailStage;
import kumtor.kg.PCMaintanence.enums.RequestStatus;
import kumtor.kg.PCMaintanence.service.reference.StatusReferenceService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class StatusReferenceServiceImpl implements StatusReferenceService {


    @Override
    public List<StatusDto> getRequestDetailStageReference() {
        List<StatusDto> requestDetailStageList = new ArrayList<>();
        Arrays.stream(kumtor.kg.PCMaintanence.enums.RequestDetailStage.values()).forEach(requestDetailStage -> {

            StatusDto statusReference = StatusDto.builder()
                    .code(requestDetailStage.getCode())
                    .name(requestDetailStage.getMessage())
                    .build();
            requestDetailStageList.add(statusReference);

        });
        return requestDetailStageList;
    }

    @Override
    public List<StatusDto> getRequestStatusReference() {
        List<StatusDto> requestStatusList = new ArrayList<>();
        Arrays.stream(RequestStatus.values()).forEach(requestStatus -> {
            if (!requestStatus.getCode().equals(RequestStatus.CANCEL.getCode())) {
                StatusDto statusDto = StatusDto.builder()
                        .code(requestStatus.getCode())
                        .name(requestStatus.getMessage())
                        .build();
                requestStatusList.add(statusDto);
            }
        });
        return requestStatusList;
    }
}
