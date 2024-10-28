package hadiz.hanaup_backend;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// 지원되는 통화 리스트
// 환테크 : 일본, 태국, 말레이시아, 대만(42.2), 홍콩
// 적금 : 미국, 영국, 중국, 필리핀(23.7), 유럽
public class ExchangeRateUtils {

    private static final BigDecimal DEFAULT_EXCHANGE_RATE = BigDecimal.valueOf(1300);

    // SSL 검증 비활성화
    static {
        disableSslVerification();
    }

    public static void disableSslVerification() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> getSupportedCurrencies() {
        Map<String, String> supportedCurrencies = new HashMap<>();
        supportedCurrencies.put("JPY(100)", "일본 옌");
        supportedCurrencies.put("CNH", "위안화");
        supportedCurrencies.put("USD", "미국 달러");
        supportedCurrencies.put("EUR", "유로");
        supportedCurrencies.put("GBP", "영국 파운드");
        supportedCurrencies.put("THB", "태국 바트");
        supportedCurrencies.put("MYR", "말레이시아 링기트");
        supportedCurrencies.put("HKD", "홍콩 달러");
        return supportedCurrencies;
    }

    public static Map<String, BigDecimal> getExchangeRates() {
        Map<String, BigDecimal> exchangeRates = new HashMap<>();
        String authKey = "JlWTMTHWCMWHw3CA1Bg39ShrNIJFMSK5";
        String searchDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String dataType = "AP01";
        String urlString = String.format(
                "https://www.koreaexim.go.kr/site/program/financial/exchangeJSON?authkey=%s&searchdate=%s&data=%s",
                authKey, searchDate, dataType);

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getResponseCode() > 299 ? connection.getErrorStream() : connection.getInputStream()))) {

                String line;
                JSONParser parser = new JSONParser();
                while ((line = reader.readLine()) != null) {
                    JSONArray exchangeRateInfoList = (JSONArray) parser.parse(line);
                    for (Object o : exchangeRateInfoList) {
                        JSONObject exchangeRateInfo = (JSONObject) o;
                        String currencyCode = (String) exchangeRateInfo.get("cur_unit");

                        // 지원되는 통화인지 확인 후 환율 추가
                        if (getSupportedCurrencies().containsKey(currencyCode)) {
                            BigDecimal rate = parseRate(exchangeRateInfo);
                            exchangeRates.put(currencyCode, rate);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 지원되지 않는 통화에 대해 기본 환율 설정
        for (String currencyCode : getSupportedCurrencies().keySet()) {
            exchangeRates.putIfAbsent(currencyCode, DEFAULT_EXCHANGE_RATE);
        }

        return exchangeRates;
    }

    private static BigDecimal parseRate(JSONObject exchangeRateInfo) throws Exception {
        NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
        return new BigDecimal(format.parse(exchangeRateInfo.get("deal_bas_r").toString()).doubleValue());
    }
}
