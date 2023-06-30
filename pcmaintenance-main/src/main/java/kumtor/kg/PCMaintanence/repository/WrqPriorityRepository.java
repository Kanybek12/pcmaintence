package kumtor.kg.PCMaintanence.repository;

import kumtor.kg.PCMaintanence.entity.reference.WrqPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WrqPriorityRepository extends JpaRepository<WrqPriority, Integer> {

    Optional<WrqPriority> findWrqPriorityByPriorityCodeEqualsAndIsActive(String priorityCode, char isActive);

    WrqPriority findWrqPriorityById(Integer id);
}
