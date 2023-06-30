package kumtor.kg.PCMaintanence.repository;

import kumtor.kg.PCMaintanence.entity.PMRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface PMRequestRepository extends JpaRepository<PMRequest, Long> {

    PMRequest findPMRequestByRequestId(Long requestId);

    PMRequest findPMRequestByRequestIdAndStatusCode(Long requestId, Integer statusCode);

    Page<PMRequest> findAll(Specification<PMRequest> pmRequestSpecification, Pageable pageable);
}
