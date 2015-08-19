package pl.touk.sputnik.processor.cpd;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

public class CpdReviewProcessorFactoryTest {

	@Test
    public void shouldReturnIsEnabled() throws Exception {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(eq(GeneralOption.CPD_ENABLED))).thenReturn("true");

        ReviewProcessorFactory factory = new CpdReviewProcessorFactory();
        assertTrue(factory.isEnabled(configuration));
    }
	
	@Test
    public void shouldReturnIsNotEnabled() throws Exception {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(eq(GeneralOption.CPD_ENABLED))).thenReturn("false");

        ReviewProcessorFactory factory = new CpdReviewProcessorFactory();
        assertFalse(factory.isEnabled(configuration));
    }

    @Test
    public void shoudlReturnCpdProcessorCreated() throws Exception {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(eq(GeneralOption.CPD_ENABLED))).thenReturn("true");

        ReviewProcessorFactory factory = new CpdReviewProcessorFactory();
        assertNotNull(factory.create(configuration));
    }

}
