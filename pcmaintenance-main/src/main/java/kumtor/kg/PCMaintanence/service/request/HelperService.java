package kumtor.kg.PCMaintanence.service.request;

import kumtor.kg.PCMaintanence.entity.reference.WrqLocation;
import kumtor.kg.PCMaintanence.entity.reference.WrqOnHold;
import kumtor.kg.PCMaintanence.entity.reference.WrqPriority;
import kumtor.kg.PCMaintanence.entity.reference.WrqReason;
import org.springframework.dao.DataAccessException;

public interface HelperService {

    String getAssetCode(String assetCode);

    String getWrqNo(String wrqCode, String assetCode);

    WrqPriority getPriority(String priorityCode);

    WrqReason getReason(String reasonCode);

    String getEmpNameByEmpCode(Integer empCode) throws DataAccessException;

    WrqLocation getWrqLocation(String code);

    WrqOnHold getWrqOnHold(String code);

    Integer getEmpCode(String username) throws DataAccessException;

    String getAssetTypeName(String assetTypeCode);
}
