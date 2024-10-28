package hadiz.hanaup_backend.service;

import hadiz.hanaup_backend.domain.TravelLog;
import hadiz.hanaup_backend.repository.TravelLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TravelLogService {

    private final TravelLogRepository travelLogRepository;

    public TravelLog findOne(Long memberId){
        return travelLogRepository.findOne(memberId);
    }
}
