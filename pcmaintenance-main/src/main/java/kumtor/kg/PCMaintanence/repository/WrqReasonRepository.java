package kumtor.kg.PCMaintanence.repository;

import kumtor.kg.PCMaintanence.entity.reference.WrqReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WrqReasonRepository extends JpaRepository<WrqReason, Integer> {

    Optional<WrqReason> findWrqReasonByWrqReasonCodeAndIsActive(String wrqReasonCode, char isActive);

    WrqReason findWrqReasonById(Integer id);
}
