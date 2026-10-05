package motorcomusic.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DescriptionExtractorTest {

    private DescriptionExtractor extractor;

    @Test
    void extract_withEmptyExcerptDiv_returnsEmptyString() {
        String html = "<div>"
            + "<div class=\"tc-event-excerpt\"></div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withExcerptDiv_returnsHtml() {
        String html = "<div>"
            + "<div class=\"tc-event-excerpt\"><p>Event details with <b>formatting</b></p></div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("<p>Event details with <b>formatting</b></p>");
    }

    @Test
    void extract_withNestedHtml_preservesStructure() {
        String html = "<div>"
            + "<div class=\"tc-event-excerpt\">"
            + "<p>Paragraph 1</p><p>Paragraph 2</p>"
            + "</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).contains("<p>").contains("Paragraph 1").contains("Paragraph 2");
    }

    @Test
    void extract_withNoExcerptDiv_returnsEmptyString() {
        String html = "<div><p>Some content</p></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_withPlainText_returnsHtml() {
        String html = "<div>"
            + "<div class=\"tc-event-excerpt\">Plain event description</div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Plain event description");
    }

    @Test
    void extract_withWhitespace_returnsTrimmedHtml() {
        String html = "<div>"
            + "<div class=\"tc-event-excerpt\">  Event content  </div>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Event content");
    }

    @BeforeEach
    void setUp() {
        extractor = new DescriptionExtractor();
    }
}
