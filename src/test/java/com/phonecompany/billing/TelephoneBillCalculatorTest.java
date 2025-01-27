package com.phonecompany.billing;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class TelephoneBillCalculatorTest {
    private final TelephoneBillCalculator calculator = new TelephoneBillCalculatorImpl();
    // Basic test to check if the result is not null and greater than zero
    @Test
    void testCalculateReturnsNonEmptyResult() {
        String phoneLog = "420774577453,13-01-2020 18:10:15,13-01-2020 18:12:57\n" +
                "420776562353,18-01-2020 08:59:20,18-01-2020 09:10:00";

        BigDecimal result = calculator.calculate(phoneLog);
        assertNotNull(result);
        assertTrue(result.compareTo(BigDecimal.ZERO) > 0, "Result should be greater than zero");
    }
    // Test to check if the result is correct for a call that lasts less than 5 minutes in peak time
    @Test
    void testCalculatePeakTimeUnderFiveMinutes() {
        String phoneLog = "420774577453,13-01-2020 08:00:00,13-01-2020 08:03:59";

        BigDecimal result = calculator.calculate(phoneLog);
        assertEquals(BigDecimal.valueOf(4.0), result, "Expected charge is 4.0 CZK for 4 minutes at peak rate");
    }
    // Test to check if the result is correct for a call that lasts more than 5 minutes in peak time
    @Test
    void testCalculatePeakTimeOverFiveMinutes() {
        String phoneLog = "420774577453,13-01-2020 08:00:00,13-01-2020 08:06:00";

        BigDecimal result = calculator.calculate(phoneLog);
        assertEquals(BigDecimal.valueOf(5.4), result, "Expected charge is 5.4 CZK for 7 minutes with peak rate and discount for minutes beyond 5");
    }
    // Test to check if the result is correct for a call that lasts less than 5 minutes in off-peak time
    @Test
    void testCalculateOffPeakTimeUnderFiveMinutes() {
        String phoneLog = "420774577453,13-01-2020 06:00:00,13-01-2020 06:02:59";

        BigDecimal result = calculator.calculate(phoneLog);
        assertEquals(BigDecimal.valueOf(1.5), result, "Expected charge is 1.5 CZK for 3 minutes at off-peak rate");
    }
    // Test to check if the result is correct for a call that lasts more than 5 minutes in off-peak time
    @Test
    void testCalculateOffPeakTimeOverFiveMinutes() {
        String phoneLog = "420774577453,13-01-2020 06:00:00,13-01-2020 06:06:59";

        BigDecimal result = calculator.calculate(phoneLog);
        assertEquals(BigDecimal.valueOf(2.9), result, "Expected charge is 2.9 CZK for 7 minutes with off-peak rate and discount for minutes beyond 5");
    }
    // Test to check if the result is correct for a call to a most frequent numbers
    @Test
    void testCalculateFrequentNumberDiscount() {
        String phoneLog = "420774577453,13-01-2020 08:00:00,13-01-2020 08:03:59\n" +
                "420774577453,13-01-2020 18:20:00,13-01-2020 18:23:59\n" +
                "420776562324,18-01-2020 09:00:00,13-01-2020 09:04:59\n" +
                "420776562324,18-01-2020 09:20:00,18-01-2020 09:23:59";

        BigDecimal result = calculator.calculate(phoneLog);
        assertEquals(BigDecimal.valueOf(6.0), result, "Expected charge is 6.0 CZK after discounting calls to the most frequent number");
    }
}