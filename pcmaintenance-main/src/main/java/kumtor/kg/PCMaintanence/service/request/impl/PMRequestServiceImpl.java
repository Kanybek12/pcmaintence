package kumtor.kg.PCMaintanence.service.request.impl;

import kumtor.kg.PCMaintanence.dto.CreatePMRequestDto;
import kumtor.kg.PCMaintanence.dto.PMRequestDto;
import kumtor.kg.PCMaintanence.dto.ResponseDto;
import kumtor.kg.PCMaintanence.entity.PMRequest;
import kumtor.kg.PCMaintanence.entity.PMRequestStage;
import kumtor.kg.PCMaintanence.entity.reference.WrqPriority;
import kumtor.kg.PCMaintanence.entity.reference.WrqReason;
import kumtor.kg.PCMaintanence.enums.RequestDetailStage;
import kumtor.kg.PCMaintanence.enums.RequestStatus;
import kumtor.kg.PCMaintanence.enums.ResponseEnum;
import kumtor.kg.PCMaintanence.exception.CustomException;
import kumtor.kg.PCMaintanence.repository.PMRequestDetailRepository;
import kumtor.kg.PCMaintanence.repository.PMRequestRepository;
import kumtor.kg.PCMaintanence.repository.WrqPriorityRepository;
import kumtor.kg.PCMaintanence.repository.WrqReasonRepository;
import kumtor.kg.PCMaintanence.service.reference.ManasReferenceService;
import kumtor.kg.PCMaintanence.service.request.HelperService;
import kumtor.kg.PCMaintanence.service.request.PMRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class PMRequestServiceImpl implements PMRequestService {


    private final PMRequestRepository pmRequestRepository;
    private final HelperService helperService;
    private final PMRequestDetailRepository pmRequestDetailRepository;
    private final ManasReferenceService manasReferenceService;
    private final WrqReasonRepository wrqReasonRepository;
    private final WrqPriorityRepository wrqPriorityRepository;


    public PMRequestServiceImpl(PMRequestRepository pmRequestRepository,
                                HelperService helperService,
                                PMRequestDetailRepository pmRequestDetailRepository,
                                ManasReferenceService manasReferenceService,
                                WrqReasonRepository wrqReasonRepository,
                                WrqPriorityRepository wrqPriorityRepository) {
        this.pmRequestRepository = pmRequestRepository;
        this.helperService = helperService;
        this.pmRequestDetailRepository = pmRequestDetailRepository;
        this.manasReferenceService = manasReferenceService;
        this.wrqReasonRepository = wrqReasonRepository;
        this.wrqPriorityRepository = wrqPriorityRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseDto savePMRequest(CreatePMRequestDto pmRequisitionDto, String username) {

        log.info("Start of save request service");
        log.info("Actual user : {}", username);
        log.info("Request body : {}", pmRequisitionDto);

        PMRequest pmRequest = PMRequest.builder()
                .assetCode(helperService.getAssetCode(pmRequisitionDto.getAssetCode()))
                .assetTypeCode(manasReferenceService.getAssetType(pmRequisitionDto.getAssetCode()).getCode())
                .stopDate(pmRequisitionDto.getStopDate())
                .wrqNo(pmRequisitionDto.getWrqNo() == null ? null : Integer.valueOf(helperService.getWrqNo(pmRequisitionDto.getWrqNo(), helperService.getAssetCode(pmRequisitionDto.getAssetCode()))))
                .wrqTitle(pmRequisitionDto.getWrqTitle())
                .priorityId(helperService.getPriority(pmRequisitionDto.getWrqPriorityCode()))
                .wrqReqComments(pmRequisitionDto.getReqComments())
                .wrqReasonId(helperService.getReason(pmRequisitionDto.getWrqReasonCode()))
                .dateExpected(pmRequisitionDto.getDateExpected())
                .statusCode(RequestStatus.IN_WORK.getCode())
                .createdBy(helperService.getEmpCode(username))
                .changedBy(helperService.getEmpCode(username))
                .build();

        pmRequestRepository.save(pmRequest);


        PMRequestStage pcMaintenanceRequestDetail = PMRequestStage.builder()
                .pmRequest(pmRequest)
                .stageCode(RequestDetailStage.STOP.getCode())
                .stageDate(LocalDateTime.now())
                .detailComment(pmRequest.getWrqReqComments())
                .createdBy(helperService.getEmpCode(username))
                .changedBy(helperService.getEmpCode(username))
                .dateCreated(LocalDateTime.now())
                .dateChanged(LocalDateTime.now())
                .isCanceled('N')
                .build();
        pmRequestDetailRepository.save(pcMaintenanceRequestDetail);

        ResponseDto response = ResponseDto.builder()
                .code(ResponseEnum.OK.getCode())
                .message(ResponseEnum.OK.getMessage())
                .build();
        log.info("Response : {}", response);
        log.info("End of save request service");
        return response;

    }


    @Override
    public ResponseDto updatePMRequest(Long requestCode, CreatePMRequestDto pmRequestDto, String username) {
        log.info("Start of update request service");
        log.info("Actual user : {}", username);
        log.info("Request body : {}", pmRequestDto);

        PMRequest pmRequisition = pmRequestRepository.findPMRequestByRequestId(requestCode);
        if (pmRequisition == null) {
            throw new CustomException(ResponseEnum.REQUEST_NOT_FOUND.getCode(),
                    ResponseEnum.REQUEST_NOT_FOUND.getMessage());
        } else if (pmRequisition.getStatusCode().equals(RequestStatus.COMPLETED.getCode())) {
            throw new CustomException(ResponseEnum.REQUEST_COMPLETED.getCode(),
                    ResponseEnum.REQUEST_COMPLETED.getMessage());
        }else if(pmRequisition.getStatusCode().equals(RequestStatus.CANCEL.getCode())){
            throw new CustomException(ResponseEnum.REQUEST_ALREADY_CANCELED.getCode(),
                    ResponseEnum.REQUEST_ALREADY_CANCELED.getMessage());
        }

        if (!pmRequestDto.getAssetCode().equals(pmRequisition.getAssetCode())) {
            pmRequisition.setAssetTypeCode(manasReferenceService.getAssetType(pmRequestDto.getAssetCode()).getCode());
        }
        pmRequisition.setAssetCode(helperService.getAssetCode(pmRequestDto.getAssetCode()));
        pmRequisition.setStopDate(pmRequisition.getStopDate());
        pmRequisition.setWrqNo(pmRequestDto.getWrqNo() == null ? null : Integer.valueOf(helperService.getWrqNo(pmRequestDto.getWrqNo(), helperService.getAssetCode(pmRequestDto.getAssetCode()))));
        pmRequisition.setWrqTitle(pmRequestDto.getWrqTitle());
        pmRequisition.setPriorityId(helperService.getPriority(pmRequestDto.getWrqPriorityCode()));
        pmRequisition.setWrqReqComments(pmRequestDto.getReqComments());
        pmRequisition.setWrqReasonId(helperService.getReason(pmRequestDto.getWrqReasonCode()));
        pmRequisition.setDateExpected(pmRequestDto.getDateExpected());
        pmRequisition.setChangedBy(helperService.getEmpCode(username));

        pmRequestRepository.save(pmRequisition);

        ResponseDto response = ResponseDto.builder()
                .code(ResponseEnum.OK.getCode())
                .message(ResponseEnum.OK.getMessage())
                .build();
        log.info("Response : {}", response);
        log.info("End of update request service");
        return response;
    }


    @Override
    public ResponseDto deletePMRequest(Long requestCode, String username) {
        log.info("Start of delete request service");
        log.info("Request param : requestCode={}", requestCode);
        log.info("Actual user : {}", username);

        ResponseDto response;

        PMRequest pmRequest = pmRequestRepository.findPMRequestByRequestId(requestCode);
        if (pmRequest == null) {
            throw new CustomException(ResponseEnum.REQUEST_NOT_FOUND.getCode(), ResponseEnum.REQUEST_NOT_FOUND.getMessage());
        } else if (pmRequest.getStatusCode().equals(RequestStatus.COMPLETED.getCode())) {
            throw new CustomException(ResponseEnum.REQUEST_COMPLETED.getCode(),
                    ResponseEnum.REQUEST_COMPLETED.getMessage());
        }

        List<PMRequestStage> pmRequestDetailList = pmRequestDetailRepository.findPMRequestDetailsByPmRequest_RequestId(pmRequest.getRequestId());

        if (pmRequest.getStatusCode().equals(RequestStatus.IN_WORK.getCode()) && pmRequestDetailList.isEmpty() ||
                pmRequest.getStatusCode().equals(RequestStatus.IN_WORK.getCode()) &&
                        pmRequestDetailList.size() == 1 && pmRequestDetailList.get(0).getStageCode().equals(RequestDetailStage.STOP.getCode())) {
            pmRequestRepository.deleteById(pmRequest.getRequestId());
            response = ResponseDto.builder()
                    .code(ResponseEnum.REQUEST_DELETED.getCode())
                    .message(ResponseEnum.REQUEST_DELETED.getMessage())
                    .build();
        } else {
            pmRequest.setStatusCode(RequestStatus.CANCEL.getCode());
            pmRequestRepository.save(pmRequest);
            response = ResponseDto.builder()
                    .code(ResponseEnum.REQUEST_CANCELED.getCode())
                    .message(ResponseEnum.REQUEST_CANCELED.getMessage())
                    .build();
        }
        log.info("Response : {}", response);
        log.info("End of delete request service");
        return response;

    }

    @Override
    public Map<String, Object> getAllPmRequests(String username, int page, int size, List<String> assetTypeCodeList, List<Integer> statusCodeList, String assetCode, Integer wrqReasonId, String wrqNo,
                                                String wrqTitle, Integer wrqPriorityId) {

        log.info("Start of get all requests service");
        log.info("Actual user : {}", username);
        log.info("Request param : page={}, size={}, assetTypeCode={}, statusCode={}, assetCode={}, wrqReasonId={}, " +
                "wrqNo={}, wrqTitle={}, wrqPriorityId={} ", page, size, assetTypeCodeList, statusCodeList, assetCode, wrqReasonId, wrqNo, wrqTitle, wrqPriorityId);

        Pageable pageable = PageRequest.of(page - 1, size);
        WrqReason wrqReason = wrqReasonRepository.findWrqReasonById(wrqReasonId);
        WrqPriority wrqPriority = wrqPriorityRepository.findWrqPriorityById(wrqPriorityId);


        Page<PMRequest> pmRequestPage = pmRequestRepository.findAll((Specification<PMRequest>) (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.notEqual(root.get("statusCode"), RequestStatus.CANCEL.getCode()));
            if (!ObjectUtils.isEmpty(assetTypeCodeList)) {
                Expression<String> expression = root.get("assetTypeCode");
                predicate = expression.in(assetTypeCodeList);
            }
            if (!ObjectUtils.isEmpty(statusCodeList)) {
                Expression<Integer> expression = root.get("statusCode");
                predicate = expression.in(statusCodeList);
            }
            if (!ObjectUtils.isEmpty(assetCode)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("assetCode"), assetCode));
            }
            if (!ObjectUtils.isEmpty(wrqPriorityId)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("priorityId"), wrqPriority));
            }
            if (!ObjectUtils.isEmpty(wrqReasonId)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("wrqReasonId"), wrqReason));
            }
            if (!ObjectUtils.isEmpty(wrqNo)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("wrqNo"), wrqNo));
            }
            if (!ObjectUtils.isEmpty(wrqTitle)) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("wrqTitle"), wrqTitle));
            }
            query.orderBy(criteriaBuilder.desc(root.get("requestId")));
            return predicate;

        }, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", mapPMRequestToPmRequestDto(pmRequestPage.getContent()));
        response.put("totalItems", pmRequestPage.getTotalElements());
        response.put("totalPages", pmRequestPage.getTotalPages());
        response.put("currentPage", pmRequestPage.getNumber() + 1);
        response.put("itemsOnPage", pmRequestPage.getNumberOfElements());
        log.info("Response : {}", response);
        log.info("End of get all requests service");
        return response;
    }


    public List<PMRequestDto> mapPMRequestToPmRequestDto(List<PMRequest> pmRequestList) {
        log.info("Start of map pmRequest to pmRequestDto service");
        log.info("Request body : {} ", pmRequestList);
        List<PMRequestDto> pmRequestDtoList = new ArrayList<>();
        pmRequestList.forEach(pmRequest -> {

            PMRequestDto pmRequisitionDto = PMRequestDto.builder()
                    .requestId(pmRequest.getRequestId())
                    .assetCode(pmRequest.getAssetCode().trim())
                    .assetTypeCode(helperService.getAssetTypeName(pmRequest.getAssetTypeCode()).trim())
                    .stopDate(pmRequest.getStopDate())
                    .wrqReasonCode(pmRequest.getWrqReasonId().getWrqReasonCode().trim())
                    .wrqReasonName(pmRequest.getWrqReasonId().getWrqReasonName().trim())
                    .wrqNo(pmRequest.getWrqNo() == null ? null : pmRequest.getWrqNo())
                    .wrqTitle(pmRequest.getWrqTitle() == null ? null : pmRequest.getWrqTitle().trim())
                    .reqComments(pmRequest.getWrqReqComments() == null ? null : pmRequest.getWrqReqComments().trim())
                    .wrqPriorityCode(pmRequest.getPriorityId().getPriorityCode().trim())
                    .wrqPriorityName(pmRequest.getPriorityId().getPriorityName().trim())
                    .dateExpected(pmRequest.getDateExpected())
                    .dateFinished(pmRequest.getDateFinished() == null ? null : pmRequest.getDateFinished())
                    .statusCode(RequestStatus.getStatus(pmRequest.getStatusCode()).getCode())
                    .statusMessage(RequestStatus.getStatus(pmRequest.getStatusCode()).getMessage())
                    .build();
            pmRequestDtoList.add(pmRequisitionDto);
        });
        log.info("Response : {}", pmRequestDtoList);
        log.info("End of map pmRequest to pmRequestDto service");
        return pmRequestDtoList;
    }

    @Override
    public PMRequestDto getPmRequestById(String username, Long requestId) {
        log.info("Start of get request by requestId service");
        log.info("Actual user : {}", username);
        log.info("Request param : requestId - {}", requestId);
        PMRequest pmRequest = pmRequestRepository.findPMRequestByRequestId(requestId);
        if (pmRequest == null) {
            throw new CustomException(ResponseEnum.REQUEST_NOT_FOUND.getCode(), ResponseEnum.REQUEST_NOT_FOUND.getMessage());
        }
        PMRequestDto pmRequestDto =  PMRequestDto.builder()
                .requestId(pmRequest.getRequestId())
                .assetCode(pmRequest.getAssetCode().trim())
                .assetTypeCode(pmRequest.getAssetTypeCode().trim())
                .stopDate(pmRequest.getStopDate())
                .wrqReasonCode(pmRequest.getWrqReasonId().getWrqReasonCode().trim())
                .wrqReasonName(pmRequest.getPriorityId().getPriorityName().trim())
                .wrqNo(pmRequest.getWrqNo())
                .wrqTitle(pmRequest.getWrqTitle() == null ? null : pmRequest.getWrqTitle().trim())
                .reqComments(pmRequest.getWrqReqComments() == null ? null : pmRequest.getWrqReqComments().trim())
                .wrqPriorityCode(pmRequest.getPriorityId().getPriorityCode())
                .wrqPriorityName(pmRequest.getPriorityId().getPriorityName())
                .dateExpected(pmRequest.getDateExpected())
                .statusCode(RequestStatus.getStatus(pmRequest.getStatusCode()).getCode())
                .statusMessage(RequestStatus.getStatus(pmRequest.getStatusCode()).getMessage())
                .build();
        log.info("Response : {}", pmRequestDto);
        log.info("End of get request by requestId service");
        return pmRequestDto;
    }
}
