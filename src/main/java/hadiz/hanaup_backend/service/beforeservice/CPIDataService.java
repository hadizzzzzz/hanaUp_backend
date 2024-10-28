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
        switch (country.toLowerCase()) {
            case "korea":
            case "한국":
                return 90.0;
            case "usa":
            case "미국":
                return 250.0;
            default:
                return 100.0; // 기본 CPI 수치 (알 수 없는 국가일 경우)
        }
    }
}
