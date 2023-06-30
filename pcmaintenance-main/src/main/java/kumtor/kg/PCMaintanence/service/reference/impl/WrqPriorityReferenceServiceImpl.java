package kumtor.kg.PCMaintanence.service.reference.impl;

import kumtor.kg.PCMaintanence.dto.reference.InternalReferenceDto;
import kumtor.kg.PCMaintanence.dto.ResponseDto;
import kumtor.kg.PCMaintanence.entity.reference.WrqPriority;
import kumtor.kg.PCMaintanence.enums.ResponseEnum;
import kumtor.kg.PCMaintanence.exception.CustomException;
import kumtor.kg.PCMaintanence.repository.WrqPriorityRepository;
import kumtor.kg.PCMaintanence.service.reference.WrqPriorityReferenceService;
import kumtor.kg.PCMaintanence.service.request.HelperService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WrqPriorityReferenceServiceImpl implements WrqPriorityReferenceService {

    private final WrqPriorityRepository wrqPriorityRepository;
    private final HelperService helperService;


    public WrqPriorityReferenceServiceImpl(WrqPriorityRepository wrqPriorityRepository, HelperService helperService) {
        this.wrqPriorityRepository = wrqPriorityRepository;
        this.helperService = helperService;
    }

    @Override
    public ResponseDto saveWrqPriorityReference(InternalReferenceDto referenceDto, String username) {
        Optional<WrqPriority> wrqPriority = wrqPriorityRepository.findWrqPriorityByPriorityCodeEqualsAndIsActive(referenceDto.getCode(), 'Y');
        if (wrqPriority.isPresent()) {
            throw new CustomException(300, "Приоритет с таким кодом уже существует");
        }

        wrqPriorityRepository.save(WrqPriority.builder()
                .priorityCode(referenceDto.getCode())
                .priorityName(referenceDto.getName())
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
    public List<InternalReferenceDto> getWrqPriorityReference() {

        List<WrqPriority> wrqPriorityList = wrqPriorityRepository.findAll();

        List<InternalReferenceDto> wrqPriorityReference = new ArrayList<>();

        wrqPriorityList.forEach(wrqPriority -> {
            InternalReferenceDto referenceDto = InternalReferenceDto.builder()
                    .id(wrqPriority.getId())
                    .code(wrqPriority.getPriorityCode())
                    .name(wrqPriority.getPriorityName())
                    .isActive(wrqPriority.getIsActive())
                    .build();
            wrqPriorityReference.add(referenceDto);
        });

        return wrqPriorityReference;
    }

    @Override
    public ResponseDto updateWrqPriority(Integer id, InternalReferenceDto referenceDto, String username) {
        WrqPriority wrqPriority = wrqPriorityRepository.findWrqPriorityById(id);

        if (wrqPriority == null) {
            throw new CustomException(301, "Приоритет не найден в справочнике");
        } else if (wrqPriorityRepository.findWrqPriorityByPriorityCodeEqualsAndIsActive(referenceDto.getCode(), 'A').isPresent() &&
                !wrqPriority.getPriorityCode().equals(referenceDto.getCode())) {
            throw new CustomException(300, "Приоритет с таким кодом уже существует");
        }

        wrqPriority.setPriorityCode(referenceDto.getCode());
        wrqPriority.setPriorityName(referenceDto.getName());
        wrqPriority.setIsActive(referenceDto.getIsActive());
        wrqPriority.setChangedBy(helperService.getEmpCode(username));
        wrqPriority.setChangedDate(LocalDateTime.now());

        wrqPriorityRepository.save(wrqPriority);

        return ResponseDto.builder()
                .code(ResponseEnum.OK.getCode())
                .message(ResponseEnum.OK.getMessage())
                .build();
    }

}
