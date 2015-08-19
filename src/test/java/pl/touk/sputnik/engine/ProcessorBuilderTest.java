package pl.touk.sputnik.engine;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.configuration.GeneralOption;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ProcessorBuilderTest {

    @Test
    public void shouldNotBuildAnyProcessor() {
        Configuration config = new ConfigurationSetup().setUp(Collections.<String, String>emptyMap());

        assertThat(ProcessorBuilder.buildProcessors(config)).isEmpty();
    }

    @Test
    public void shouldBuildDisabledProcessors() {
    	HashMap<String, String> allDisabled = new HashMap<String, String>();
    	allDisabled.put(GeneralOption.CHECKSTYLE_ENABLED.getKey(), "false");
    	allDisabled.put(GeneralOption.FINDBUGS_ENABLED.getKey(), "false");
    	allDisabled.put(GeneralOption.PMD_ENABLED.getKey(), "false");
    	allDisabled.put(GeneralOption.CPD_ENABLED.getKey(), "false");
    	allDisabled.put(GeneralOption.SCALASTYLE_ENABLED.getKey(), "false");
    	allDisabled.put(GeneralOption.CODE_NARC_ENABLED.getKey(), "false");
    	allDisabled.put(GeneralOption.JSHINT_ENABLED.getKey(), "false");
    	allDisabled.put(GeneralOption.JSLINT_ENABLED.getKey(), "false");
    	allDisabled.put(GeneralOption.SONAR_ENABLED.getKey(), "false");
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.copyOf(allDisabled
        ));

        assertThat(ProcessorBuilder.buildProcessors(config)).isEmpty();
    }

    @Test
    public void shouldBuildAllProcessors() {
    	HashMap<String, String> allEnabled = new HashMap<String, String>();
    	allEnabled.put(GeneralOption.CHECKSTYLE_ENABLED.getKey(), "true");
    	allEnabled.put(GeneralOption.FINDBUGS_ENABLED.getKey(), "true");
    	allEnabled.put(GeneralOption.PMD_ENABLED.getKey(), "true");
    	allEnabled.put(GeneralOption.CPD_ENABLED.getKey(), "true");
    	allEnabled.put(GeneralOption.SCALASTYLE_ENABLED.getKey(), "true");
    	allEnabled.put(GeneralOption.CODE_NARC_ENABLED.getKey(), "true");
    	allEnabled.put(GeneralOption.JSHINT_ENABLED.getKey(), "true");
    	allEnabled.put(GeneralOption.JSLINT_ENABLED.getKey(), "true");
    	allEnabled.put(GeneralOption.SONAR_ENABLED.getKey(), "true");
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.copyOf(allEnabled
        ));

        assertThat(ProcessorBuilder.buildProcessors(config)).hasSize(9);
    }
}