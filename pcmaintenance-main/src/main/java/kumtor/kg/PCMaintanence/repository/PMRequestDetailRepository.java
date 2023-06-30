package kumtor.kg.PCMaintanence.repository;

import kumtor.kg.PCMaintanence.entity.PMRequestStage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface PMRequestDetailRepository extends JpaRepository<PMRequestStage, Long> {


    Optional<PMRequestStage> findPMRequestDetailByPmRequest_RequestIdAndStageIdAndPmRequest_StatusCodeNot(Long requestId, Long stageId, Integer statusCode);

    List<PMRequestStage> findPMRequestDetailsByPmRequest_RequestIdOrderByDateChangedAsc(Long requestId);

    List<PMRequestStage> findPMRequestDetailsByPmRequest_RequestId(Long requestId);

    PMRequestStage findTop1ByPmRequest_RequestIdAndIsCanceledAndStageIdNotOrderByStageIdDesc(Long requestId , char isCanceled, Long stageId);

    List<PMRequestStage> findPMRequestDetailsByPmRequest_RequestIdAndIsCanceled(Long requestId, char isCanceled);
}
