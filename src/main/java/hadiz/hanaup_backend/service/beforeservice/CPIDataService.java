package hadiz.hanaup_backend.service.beforeservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CPIDataService {

    /**
     * 국가별 CPI 데이터를 가져오는 메서드
     * @param country 국가명
     * @return 해당 국가의 CPI 수치
     */
    public Double getCpiForCountry(String country) {
        // 국가별 CPI 수치 설정
        switch (country) {
            case "Korea":
                return 71.8;
            case "USA":
                return 100.0;
            case "Japan":
                return 53.4;
            case "Thailand":
                return 66.4;
            case "Malaysia":
                return 48.3;
            case "China":
                return 60.9;
            case "Taiwan":
                return 41.9;
            case "UK":
                return 100.4;
            case "Australia":
                return 89.1;
            case "Philippines":
                return 50.3;
            case "Europe":
                return 103.1;
            default:
                return 71.8; // 기본값 (한국)
        }
    }
}
