package ni.jug.ncb.exchangerate;

import ni.jug.exchangerate.ExchangeRateClient;
import ni.jug.exchangerate.cb.CommercialBankExchangeRateScraperType;
import ni.jug.exchangerate.ncb.MonthlyExchangeRate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Map;

/**
 *
 * @author Armando Alaniz
 * @version 2.0
 * @since 2.0
 */
public class ExchangeRateTest {

    @Test
    public void testExchangeRateAtSpecificDate() {
        ExchangeRateClient client = new ExchangeRateClient();

        Assertions.assertEquals(new BigDecimal("31.9396"), client.getOfficialExchangeRate(LocalDate.of(2018, 10, 1)));
        Assertions.assertEquals(new BigDecimal("32.0679"), client.getOfficialExchangeRate(LocalDate.of(2018, 10, 31)));
    }

    @Test
    public void testMonthlyExchangeRateAtSpecificDate() {
        ExchangeRateClient client = new ExchangeRateClient();
        MonthlyExchangeRate monthlyExchangeRate = client.getOfficialMonthlyExchangeRate(YearMonth.of(2018, 10));

        Assertions.assertEquals(31, monthlyExchangeRate.size());
        Assertions.assertEquals(new BigDecimal("31.9396"), monthlyExchangeRate.getFirstExchangeRate());
        Assertions.assertEquals(new BigDecimal("32.0679"), monthlyExchangeRate.getLastExchangeRate());
        Assertions.assertEquals(new BigDecimal("31.9994"), monthlyExchangeRate.getExchangeRate(LocalDate.of(2018, 10, 15)));
        Assertions.assertEquals(BigDecimal.ZERO, monthlyExchangeRate.getExchangeRate(LocalDate.of(2018, 9, 30)));
        Assertions.assertFalse(monthlyExchangeRate.isIncomplete());

        LocalDate date1 = LocalDate.of(2018, 10, 1);
        LocalDate date2 = LocalDate.of(2018, 10, 15);
        Map<LocalDate, BigDecimal> rangeOfValues = monthlyExchangeRate.getExchangeRateBetween(date1, date2);
        Assertions.assertEquals(15, rangeOfValues.size());
        Assertions.assertEquals(new BigDecimal("31.9396"), rangeOfValues.get(date1));
        Assertions.assertEquals(new BigDecimal("31.9994"), rangeOfValues.get(date2));
    }

    @Test
    public void testValidationOfYear() {
        ExchangeRateClient client = new ExchangeRateClient();

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            client.getOfficialExchangeRate(LocalDate.of(2011, 12, 31));
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            client.getOfficialMonthlyExchangeRate(YearMonth.of(2011, Month.DECEMBER));
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            client.getOfficialMonthlyExchangeRate(YearMonth.of(2011, 10));
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            client.getOfficialMonthlyExchangeRate(LocalDate.of(2011, 12, 1));
        });
    }

    @Test
    public void testLAFISE() {
        CommercialBankExchangeRateScraperType scraper = CommercialBankExchangeRateScraperType.LAFISE;
        scraper.extractData();
    }

    @Test
    public void testBAC() {
        CommercialBankExchangeRateScraperType scraper = CommercialBankExchangeRateScraperType.BAC;
        scraper.extractData();
    }
}
