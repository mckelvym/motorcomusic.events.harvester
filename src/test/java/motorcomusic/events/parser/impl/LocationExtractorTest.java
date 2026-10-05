package motorcomusic.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LocationExtractorTest {

    private LocationExtractor extractor;

    @Test
    void extract_withEmptyLocationSpan_returnsEmptyString() {
        String html = "<div>"
            + "<div class=\"tc-event-location\">"
            + "<span></span>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withLocationSpan_returnsLocation() {
        String html = "<div>"
            + "<div class=\"tc-event-location\">"
            + "<span>Motorco Music Hall</span>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Motorco Music Hall");
    }

    @Test
    void extract_withMultipleSpans_returnsFirst() {
        String html = "<div>"
            + "<div class=\"tc-event-location\">"
            + "<span>First Location</span>"
            + "<span>Second Location</span>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("First Location");
    }

    @Test
    void extract_withNoLocationDiv_returnsEmptyString() {
        String html = "<div><p>Event info</p></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withWhitespace_returnsTrimmedLocation() {
        String html = "<div>"
            + "<div class=\"tc-event-location\">"
            + "<span>  Durham Armory  </span>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Durham Armory");
    }

    @BeforeEach
    void setUp() {
        extractor = new LocationExtractor();
    }
}
