package pl.touk.sputnik.processor.cpd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.cpd.TokenEntry;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

@Slf4j
public class CpdMatchesMapper {
	
	private static final String ROOT = System.getProperty("user.dir");
	
	private String sputnikLevelViolation = "INFO";

	public CpdMatchesMapper(String sputnikLevelViolation) {
		this.sputnikLevelViolation = sputnikLevelViolation;
	}

	public List<Violation> renderOn(final Match match) {
		final AnalyzedMatchs violations = analyzeMatchs(match);
		return buildFinalViolation(violations);
	}

	private List<Violation> buildFinalViolation(final AnalyzedMatchs violations) {
		List<Violation> violationsToReturn = new ArrayList<Violation>();
		List<String> sources = violations.getSources();
		for (Violation violation : violations.getViolations()) {

			@SuppressWarnings("unchecked")
			List<String> other = ListUtils.removeAll(sources,
					ImmutableList.<String> of(translateToRelative(violation.getFilenameOrJavaClassName())));

			String affected_sources = StringUtils.join(other, ",");
			String message = violation.getMessage() + " in " + affected_sources;
			Violation violationToReturn = new Violation(violation.getFilenameOrJavaClassName(), violation.getLine(),
					message, violation.getSeverity());
			violationsToReturn.add(violationToReturn);
		}
		return violationsToReturn;
	}

	private AnalyzedMatchs analyzeMatchs(final Match match) {
		final AnalyzedMatchs matchs = new AnalyzedMatchs();
		for (Iterator<TokenEntry> occurrences = match.iterator(); occurrences.hasNext();) {
			Violation occurrenceViolation = buildViolationFromTokenEntry(match, occurrences.next());
			matchs.getViolations().add(occurrenceViolation);
			matchs.getSources().add(translateToRelative(occurrenceViolation.getFilenameOrJavaClassName()));
		}
		return matchs;
	}

	private Violation buildViolationFromTokenEntry(final Match match, TokenEntry occurrence) {
		int line = occurrence.getBeginLine();
		String source = occurrence.getTokenSrcID();
		int endLine = line + match.getLineCount();
		String message = "Found " + match.getLineCount() + (match.getLineCount() > 1 ? " lines " : " line ") + "("
				+ match.getTokenCount() + " tokens) duplication";
		
		Violation violation = new Violation(source, endLine, message,
				Severity.valueOf(this.sputnikLevelViolation));
		
		log.debug(message + " in " + source);
		
		return violation;
	}

	public List<Violation> createViolationsFromMatches(final Iterator<Match> matches) {

		final List<Violation> violations = new ArrayList<Violation>();
		Match match;
		while (matches.hasNext()) {
			match = matches.next();
			violations.addAll(renderOn(match));
		}

		log.info("Number total violations:" + violations.size());

		return violations;
	}
	
	@NotNull
    public String translateToRelative (String reviewFilename) {
		String relativeReviewFile = StringUtils.substringAfterLast(reviewFilename, ROOT).replaceAll("\\\\", "/");
        return relativeReviewFile;
    }
}
