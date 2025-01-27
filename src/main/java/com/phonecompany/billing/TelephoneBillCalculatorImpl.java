package com.phonecompany.billing;

import com.phonecompany.models.CallRecord;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class TelephoneBillCalculatorImpl implements TelephoneBillCalculator {
    private static final BigDecimal PEAK_RATE = BigDecimal.valueOf(1.0);
    private static final BigDecimal OFF_PEAK_RATE = BigDecimal.valueOf(0.5);
    private static final BigDecimal ADDITIONAL_RATE = BigDecimal.valueOf(0.2);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    /**
     * Calculates the total charge for a given phone log.
     * @param phoneLog
     * @return
     */
    @Override
    public BigDecimal calculate(String phoneLog) {
        List<CallRecord> records = parseLog(phoneLog);
        Map<String, BigDecimal> phoneCharges = new HashMap<>();

        for (CallRecord record : records) {
            BigDecimal charge = calculateCallCharge(record);
            phoneCharges.merge(record.getPhoneNumber(), charge, BigDecimal::add);
        }

        String freeNumber = getMostFrequentNumber(records);
        phoneCharges.remove(freeNumber);

        return phoneCharges.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Parses the phone log string into a list of CallRecord objects.
     * @param phoneLog
     * @return
     */
    private List<CallRecord> parseLog(String phoneLog) {
        List<CallRecord> callRecords = new ArrayList<>();

        if (phoneLog == null || phoneLog.isBlank()) {
            return callRecords;
        }

        String[] rows = phoneLog.contains("\n") ? phoneLog.split("\n") : new String[]{phoneLog};
        for (String row : rows) {
            String[] fields = row.split(",");
            if (fields.length == 3) {
                String phoneNumber = fields[0].trim();
                LocalDateTime start = LocalDateTime.parse(fields[1].trim(), DATE_TIME_FORMATTER);
                LocalDateTime end = LocalDateTime.parse(fields[2].trim(), DATE_TIME_FORMATTER);
                callRecords.add(new CallRecord(phoneNumber, start, end));
            }
        }
        return callRecords;
    }

    /**
     * Calculates the charge for a single call record.
     * @param record
     * @return
     */
    private BigDecimal calculateCallCharge(CallRecord record) {
        LocalDateTime start = record.getStartTime();
        LocalDateTime end = record.getEndTime();
        long totalMinutes = Duration.between(start, end).toMinutes() + 1;

        BigDecimal totalCharge = BigDecimal.ZERO;
        for (long i = 0; i < totalMinutes; i++) {
            LocalDateTime currentMinute = start.plusMinutes(i);
            BigDecimal rate = isPeakTime(currentMinute) ? PEAK_RATE : OFF_PEAK_RATE;

            if (i >= 5) {
                rate = ADDITIONAL_RATE;
            }
            totalCharge = totalCharge.add(rate);
        }
        return totalCharge;
    }
    /**
     * Checks if the given time is in peak time.
     * @param time
     * @return
     */
    private boolean isPeakTime(LocalDateTime time) {
        int hour = time.getHour();
        return hour >= 8 && hour < 16;
    }
    /**
     * Finds the most frequent number in the list of call records.
     * @param callRecords
     * @return
     */
    private String getMostFrequentNumber(List<CallRecord> callRecords) {
        if (callRecords == null || callRecords.size() <= 1) {
            return null;
        }

        Map<String, Long> frequencyMap = callRecords.stream()
                .collect(Collectors.groupingBy(CallRecord::getPhoneNumber, Collectors.counting()));

        long maxFrequency = frequencyMap.values().stream().max(Long::compare).orElse(0L);

        if (maxFrequency <= 1) {
            return null;
        }

        return frequencyMap.entrySet().stream()
                .filter(entry -> entry.getValue() == maxFrequency)
                .map(Map.Entry::getKey)
                .max(Comparator.naturalOrder()) // Get the numerically highest frequent number
                .orElse(null);
    }
}