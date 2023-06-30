package kumtor.kg.PCMaintanence.service.reference.impl;

import kumtor.kg.PCMaintanence.dto.reference.InternalReferenceDto;
import kumtor.kg.PCMaintanence.dto.ResponseDto;
import kumtor.kg.PCMaintanence.entity.reference.WrqLocation;
import kumtor.kg.PCMaintanence.enums.ResponseEnum;
import kumtor.kg.PCMaintanence.exception.CustomException;
import kumtor.kg.PCMaintanence.repository.WrqLocationRepository;
import kumtor.kg.PCMaintanence.service.reference.WrqLocationReferenceService;
import kumtor.kg.PCMaintanence.service.request.HelperService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WrqLocationReferenceServiceImpl implements WrqLocationReferenceService {

    private final WrqLocationRepository wrqLocationRepository;
    private final HelperService helperService;

    public WrqLocationReferenceServiceImpl(WrqLocationRepository wrqLocationRepository, HelperService helperService) {
        this.wrqLocationRepository = wrqLocationRepository;
        this.helperService = helperService;
    }

    @Override
    public ResponseDto saveWrqLocation(InternalReferenceDto referenceDto, String username) {
        Optional<WrqLocation> wrqLocation = wrqLocationRepository.findWrqLocationByLocationCodeAndIsActive(referenceDto.getCode(), 'Y');

        if (wrqLocation.isPresent()) {
            throw new CustomException(300, "Место с таким кодом уже существует");
        }

        wrqLocationRepository.save(WrqLocation.builder()
                .locationCode(referenceDto.getCode())
                .locationName(referenceDto.getName())
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
    public List<InternalReferenceDto> getWrqLocations() {

        List<WrqLocation> wrqLocationList = wrqLocationRepository.findAll();

        List<InternalReferenceDto> wrqLocationReference = new ArrayList<>();

         wrqLocationList.forEach(wrqLocation -> {
             InternalReferenceDto referenceDto = InternalReferenceDto.builder()
                     .id(wrqLocation.getId())
                     .code(wrqLocation.getLocationCode())
                     .name(wrqLocation.getLocationName())
                     .isActive(wrqLocation.getIsActive())
                     .build();
             wrqLocationReference.add(referenceDto);
         });

         return wrqLocationReference;
    }

    @Override
    public ResponseDto updateWrqLocation(Integer id, InternalReferenceDto referenceDto, String username) {

        WrqLocation wrqLocation = wrqLocationRepository.findWrqLocationById(id);

        if (wrqLocation == null) {
            throw new CustomException(301, "Место не найдено в справочнике");
        } else if (wrqLocationRepository.findWrqLocationByLocationCodeAndIsActive(referenceDto.getCode(), 'Y').isPresent() &&
                !wrqLocation.getLocationCode().equals(referenceDto.getCode())) {
            throw new CustomException(300, "Место с таким кодом уже существует");
        }

        wrqLocation.setLocationCode(referenceDto.getCode());
        wrqLocation.setLocationName(referenceDto.getName());
        wrqLocation.setIsActive(referenceDto.getIsActive());
        wrqLocation.setChangedBy(helperService.getEmpCode(username));
        wrqLocation.setChangedDate(LocalDateTime.now());
        wrqLocationRepository.save(wrqLocation);

        return ResponseDto.builder()
                .code(ResponseEnum.OK.getCode())
                .message(ResponseEnum.OK.getMessage())
                .build();
    }

}
