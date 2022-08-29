package ch.ost.newspapercommenttodatabasefetcher.helper;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StringHelperTests {

    @Test
    void stringConcatenationWorksForFilledList() {
        StringHelper stringHelper = new StringHelper();
        List<String> strings = List.of("First", "Second", "Third");
        String result = stringHelper.joinStringListOrDefault(strings, "empty");
        assertThat(result).isEqualTo("First, Second, Third");
    }

    @Test
    void stringConcatenationWorksForEmptyList() {
        StringHelper stringHelper = new StringHelper();
        List<String> strings = new ArrayList<>();
        String result = stringHelper.joinStringListOrDefault(strings, "empty");
        assertThat(result).isEqualTo("empty");
    }

}
