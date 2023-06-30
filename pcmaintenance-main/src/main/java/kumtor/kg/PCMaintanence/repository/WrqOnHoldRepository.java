package kumtor.kg.PCMaintanence.repository;

import kumtor.kg.PCMaintanence.entity.reference.WrqOnHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface WrqOnHoldRepository extends JpaRepository<WrqOnHold, Integer> {

    Optional<WrqOnHold> findWrqOnHoldByWrqOnHoldCodeAndIsActive(String wrqOnHoldCode, char isActive);

    WrqOnHold findWrqOnHoldById(Integer id);
}
