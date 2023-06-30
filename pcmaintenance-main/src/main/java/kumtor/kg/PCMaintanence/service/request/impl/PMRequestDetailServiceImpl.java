package kumtor.kg.PCMaintanence.service.request.impl;

import kumtor.kg.PCMaintanence.dto.*;
import kumtor.kg.PCMaintanence.entity.PMRequest;
import kumtor.kg.PCMaintanence.entity.PMRequestStage;
import kumtor.kg.PCMaintanence.enums.RequestDetailStage;
import kumtor.kg.PCMaintanence.enums.RequestStatus;
import kumtor.kg.PCMaintanence.enums.ResponseEnum;
import kumtor.kg.PCMaintanence.exception.CustomException;
import kumtor.kg.PCMaintanence.repository.PMRequestDetailRepository;
import kumtor.kg.PCMaintanence.repository.PMRequestRepository;
import kumtor.kg.PCMaintanence.service.request.HelperService;
import kumtor.kg.PCMaintanence.service.request.PMRequestDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
@Slf4j
public class PMRequestDetailServiceImpl implements PMRequestDetailService {

    private final PMRequestRepository pmRequestRepository;
    private final PMRequestDetailRepository pmRequestDetailRepository;
    private final HelperService helperService;

    public PMRequestDetailServiceImpl(PMRequestRepository pmRequestRepository,
                                      PMRequestDetailRepository pmRequestDetailRepository,
                                      HelperService helperService) {
        this.pmRequestRepository = pmRequestRepository;
        this.pmRequestDetailRepository = pmRequestDetailRepository;
        this.helperService = helperService;
    }

    private final static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseDto saveRequestDetail(Long requestId, CreatePMRequestStageDto pmRequestDetailDto, String username) {
        log.info("Start of save request's stage service");
        log.info("Actual user : {}", username);
        log.info("Request param : requestId={}, request body : {}", requestId, pmRequestDetailDto);

        PMRequest pmRequest = pmRequestRepository.findPMRequestByRequestId(requestId);
        if (pmRequest == null) {
            log.info("Request with id : {} not found", requestId);
            throw new CustomException(ResponseEnum.REQUEST_NOT_FOUND.getCode(),
                    ResponseEnum.REQUEST_NOT_FOUND.getMessage());
        } else if (pmRequestRepository.findPMRequestByRequestIdAndStatusCode(requestId, RequestStatus.COMPLETED.getCode()) != null) {
            log.info("Request with id : {} already completed", requestId);
            throw new CustomException(ResponseEnum.REQUEST_COMPLETED.getCode(), ResponseEnum.REQUEST_COMPLETED.getMessage());
        }

        PMRequestStage pmRequestDetail = PMRequestStage.builder()
                .pmRequest(pmRequest)
                .wrqLocation(pmRequestDetailDto.getLocationCode().isBlank() ? null : helperService.getWrqLocation(pmRequestDetailDto.getLocationCode()))
                .wrqOnHold(pmRequestDetailDto.getOnHoldCode().isBlank() ? null : helperService.getWrqOnHold(pmRequestDetailDto.getOnHoldCode()))
                .foremanQty(pmRequestDetailDto.getForemanQty())
                .detailComment(pmRequestDetailDto.getComment())
                .createdBy(helperService.getEmpCode(username))
                .changedBy(helperService.getEmpCode(username))
                .stageDate(pmRequestDetailDto.getStageDate())
                .stageCode(pmRequestDetailDto.getStageCode())
                .dateCreated(LocalDateTime.now())
                .dateChanged(LocalDateTime.now())
                .isCanceled('N')
                .build();

        pmRequestDetailRepository.save(pmRequestDetail);

        if (pmRequestDetailDto.getStageCode() != null) {
            switch (pmRequestDetailDto.getStageCode()) {

                case 110:
                case 111:
                    pmRequest.setStatusCode(RequestStatus.IN_WORK.getCode());
                    pmRequestRepository.save(pmRequest);
                    break;

                case 112:
                    pmRequest.setStatusCode(RequestStatus.WAITING.getCode());
                    pmRequestRepository.save(pmRequest);
                    break;

                case 113:
                    pmRequest.setStatusCode(RequestStatus.COMPLETED.getCode());
                    pmRequest.setDateFinished(LocalDateTime.now());
                    pmRequestRepository.save(pmRequest);
                    break;
                default:
                    log.info("Incorrect stage code was inserted : {}", pmRequestDetailDto.getStageCode());
                    throw new CustomException(300, "Код этапа не найден");
            }
        }

        ResponseDto response = ResponseDto.builder()
                .code(ResponseEnum.OK.getCode())
                .message(ResponseEnum.OK.getMessage())
                .build();
        log.info("Response : {}", response);
        log.info("End of save request's stage service");
        return response;
    }


    @Override
    public ResponseDto updateRequestDetail(Long requestId, Long stageId, UpdatePMRequestDetailDto pmRequestDetailDto, String username) {
        log.info("Start of update request's stage service");
        log.info("Actual user : {}", username);
        log.info("Request params : requestId={}, requestDetailId={}, request body : {}", requestId, stageId, pmRequestDetailDto);

        PMRequest pmRequest = pmRequestRepository.findPMRequestByRequestId(requestId);
        if (pmRequest == null) {
            log.error("Request with id : {} not found", requestId);
            throw new CustomException(ResponseEnum.REQUEST_NOT_FOUND.getCode(),
                    ResponseEnum.REQUEST_NOT_FOUND.getMessage());
        } else if (pmRequest.getStatusCode().equals(RequestStatus.COMPLETED.getCode())) {
            throw new CustomException(ResponseEnum.REQUEST_COMPLETED.getCode(),
                    ResponseEnum.REQUEST_COMPLETED.getMessage());
        }
        PMRequestStage pmRequestDetail = pmRequestDetailRepository.findPMRequestDetailByPmRequest_RequestIdAndStageIdAndPmRequest_StatusCodeNot(pmRequest.getRequestId(),
                stageId, RequestStatus.CANCEL.getCode()).orElseThrow(() ->
                new CustomException(ResponseEnum.REQUEST_DETAIL_NOT_FOUND.getCode(), ResponseEnum.REQUEST_DETAIL_NOT_FOUND.getMessage()));
        if (pmRequestDetail == null) {
            log.info("Stage with id : {} in request : {} not found", stageId, requestId);
        } else if (pmRequestDetail.getIsCanceled() == 'Y') {

            throw new CustomException(ResponseEnum.STAGE_ALREADY_CANCELED.getCode(),
                    ResponseEnum.STAGE_ALREADY_CANCELED.getMessage());

        }

        pmRequestDetail.setWrqLocation(pmRequestDetailDto.getLocationCode().isBlank() ? null : helperService.getWrqLocation(pmRequestDetailDto.getLocationCode()));
        pmRequestDetail.setWrqOnHold(pmRequestDetailDto.getOnHoldCode().isBlank() ? null : helperService.getWrqOnHold(pmRequestDetailDto.getOnHoldCode()));
        pmRequestDetail.setForemanQty(pmRequestDetailDto.getForemanQty());
        pmRequestDetail.setDetailComment(pmRequestDetailDto.getComment());
        pmRequestDetail.setChangedBy(helperService.getEmpCode(username));
        pmRequestDetail.setDateChanged(LocalDateTime.now());
        pmRequestDetailRepository.save(pmRequestDetail);

        ResponseDto response = ResponseDto.builder()
                .code(ResponseEnum.OK.getCode())
                .message(ResponseEnum.OK.getMessage())
                .build();
        log.info("Response : {}", response);
        log.info("End of update request's stage service");
        return response;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, CustomException.class})
    public ResponseDto deleteRequestDetail(Long requestId, Long stageId, CancelStageDto cancelStageDto, String username) {
        log.info("Start of delete request's stage service");
        log.info("Actual user : {}", username);
        log.info("Request params : requestId={}, requestDetailId={}", requestId, stageId);

        ResponseDto response = new ResponseDto();

        PMRequest pmRequest = pmRequestRepository.findPMRequestByRequestId(requestId);

        if (pmRequest == null) {
            log.error("Request with id : {} not found", requestId);
            throw new CustomException(ResponseEnum.REQUEST_NOT_FOUND.getCode(),
                    ResponseEnum.REQUEST_NOT_FOUND.getMessage());
        } else if (pmRequest.getStatusCode().equals(RequestStatus.COMPLETED.getCode())) {
            throw new CustomException(ResponseEnum.REQUEST_COMPLETED.getCode(),
                    ResponseEnum.REQUEST_COMPLETED.getMessage());
        }

        PMRequestStage pmRequestDetail = pmRequestDetailRepository.findPMRequestDetailByPmRequest_RequestIdAndStageIdAndPmRequest_StatusCodeNot(pmRequest.getRequestId(),
                stageId, RequestStatus.CANCEL.getCode()).orElseThrow(() ->

                new CustomException(ResponseEnum.REQUEST_DETAIL_NOT_FOUND.getCode(), ResponseEnum.REQUEST_DETAIL_NOT_FOUND.getMessage()));


        if (pmRequestDetail == null) {

            log.info("Stage with id : {} in request : {} not found", stageId, requestId);

        } else if (pmRequestDetail.getIsCanceled() == 'Y') {

            log.info("Stage with id : {} in request : {} is already canceled", stageId, requestId);
            throw new CustomException(ResponseEnum.STAGE_ALREADY_CANCELED.getCode(),
                    ResponseEnum.STAGE_ALREADY_CANCELED.getMessage());

        } else if (pmRequestDetail.getStageCode().equals(RequestDetailStage.STOP.getCode())
                && pmRequestDetailRepository.findPMRequestDetailsByPmRequest_RequestId(pmRequest.getRequestId()).size() == 1) {

            pmRequestDetailRepository.deleteById(stageId);
            pmRequestRepository.deleteById(requestId);

            response = ResponseDto.builder()
                    .code(ResponseEnum.STAGE_DELETED.getCode())
                    .message(ResponseEnum.STAGE_DELETED.getMessage())
                    .build();

        } else if (pmRequestDetailRepository.findPMRequestDetailsByPmRequest_RequestId(pmRequest.getRequestId())
                .stream()
                .filter(x -> !x.getStageId().equals(stageId)).allMatch(x -> x.getIsCanceled() == 'Y')) {

            pmRequestDetail.setIsCanceled('Y');
            pmRequestDetail.setCancelReason(cancelStageDto.getCancelReason());
            pmRequest.setStatusCode(RequestStatus.CANCEL.getCode());
            pmRequestDetailRepository.save(pmRequestDetail);
            pmRequestRepository.save(pmRequest);

            response = ResponseDto.builder()
                    .code(ResponseEnum.REQUEST_CANCELED.getCode())
                    .message(ResponseEnum.REQUEST_CANCELED.getMessage())
                    .build();

        } else {

            if (pmRequestDetailRepository.findPMRequestDetailsByPmRequest_RequestIdAndIsCanceled(requestId, 'N').size() == 1) {

                pmRequest.setStatusCode(getRequestStatusCode(pmRequestDetail.getStageCode()));

            } else {

                PMRequestStage requestDetail = pmRequestDetailRepository.findTop1ByPmRequest_RequestIdAndIsCanceledAndStageIdNotOrderByStageIdDesc(
                        requestId, 'N', stageId);
                pmRequest.setStatusCode(getRequestStatusCode(requestDetail.getStageCode()));
            }

            pmRequestDetail.setIsCanceled('Y');
            pmRequestDetail.setCancelReason(cancelStageDto.getCancelReason());
            pmRequestDetailRepository.save(pmRequestDetail);
            pmRequestRepository.save(pmRequest);
            response = ResponseDto.builder()
                    .code(ResponseEnum.STAGE_CANCELED.getCode())
                    .message(ResponseEnum.STAGE_CANCELED.getMessage())
                    .build();
        }

        log.info("Response : {}", response);
        log.info("End of delete request's stage service");
        return response;
    }

    @Override
    public List<PMRequestStageDto> getRequestDetails(Long requestId, String username) {
        log.info("Start of get request's stages service");
        log.info("Request param : requestId={}, page={}, size={}", requestId);
        log.info("Actual user : {}", username);


        List<PMRequestStage> pmRequestDetailList = pmRequestDetailRepository.findPMRequestDetailsByPmRequest_RequestIdOrderByDateChangedAsc
                (requestId);

        List<PMRequestStageDto> pmRequestDetailDtoList = new ArrayList<>();


        pmRequestDetailList.forEach(
                pmRequestDetail -> {

                    final String history = "Обновлено " + helperService.getEmpNameByEmpCode(pmRequestDetail.getChangedBy()).trim() + " в " + pmRequestDetail.getDateChanged().format(DateTimeFormatter.ofPattern(DATE_PATTERN));

                    final String cancelTitle = "Этап отменен.";

                    PMRequestStageDto pmRequestDetailDto = PMRequestStageDto.builder()
                            .stageId(pmRequestDetail.getStageId())
                            .stageCode(RequestDetailStage.getStage(pmRequestDetail.getStageCode()).getCode())
                            .stageMessage(RequestDetailStage.getStage(pmRequestDetail.getStageCode()).getMessage())
                            .locationCode(pmRequestDetail.getWrqLocation() == null ? null : pmRequestDetail.getWrqLocation().getLocationCode().trim())
                            .locationName(pmRequestDetail.getWrqLocation() == null ? null : pmRequestDetail.getWrqLocation().getLocationName().trim())
                            .onHoldCode(pmRequestDetail.getWrqOnHold() == null ? null : pmRequestDetail.getWrqOnHold().getWrqOnHoldCode().trim())
                            .onHoldName(pmRequestDetail.getWrqOnHold() == null ? null : pmRequestDetail.getWrqOnHold().getWrqOnHoldName().trim())
                            .stageDate(pmRequestDetail.getStageDate())
                            .foremanQty(pmRequestDetail.getForemanQty())
                            .comment(pmRequestDetail.getDetailComment() == null ? null : pmRequestDetail.getDetailComment().trim())
                            .history(history)
                            .isCanceled(pmRequestDetail.getIsCanceled())
                            .cancelReason(pmRequestDetail.getIsCanceled() == 'Y' ? cancelTitle + " " + pmRequestDetail.getCancelReason().trim() : null)
                            .build();
                    pmRequestDetailDtoList.add(pmRequestDetailDto);
                }
        );


        log.info("Response : {}", pmRequestDetailDtoList);
        log.info("End of get request's stages service");
        return pmRequestDetailDtoList;
    }

    public Integer getRequestStatusCode(Integer stageCode) {
        Integer requestStatusCode = null;
        switch (stageCode) {
            case 110:
            case 111:
                requestStatusCode = RequestStatus.IN_WORK.getCode();
                break;

            case 112:
                requestStatusCode = RequestStatus.WAITING.getCode();
                break;
        }
        return requestStatusCode;
    }
}
