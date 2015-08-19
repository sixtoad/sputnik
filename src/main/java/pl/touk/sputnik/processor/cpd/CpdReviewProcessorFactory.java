package pl.touk.sputnik.processor.cpd;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

public class CpdReviewProcessorFactory implements ReviewProcessorFactory<CpdProcessor> {

    @Override
    public boolean isEnabled(Configuration configuration) {
        return Boolean.valueOf(configuration.getProperty(GeneralOption.CPD_ENABLED));
    }

    @Override
    public CpdProcessor create(Configuration configuration) {
        return new CpdProcessor(configuration);
    }
}