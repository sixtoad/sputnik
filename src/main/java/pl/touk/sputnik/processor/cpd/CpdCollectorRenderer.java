package pl.touk.sputnik.processor.cpd;

import java.util.Iterator;
import java.util.List;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.cpd.Renderer;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Violation;

@Slf4j
public class CpdCollectorRenderer implements Renderer {

	 
	 
	 private final CpdMatchesMapper mapper;
	
	 public CpdCollectorRenderer (String sputnikLevelViolation) {
		 mapper = new CpdMatchesMapper(sputnikLevelViolation);
	 }
	 
	@Getter
	private final ReviewResult reviewResult = new ReviewResult();
	
	public String render(final Iterator<Match> matches) {
		log.debug("Render starting");
		final List<Violation> violations = mapper.createViolationsFromMatches(matches);
		buildReviewResult(violations);
		return reviewResult.toString();
	}

	private void buildReviewResult(final List<Violation> violations) {
		for (Violation violation : violations) {
			reviewResult.add(violation);
		}
	}

	
	
}
