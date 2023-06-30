package kumtor.kg.PCMaintanence.service.request.impl;

import kumtor.kg.PCMaintanence.dto.reference.AssetDto;
import kumtor.kg.PCMaintanence.dto.reference.ManasReferenceDto;
import kumtor.kg.PCMaintanence.entity.reference.WrqLocation;
import kumtor.kg.PCMaintanence.entity.reference.WrqOnHold;
import kumtor.kg.PCMaintanence.entity.reference.WrqPriority;
import kumtor.kg.PCMaintanence.entity.reference.WrqReason;
import kumtor.kg.PCMaintanence.exception.CustomException;
import kumtor.kg.PCMaintanence.repository.WrqLocationRepository;
import kumtor.kg.PCMaintanence.repository.WrqOnHoldRepository;
import kumtor.kg.PCMaintanence.repository.WrqPriorityRepository;
import kumtor.kg.PCMaintanence.repository.WrqReasonRepository;
import kumtor.kg.PCMaintanence.service.reference.ManasReferenceService;
import kumtor.kg.PCMaintanence.service.request.HelperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@Slf4j
public class HelperServiceImpl implements HelperService {

    private final ManasReferenceService manasReferenceService;
    private final WrqPriorityRepository wrqPriorityRepository;
    private final WrqReasonRepository wrqReasonRepository;
    private final WrqLocationRepository wrqLocationRepository;
    private final WrqOnHoldRepository wrqOnHoldRepository;
    private final JdbcTemplate jdbcTemplate;

    public HelperServiceImpl(ManasReferenceService manasReferenceService,
                             WrqPriorityRepository wrqPriorityRepository,
                             WrqReasonRepository wrqReasonRepository,
                             WrqLocationRepository wrqLocationRepository,
                             WrqOnHoldRepository wrqOnHoldRepository,
                             JdbcTemplate jdbcTemplate) {
        this.manasReferenceService = manasReferenceService;
        this.wrqPriorityRepository = wrqPriorityRepository;
        this.wrqReasonRepository = wrqReasonRepository;
        this.wrqLocationRepository = wrqLocationRepository;
        this.wrqOnHoldRepository = wrqOnHoldRepository;
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public String getAssetCode(String assetCode) {
        try {
            return manasReferenceService.getAssetReference().stream().map(AssetDto::getCode).filter(code -> code.equals(assetCode)).findFirst().get();
        } catch (NoSuchElementException e) {
            throw new CustomException(300, "Бортовой номер не найден в справочнике");
        }
    }

    @Override
    public String getWrqNo(String wrqCode, String assetCode) {
        try {
            return manasReferenceService.getWrqReference(assetCode).stream().map(ManasReferenceDto::getCode).filter(code -> code.equals(wrqCode)).findFirst().get();
        } catch (NoSuchElementException e) {
            throw new CustomException(300, "Рабочий наряд не найден в справочнике");
        }
    }

    @Override
    public WrqPriority getPriority(String priorityCode) {
        return wrqPriorityRepository.findWrqPriorityByPriorityCodeEqualsAndIsActive(priorityCode, 'Y').orElseThrow(() -> new CustomException(101, "Приоритет не найден в справочнике"));
    }

    public WrqReason getReason(String reasonCode) {
        return wrqReasonRepository.findWrqReasonByWrqReasonCodeAndIsActive(reasonCode, 'Y').orElseThrow(() -> new CustomException(300, "Причина не найдена в справочнике"));
    }

    @Override
    public String getEmpNameByEmpCode(Integer empCode) throws DataAccessException {
        try {
            String query = "SELECT COALESCE(GivenNameRu, GivenName) + ' ' + COALESCE(SurnameRu,Surname) FROM HR_Emp_Master  WHERE StatusCode ='A' AND EmpCode = ?";
            return jdbcTemplate.queryForObject(query, String.class, empCode);
        } catch (EmptyResultDataAccessException ex) {
            throw new EmptyResultDataAccessException("Сотрудник с табельным номером : " + empCode + " не найден", 1);
        }
    }

    @Override
    public WrqLocation getWrqLocation(String code) {
        return wrqLocationRepository.findWrqLocationByLocationCodeAndIsActive(code, 'Y').orElseThrow(() ->
                new CustomException(300, "Место не найдено"));
    }

    @Override
    public WrqOnHold getWrqOnHold(String code) {
        return wrqOnHoldRepository.findWrqOnHoldByWrqOnHoldCodeAndIsActive(code, 'Y').orElseThrow(() -> new CustomException(300, "Ожидание не найдено в справочнике"));
    }

    @Override
    public Integer getEmpCode(String username) throws DataAccessException {
        try {
            String query = "SELECT ad_emp.EmpCode\n" +
                    "FROM [dbo].[TT_Security_ActiveDirectoryAccount] AS ad\n" +
                    "    INNER JOIN [dbo].[TT_ADUserEmployeeMap] AS ad_emp\n" +
                    "ON ad.WinNTAccountName=ad_emp.WinNTAccountName\n" +
                    "    INNER JOIN [dbo].[HR_Emp_Master] AS h ON ad_emp.EmpCode = h.EmpCode\n" +
                    "WHERE h.StatusCode ='A' and UPPER(LTRIM(RTRIM(ad.Mail))) = ?";
            return jdbcTemplate.queryForObject(query, Integer.class, username.trim().toUpperCase());
        } catch (EmptyResultDataAccessException ex) {
            log.error("Табельный номер сотрудника по почте : {} не найден", username);
            return null;
        }
    }

    @Override
    public String getAssetTypeName(String assetTypeCode) {
        try{
            return manasReferenceService.getAssetTypeReference()
                    .stream()
                    .filter(assetType -> assetType.getCode().equals(assetTypeCode))
                    .findFirst()
                    .get()
                    .getName();
        }catch (NoSuchElementException e){
            throw new CustomException(300, "Описание бортового номера : " + assetTypeCode + " не найдено");
        }
    }
}
