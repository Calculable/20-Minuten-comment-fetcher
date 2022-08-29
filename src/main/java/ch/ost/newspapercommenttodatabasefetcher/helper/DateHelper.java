package ch.ost.newspapercommenttodatabasefetcher.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
/**
 * Contains helper methods for the handling of dates
 */
public class DateHelper {

    @Value("${settings.timeZone}")
    private String timeZone = "Europe/Zurich";

    /**
     * Converts a Date-String in the dateISO8601 into a LocalDateTime instance
     *
     * @param dateISO8601 a string representation of the date
     * @return the same date as an instance of LocalDate Time
     */
    public LocalDateTime convertISO8601DateToLocalDateTime(String dateISO8601) {
        return Instant.parse(dateISO8601).atZone(ZoneId.of(timeZone)).toLocalDateTime();
    }

    /**
     * Converts a date into a local date time for a configured time Zone
     *
     * @param dateToConvert date to convert
     * @return the same date as local date time
     */
    public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.of(timeZone))
                .toLocalDateTime();
    }
}
