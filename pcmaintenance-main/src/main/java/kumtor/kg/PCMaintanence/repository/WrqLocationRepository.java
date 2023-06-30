package kumtor.kg.PCMaintanence.repository;

import kumtor.kg.PCMaintanence.entity.reference.WrqLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface WrqLocationRepository extends JpaRepository<WrqLocation, Integer> {

    Optional<WrqLocation> findWrqLocationByLocationCodeAndIsActive(String locationCode, char isActive);

    WrqLocation findWrqLocationById(Integer id);
}
