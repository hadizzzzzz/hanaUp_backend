package hadiz.hanaup_backend;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Map;

public class ExchangeRateTest {

    public static void main(String[] args) {
        Map<String, BigDecimal> exchangeRates = ExchangeRateUtils.getExchangeRates();
        Map<String, String> supportedCurrencies = ExchangeRateUtils.getSupportedCurrencies();

        System.out.println("환율 정보:");
        for (Map.Entry<String, BigDecimal> entry : exchangeRates.entrySet()) {
            String currencyCode = entry.getKey();
            String currencyName = supportedCurrencies.get(currencyCode);
            System.out.printf("%s (%s): %s%n", currencyCode, currencyName, entry.getValue());
        }
    }
}
