package kumtor.kg.PCMaintanence.service.reference.impl;

import kumtor.kg.PCMaintanence.dto.reference.InternalReferenceDto;
import kumtor.kg.PCMaintanence.dto.ResponseDto;
import kumtor.kg.PCMaintanence.entity.reference.WrqOnHold;
import kumtor.kg.PCMaintanence.enums.ResponseEnum;
import kumtor.kg.PCMaintanence.exception.CustomException;
import kumtor.kg.PCMaintanence.repository.WrqOnHoldRepository;
import kumtor.kg.PCMaintanence.service.reference.WrqOnHoldReferenceService;
import kumtor.kg.PCMaintanence.service.request.HelperService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WrqOnHoldReferenceServiceImpl implements WrqOnHoldReferenceService {

    private final WrqOnHoldRepository wrqOnHoldRepository;
    private final HelperService helperService;


    public WrqOnHoldReferenceServiceImpl(WrqOnHoldRepository wrqOnHoldRepository, HelperService helperService) {
        this.wrqOnHoldRepository = wrqOnHoldRepository;
        this.helperService = helperService;
    }

    @Override
    public ResponseDto saveWrqOnHoldReference(InternalReferenceDto referenceDto, String username) {

        Optional<WrqOnHold> wrqOnHold = wrqOnHoldRepository.findWrqOnHoldByWrqOnHoldCodeAndIsActive(referenceDto.getCode(), 'Y');
        if (wrqOnHold.isPresent()) {
            throw new CustomException(300, "Ожидание с таким кодом уже существует");
        }

        wrqOnHoldRepository.save(WrqOnHold.builder()
                .wrqOnHoldCode(referenceDto.getCode())
                .wrqOnHoldName(referenceDto.getName())
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
    public List<InternalReferenceDto> getWrqOnHoldReference() {

        List<WrqOnHold> wrqOnHoldList = wrqOnHoldRepository.findAll();

        List<InternalReferenceDto> wrqOnHoldReference = new ArrayList<>();

        wrqOnHoldList.forEach(wrqOnHold -> {
            InternalReferenceDto referenceDto = InternalReferenceDto.builder()
                    .id(wrqOnHold.getId())
                    .code(wrqOnHold.getWrqOnHoldCode())
                    .name(wrqOnHold.getWrqOnHoldName())
                    .isActive(wrqOnHold.getIsActive())
                    .build();
            wrqOnHoldReference.add(referenceDto);
        });

        return wrqOnHoldReference;
    }

    @Override
    public ResponseDto updateWrqOnHold(Integer id, InternalReferenceDto referenceDto, String username) {

        WrqOnHold wrqOnHold = wrqOnHoldRepository.findWrqOnHoldById(id);

        if (wrqOnHold == null) {
            throw new CustomException(301, "Ожидание не найдено в справочнике");
        } else if (wrqOnHoldRepository.findWrqOnHoldByWrqOnHoldCodeAndIsActive(referenceDto.getCode(), 'Y').isPresent() &&
                !wrqOnHold.getWrqOnHoldCode().equals(referenceDto.getCode())) {
            throw new CustomException(300, "Ожидание с таким кодом уже есть в справочнике");
        }

        wrqOnHold.setWrqOnHoldCode(referenceDto.getCode());
        wrqOnHold.setWrqOnHoldName(referenceDto.getName());
        wrqOnHold.setIsActive(referenceDto.getIsActive());
        wrqOnHold.setChangedBy(helperService.getEmpCode(username));
        wrqOnHold.setChangedDate(LocalDateTime.now());
        wrqOnHoldRepository.save(wrqOnHold);

        return ResponseDto.builder()
                .code(ResponseEnum.OK.getCode())
                .message(ResponseEnum.OK.getMessage())
                .build();

    }
}
