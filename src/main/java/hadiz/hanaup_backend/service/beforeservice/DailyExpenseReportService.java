package hadiz.hanaup_backend.service.beforeservice;

import hadiz.hanaup_backend.domain.DailyExpenseReport;
import hadiz.hanaup_backend.repository.before.DailyExpenseReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DailyExpenseReportService {

    private final DailyExpenseReportRepository repository;

    // 나라와 일자에 따른 데일리 레포트 찾기
    public DailyExpenseReport getAllByCountryAndDay(String country, int day) {
        return repository.findByCountryAndDay(country, day);
    }

    // 커스텀 저장 메서드
    public void saveOrUpdateReport(DailyExpenseReport dailyExpenseReport) {
        repository.saveCustom(dailyExpenseReport);
    }
}
