package hadiz.hanaup_backend;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.cert.X509Certificate;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

// 지원되는 통화 리스트
// 환테크 : 일본, 태국, 말레이시아, 대만(42.2), 중국
// 적금 : 미국, 영국, 호주, 필리핀(23.7), 유럽
public class ExchangeRateUtils {

    private static final BigDecimal DEFAULT_EXCHANGE_RATE = BigDecimal.valueOf(1300);

    // SSL 검증 비활성화
    static {
        disableSslVerification();
    }

    public static void disableSslVerification() {
        try {
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }}, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> getSupportedCurrencies() {
        Map<String, String> supportedCurrencies = new HashMap<>();

        // 환테크 + 대만(42.2)
        supportedCurrencies.put("JPY(100)", "일본 옌");
        supportedCurrencies.put("THB", "태국 바트");
        supportedCurrencies.put("MYR", "말레이시아 링기트");
        supportedCurrencies.put("CNH", "위안화");

        // 적금 + 필리핀(23.7)
        supportedCurrencies.put("USD", "미국 달러");
        supportedCurrencies.put("EUR", "유로");
        supportedCurrencies.put("GBP", "영국 파운드");
        supportedCurrencies.put("AUD", "호주 달러");
        return supportedCurrencies;
    }

    public static Map<String, BigDecimal> getExchangeRates(String searchDate) {
        Map<String, BigDecimal> exchangeRates = new HashMap<>();
        String authKey = "jua2NsoiCMwJ09HSaRv2EbtFtL2uNOhL";
        String dataType = "AP01";
        String urlString = String.format(
                "https://www.koreaexim.go.kr/site/program/financial/exchangeJSON?authkey=%s&searchdate=%s&data=%s",
                authKey, searchDate, dataType);

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int responseCode = response.statusCode();
            System.out.println("Response Code: " + responseCode);
            if (responseCode == 200) {
                String responseBody = response.body();
                System.out.println("Response: " + responseBody);

                JSONParser parser = new JSONParser();
                JSONArray exchangeRateInfoList = (JSONArray) parser.parse(responseBody);
                for (Object o : exchangeRateInfoList) {
                    JSONObject exchangeRateInfo = (JSONObject) o;
                    String currencyCode = (String) exchangeRateInfo.get("cur_unit");

                    if (getSupportedCurrencies().containsKey(currencyCode)) {
                        try {
                            BigDecimal rate = parseRate(exchangeRateInfo);
                            // 소수점 둘째 자리로 제한
                            BigDecimal roundedRate = rate.setScale(2, BigDecimal.ROUND_HALF_UP);
                            exchangeRates.put(currencyCode, roundedRate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                System.err.println("Error response code: " + responseCode);
            }
        } catch (IOException | InterruptedException | org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }

        for (String currencyCode : getSupportedCurrencies().keySet()) {
            exchangeRates.putIfAbsent(currencyCode, DEFAULT_EXCHANGE_RATE);
        }

        return exchangeRates;
    }


    private static BigDecimal parseRate(JSONObject exchangeRateInfo) {
        try {
            NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
            return new BigDecimal(format.parse(exchangeRateInfo.get("deal_bas_r").toString()).doubleValue());
        } catch (Exception e) {
            e.printStackTrace();
            return DEFAULT_EXCHANGE_RATE; // 예외 발생 시 기본 환율 반환
        }
    }

    // 2024년 11월부터 현재까지 1주 간격으로 월화수목금 환율 가져오기
    public static Map<String, Map<String, BigDecimal>> getWeeklyExchangeRates() {
        Map<String, Map<String, BigDecimal>> weeklyExchangeRates = new LinkedHashMap<>();
        Calendar startDate = new GregorianCalendar(2024, Calendar.NOVEMBER, 4);
        Calendar endDate = Calendar.getInstance();  // 현재 날짜
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

        // 시작 날짜를 월요일로 설정
        if (startDate.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            // (일요일은 1, 월요일은 2... 토요일은 7)
            int diff = Calendar.MONDAY - startDate.get(Calendar.DAY_OF_WEEK);
            if (diff < 0) {
                diff += 7; // 이전 주 월요일로 이동
            }
            startDate.add(Calendar.DAY_OF_YEAR, diff);
        }

        HttpClient client = HttpClient.newBuilder().build();

        while (startDate.before(endDate)) {
            String weekStartDate = dateFormat.format(startDate.getTime());
            Map<String, BigDecimal> exchangeRatesForWeek = new HashMap<>();

            for (int i = 0; i < 5; i++) { // 월요일부터 금요일까지
                String formattedDate = dateFormat.format(startDate.getTime());
                String dataType = "AP01";
                String urlString = String.format(
                        "https://www.koreaexim.go.kr/site/program/financial/exchangeJSON?authkey=%s&searchdate=%s&data=%s",
                        "jua2NsoiCMwJ09HSaRv2EbtFtL2uNOhL", formattedDate, dataType);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlString))
                        .GET()
                        .build();

                try {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200) {
                        String responseBody = response.body();
                        JSONParser parser = new JSONParser();
                        JSONArray exchangeRateInfoList = (JSONArray) parser.parse(responseBody);
                        for (Object o : exchangeRateInfoList) {
                            JSONObject exchangeRateInfo = (JSONObject) o;
                            String currencyCode = (String) exchangeRateInfo.get("cur_unit");

                            if (getSupportedCurrencies().containsKey(currencyCode)) {
                                try {
                                    BigDecimal rate = parseRate(exchangeRateInfo);
                                    // 소수점 둘째 자리로 제한
                                    BigDecimal roundedRate = rate.setScale(2, BigDecimal.ROUND_HALF_UP);
                                    exchangeRatesForWeek.put(currencyCode + "_" + formattedDate, roundedRate); // 날짜와 함께 저장
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        System.err.println("Error response code: " + response.statusCode());
                    }
                } catch (IOException | InterruptedException | org.json.simple.parser.ParseException e) {
                    e.printStackTrace();
                }

                // 다음 날로 이동
                startDate.add(Calendar.DAY_OF_YEAR, 1);
            }

            // 주별 환율 저장 (월요일 날짜를 키로 사용)
            weeklyExchangeRates.put(weekStartDate, exchangeRatesForWeek);

            // 다음 주 월요일로 이동
            startDate.add(Calendar.DAY_OF_YEAR, 2); // 금요일에서 다음 주 월요일까지 이동
        }

        return weeklyExchangeRates;
    }
}
