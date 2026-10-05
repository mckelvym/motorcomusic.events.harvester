package motorcomusic.events.config.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ScraperConfigurationImpl.
 */
class ScraperConfigurationImplTest {

    private ScraperConfigurationImpl config;

    @BeforeEach
    void setUp() {
        config = new ScraperConfigurationImpl();
    }

    @Test
    void testGetBaseUrl() {
        assertThat(config.getBaseUrl())
            .isEqualTo("https://motorcomusic.com/");
    }

    @Test
    void testGetEventContainerSelector() {
        assertThat(config.getEventContainerSelector())
            .isEqualTo("div.tc-single-event");
    }

    @Test
    void testGetPageLoadSelector() {
        assertThat(config.getPageLoadSelector())
            .isEqualTo("div.tc-single-event");
    }

    @Test
    void testGetPageLoadTimeout() {
        assertThat(config.getPageLoadTimeout().toSecondsPart())
            .isEqualTo(10);
    }

    @Test
    void testGetRetentionDays() {
        assertThat(config.getRetentionDays())
            .isEqualTo(7);
    }
}
