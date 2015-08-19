package pl.touk.sputnik.processor.cpd;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.cpd.TokenEntry;
import pl.touk.sputnik.TestEnvironment;

public class CpdCollectorRendererTest extends TestEnvironment {
	
	private CpdCollectorRenderer fixture;
	
	@Before
    public void setUp() throws Exception {
        super.setUp();
        fixture = new CpdCollectorRenderer("INFO");
    }
	
	@Test
    public void shouldReturnEmptyCpdViolationsInReviewFiles() {
        // when
        fixture.render(emptyMatches());
        // then
        assertThat(fixture.getReviewResult().getViolations())
                .isEmpty();;
    }

	@Test
    public void shouldReturnTwoCpdViolationsInReviewFiles() {
        // when
        fixture.render(oneMatch());
        // then
        assertThat(fixture.getReviewResult().getViolations())
                .isNotEmpty().hasSize(2).extracting("message")
                .contains("Found 5 lines (2 tokens) duplication in /src/test/java/pl/touk/sputnik/processor/codenarc/CodeNarcProcessorTest2.java")
                .contains("Found 5 lines (2 tokens) duplication in /src/test/java/pl/touk/sputnik/processor/codenarc/CodeNarcProcessorTest.java");;
    }
	
	@Test
    public void shouldReturnToCpdViolationsInReviewFilesInSingular() {
        // when
        fixture.render(oneMatchSingular());
        // then
        assertThat(fixture.getReviewResult().getViolations())
                .isNotEmpty().hasSize(2).extracting("message")
                .contains("Found 1 line (2 tokens) duplication in /src/test/java/pl/touk/sputnik/processor/codenarc/CodeNarcProcessorTest2.java")
                .contains("Found 1 line (2 tokens) duplication in /src/test/java/pl/touk/sputnik/processor/codenarc/CodeNarcProcessorTest.java");;
    }
	
	protected Iterator<Match> emptyMatches() {
    	ArrayList<Match> matchesList = new ArrayList<>();
        return matchesList.iterator();
    }
	
	protected Iterator<Match> oneMatch() {
    	ArrayList<Match> matchesList = new ArrayList<>();
    	Match match = new Match(2, new TokenEntry("public", "/Users/sixtocantolla/dev/workspaces/sputnik/sputnik/src/test/java/pl/touk/sputnik/processor/codenarc/CodeNarcProcessorTest.java", 15), new TokenEntry("public", "/Users/sixtocantolla/dev/workspaces/sputnik/sputnik/src/test/java/pl/touk/sputnik/processor/codenarc/CodeNarcProcessorTest2.java", 22));
        match.setLineCount(5);
    	matchesList.add(match);
    	return matchesList.iterator();
    }
	
	protected Iterator<Match> oneMatchSingular() {
    	ArrayList<Match> matchesList = new ArrayList<>();
    	Match match = new Match(2, new TokenEntry("public", "/Users/sixtocantolla/dev/workspaces/sputnik/sputnik/src/test/java/pl/touk/sputnik/processor/codenarc/CodeNarcProcessorTest.java", 15), new TokenEntry("public", "/Users/sixtocantolla/dev/workspaces/sputnik/sputnik/src/test/java/pl/touk/sputnik/processor/codenarc/CodeNarcProcessorTest2.java", 22));
        match.setLineCount(1);
    	matchesList.add(match);
    	return matchesList.iterator();
    }

}
