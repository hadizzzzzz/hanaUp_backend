package hadiz.hanaup_backend.service;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// 지원되는 통화 리스트
// 환테크 : 일본, 태국, 말레이시아, 대만(42.2), 중국
// 적금 : 미국, 영국, 호주, 필리핀(23.7), 유럽

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    @Builder
    @Getter
    public static class ExchangeRateDto {
        private String basicDate;  // 등록년월일
        private String currCD;     // 통화 코드
        private Double basicRate;  // 매매 기준율
    }

    private static final String URL = "https://www.kebhana.com/cms/rate/wpfxd651_01i_01.do";
    private static final List<String> SUPPORTED_CURRENCIES = Arrays.asList(
            "USD", "JPY", "EUR", "THB", "AUD", "GBP", "MYR", "CNY", "TWD", "PHP"
    );

    public static List<ExchangeRateDto> getExchangeRatesForDate(LocalDate date) throws Exception {
        // 날짜 설정
        String tmpInpStrDt = date.toString(); // 입력 날짜를 yyyy-MM-dd 형식으로
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd")); // yyyyMMdd 형식으로 변환

        // Jsoup으로 데이터 가져오기
        Document doc = Jsoup.connect(URL)
                .data("ajax", "true")
                .data("tmpInpStrDt", tmpInpStrDt)
                .data("pbldDvCd", "1")
                .data("inqStrDt", formattedDate)
                .data("inqKindCd", "1")
                .data("requestTarget", "searchContentDiv")
                .timeout(3000)
                .post();

        // 테이블 데이터 가져오기
        Elements table = doc.select("div.printdiv tbody>tr");

        List<ExchangeRateDto> exchangeRates = new ArrayList<>();

        for (Element row : table) {
            String currency = extractCurrencyCode(row.child(0).text());
            if (SUPPORTED_CURRENCIES.contains(currency)) {
                double basicRate = parseDouble(row.child(8).text());

                ExchangeRateDto exchangeRate = ExchangeRateDto.builder()
                        .basicDate(formattedDate)
                        .currCD(currency)
                        .basicRate(basicRate)
                        .build();

                exchangeRates.add(exchangeRate);
            }
        }

        return exchangeRates;
    }

    private static String extractCurrencyCode(String text) {
        int idx = text.indexOf(" ");
        return idx > 0 ? text.substring(idx + 1, idx + 4) : text.trim();
    }

    private static double parseDouble(String text) {
        text = text.replace(",", "").trim();
        return text.isEmpty() ? 0.0 : Double.parseDouble(text);
    }


    public static void main(String[] args) {
        try {
            List<ExchangeRateDto> rates = getExchangeRatesForDate(LocalDate.now());
            rates.forEach(rate -> System.out.printf(
                    "Date: %s, Currency: %s, Basic Rate: %.2f%n",
                    rate.getBasicDate(), rate.getCurrCD(), rate.getBasicRate()
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

