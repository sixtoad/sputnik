package java;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.gmetrics.formatter.FormatterFactory;

import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.engine.visitor.score.NoScore;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewFormatterFactory;

class TestFile1 {
	public void shouldAddNoScoreToReview() {
        Review review = new Review(Collections.<ReviewFile>emptyList(), ReviewFormatterFactory.get(new ConfigurationBuilder().initFromFile("dummy")));

        new NoScore().afterReview(review);

        assertThat(review.getScores()).isEmpty();
    }
}
