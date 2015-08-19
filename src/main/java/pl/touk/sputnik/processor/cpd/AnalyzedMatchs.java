package pl.touk.sputnik.processor.cpd;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import pl.touk.sputnik.review.Violation;


public class AnalyzedMatchs {
	@Getter
	final private List<Violation> violations = new ArrayList<Violation>();
	@Getter
	final private List<String> sources = new ArrayList<String>();
}
