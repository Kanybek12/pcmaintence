package kumtor.kg.PCMaintanence.service.reference.impl;

import kumtor.kg.PCMaintanence.dto.reference.InternalReferenceDto;
import kumtor.kg.PCMaintanence.dto.ResponseDto;
import kumtor.kg.PCMaintanence.entity.reference.WrqReason;
import kumtor.kg.PCMaintanence.enums.ResponseEnum;
import kumtor.kg.PCMaintanence.exception.CustomException;
import kumtor.kg.PCMaintanence.repository.WrqReasonRepository;
import kumtor.kg.PCMaintanence.service.reference.WrqReasonReferenceService;
import kumtor.kg.PCMaintanence.service.request.HelperService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WrqReasonReferenceServiceImpl implements WrqReasonReferenceService {

    private final WrqReasonRepository wrqReasonRepository;
    private final HelperService helperService;

    public WrqReasonReferenceServiceImpl(WrqReasonRepository wrqReasonRepository, HelperService helperService) {
        this.wrqReasonRepository = wrqReasonRepository;
        this.helperService = helperService;
    }

    @Override
    public ResponseDto saveWrqReasonReference(InternalReferenceDto referenceDto, String username) {
        Optional<WrqReason> wrqReason = wrqReasonRepository.findWrqReasonByWrqReasonCodeAndIsActive(referenceDto.getCode(), 'Y');
        if (wrqReason.isPresent()) {
            throw new CustomException(300, "Причина с таким кодом уже существует");
        }

        wrqReasonRepository.save(WrqReason.builder()
                .wrqReasonCode(referenceDto.getCode())
                .wrqReasonName(referenceDto.getName())
                .isActive('Y')
                .changedBy(helperService.getEmpCode(username))
                .changedDate(LocalDateTime.now())
                .build());

        return ResponseDto.builder()
                .code(ResponseEnum.OK.getCode())
                .message(ResponseEnum.OK.getMessage())
                .build();
    }

    @Override
    public List<InternalReferenceDto> getWrqReasonReference() {

        List<WrqReason> wrqReasonList = wrqReasonRepository.findAll();

        List<InternalReferenceDto> wrqReasonReference = new ArrayList<>();

        wrqReasonList.forEach(wrqReason -> {
            InternalReferenceDto referenceDto = InternalReferenceDto.builder()
                    .id(wrqReason.getId())
                    .code(wrqReason.getWrqReasonCode())
                    .name(wrqReason.getWrqReasonName())
                    .isActive(wrqReason.getIsActive())
                    .build();
            wrqReasonReference.add(referenceDto);
        });

        return wrqReasonReference;
    }

    @Override
    public ResponseDto updateWrqReason(Integer id, InternalReferenceDto referenceDto, String username) {
        WrqReason wrqReason = wrqReasonRepository.findWrqReasonById(id);

        if (wrqReason == null) {
            throw new CustomException(301, "Причина не найдена в справочнике");
        } else if (wrqReasonRepository.findWrqReasonByWrqReasonCodeAndIsActive(referenceDto.getCode(), 'Y').isPresent() &&
                !wrqReason.getWrqReasonCode().equals(referenceDto.getCode())) {
            throw new CustomException(300, "Причина с таким кодом уже существует");
        }
        wrqReason.setWrqReasonCode(referenceDto.getCode());
        wrqReason.setWrqReasonName(referenceDto.getName());
        wrqReason.setIsActive(referenceDto.getIsActive());
        wrqReason.setChangedBy(helperService.getEmpCode(username));
        wrqReason.setChangedDate(LocalDateTime.now());

        wrqReasonRepository.save(wrqReason);

        return ResponseDto.builder()
                .code(ResponseEnum.OK.getCode())
                .message(ResponseEnum.OK.getMessage())
                .build();
    }
}
