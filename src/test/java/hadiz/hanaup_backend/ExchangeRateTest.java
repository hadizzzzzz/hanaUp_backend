package hadiz.hanaup_backend;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

import static hadiz.hanaup_backend.ExchangeRateUtils.getWeeklyExchangeRates;


public class ExchangeRateTest {

    public static void main(String[] args) throws Exception {
        Map<String, BigDecimal> exchangeRates = ExchangeRateUtils.getExchangeRates("20241107");
        Map<String, String> supportedCurrencies = ExchangeRateUtils.getSupportedCurrencies();

        //해당 날짜 환율 정보
        System.out.println("환율 정보:");
        for (Map.Entry<String, BigDecimal> entry : exchangeRates.entrySet()) {
            String currencyCode = entry.getKey();
            String currencyName = supportedCurrencies.get(currencyCode);
            System.out.printf("%s (%s): %s%n", currencyCode, currencyName, entry.getValue());
        }

        // 주별 환율 정보 (11월 ~)
        Map<String, Map<String, BigDecimal>> weeklyRates = getWeeklyExchangeRates();
        for (String weekStartDate : weeklyRates.keySet()) {
            System.out.println("Week starting on (Monday): " + weekStartDate);
            Map<String, BigDecimal> rates = weeklyRates.get(weekStartDate);

            // 통화별로 날짜와 환율을 정리하기 위해 Map 사용
            Map<String, Map<String, BigDecimal>> currencySortedMap = new TreeMap<>();
            for (String currencyWithDate : rates.keySet()) {
                String[] parts = currencyWithDate.split("_");
                String currency = parts[0];
                String date = parts[1];

                currencySortedMap.putIfAbsent(currency, new TreeMap<>()); // 통화별로 초기화
                currencySortedMap.get(currency).put(date, rates.get(currencyWithDate));
            }

            // 정리된 데이터 출력
            for (String currency : currencySortedMap.keySet()) {
                System.out.println("  Currency: " + currency);
                Map<String, BigDecimal> dates = currencySortedMap.get(currency);
                for (String date : dates.keySet()) {
                    System.out.println("    Date: " + date + ", Rate: " + dates.get(date));
                }
            }
        }

    }
}

