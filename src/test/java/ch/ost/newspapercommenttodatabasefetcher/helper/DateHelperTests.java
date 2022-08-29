package ch.ost.newspapercommenttodatabasefetcher.helper;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class DateHelperTests {

    @Test
    void ISO8601DateCanBeConverted() {
        DateHelper dateHelper = new DateHelper();
        LocalDateTime convertedDateTime = dateHelper.convertISO8601DateToLocalDateTime("2022-03-07T10:35:32Z");
        assertThat(convertedDateTime).isEqualTo(LocalDateTime.of(2022, 3, 7, 11, 35, 32));
    }

}
