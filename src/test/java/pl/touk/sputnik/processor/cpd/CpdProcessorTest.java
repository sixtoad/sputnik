package pl.touk.sputnik.processor.cpd;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;

import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewResult;

public class CpdProcessorTest extends TestEnvironment {

    private CpdProcessor fixture;
    
    private String baseDir = System.getProperty("user.dir")+File.separator;
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        fixture = new CpdProcessor(config);
    }
    
    @Test
    public void shouldReturnCpdViolationsInReviewFiles() {
        // when
        ReviewResult reviewResult = fixture.process(review());
        
        // then
        assertThat(reviewResult.getViolations())
                .isNotEmpty()
                .hasSize(2)
                .extracting("message")
                .contains("Found 9 lines (60 tokens) duplication in /build/resources/test/java/TestFile2.java")
                .contains("Found 9 lines (60 tokens) duplication in /build/resources/test/java/TestFile1.java");
    }
    
    @Test
    public void shouldReturnCpdViolationsInAllProject() {
    	// given
    	Configuration config = new ConfigurationSetup()
    			.setUp(ImmutableMap.of(
    					GeneralOption.CPD_CHECK_ONLY_REVIEW_FILES.getKey()
    					, "false"));
        fixture = new CpdProcessor(config);

        // when
        ReviewResult reviewResult = fixture.process(review());
        
        // then
        String thisTestFile = baseDir +"src/test/java/pl/touk/sputnik/processor/cpd/CpdProcessorTest.java";
        String srcClassTestFile1 = baseDir +"src/test/resources/java/TestFile1.java";
        assertThat(reviewResult.getViolations())
        	.isNotEmpty().extracting("filenameOrJavaClassName")
        	.contains(thisTestFile)
        	.contains(srcClassTestFile1);
    }
    
    @Test
    public void shouldReturnCpdViolationsInAllProjectExceptDirectoryEndWithCPD() {
    	// given
    	Configuration config = new ConfigurationSetup()
    			.setUp(ImmutableMap.of(
    					GeneralOption.CPD_CHECK_ONLY_REVIEW_FILES.getKey(),
    					"false",
    					GeneralOption.CPD_EXCLUDE_DIRECTORY.getKey(),
    					"cpd"));
        fixture = new CpdProcessor(config);

        // when
        ReviewResult reviewResult = fixture.process(review());
        
        // then
        
        String thisTestFile = baseDir +"src/test/java/pl/touk/sputnik/processor/cpd/CpdProcessorTest.java";
        String srcClassTestFile1 = baseDir +"src/test/resources/java/TestFile1.java";
        assertThat(reviewResult.getViolations())
                .isNotEmpty().extracting("filenameOrJavaClassName")
                .doesNotContain(thisTestFile)
                .contains(srcClassTestFile1);
    }
    
    @Test
    public void givenExclusionResourceShouldReturnCpdViolationsInAllProjectExceptDirectoryResources() {
    	// given
    	Configuration config = new ConfigurationSetup()
    			.setUp(ImmutableMap.of(
    					GeneralOption.CPD_CHECK_ONLY_REVIEW_FILES.getKey(),
    					"false",
    					GeneralOption.CPD_EXCLUDE_DIRECTORY.getKey(),
    					"resources"));
        fixture = new CpdProcessor(config);

        // when
        ReviewResult reviewResult = fixture.process(review());
        
        // then
        
        String thisTestFile = baseDir +"src/test/java/pl/touk/sputnik/processor/cpd/CpdProcessorTest.java";
        String srcClassTestFile1 = baseDir +"src/test/resources/java/TestFile1.java";
        assertThat(reviewResult.getViolations())
                .isNotEmpty().extracting("filenameOrJavaClassName")
                .doesNotContain(srcClassTestFile1)
                .contains(thisTestFile);
    }
    
    @Test
    public void shouldReturnCpdViolationsInAllProjectExceptDirectoryContainsTestResources() {
    	// given
    	Configuration config = new ConfigurationSetup()
    			.setUp(ImmutableMap.of(
    					GeneralOption.CPD_CHECK_ONLY_REVIEW_FILES.getKey(),
    					"false",
    					GeneralOption.CPD_EXCLUDE_DIRECTORY.getKey(),
    					"test/resources"));
        fixture = new CpdProcessor(config);

        // when
        ReviewResult reviewResult = fixture.process(review());
        
        // then
        
        String thisTestFile = baseDir +"src/test/java/pl/touk/sputnik/processor/cpd/CpdProcessorTest.java";
        String srcClassTestFile1 = baseDir +"src/test/resources/java/TestFile1.java";
        String srcClassTestFile1Exclude = baseDir +"src/test/resources/java/exclude/TestFile.java";
        assertThat(reviewResult.getViolations())
                .isNotEmpty().extracting("filenameOrJavaClassName")
                .doesNotContain(srcClassTestFile1)
                .doesNotContain(srcClassTestFile1Exclude)
                .contains(thisTestFile);
    }
    
    @Test
    public void givenTwoDirectoriesSouldReturnCpdViolationsInAllProjectExceptDirectoryExcluded() {
    	// given
    	Configuration config = new ConfigurationSetup()
    			.setUp(ImmutableMap.of(
    					GeneralOption.CPD_CHECK_ONLY_REVIEW_FILES.getKey(),
    					"false",
    					GeneralOption.CPD_EXCLUDE_DIRECTORY.getKey(),
    					"test/resource/java/exclude,cpd"));
        fixture = new CpdProcessor(config);

        // when
        ReviewResult reviewResult = fixture.process(review());
        
        // then
        
        String thisTestFile = baseDir +"src/test/java/pl/touk/sputnik/processor/cpd/CpdProcessorTest.java";
        String srcClassTestFile1 = baseDir +"src/test/resources/java/TestFile1.java";
        String srcClassTestFile1Exclude = baseDir +"src/test/resources/java/exclude/TestFile.java";
        assertThat(reviewResult.getViolations())
                .isNotEmpty().extracting("filenameOrJavaClassName")
                .contains(srcClassTestFile1)
                .doesNotContain(srcClassTestFile1Exclude)
                .doesNotContain(thisTestFile);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void givenDirectoryExclusionWithWindowsSeparatorShouldThrowIllegalArgumentException() {
    	// given
    	Configuration config = new ConfigurationSetup()
    			.setUp(ImmutableMap.of(
    					GeneralOption.CPD_CHECK_ONLY_REVIEW_FILES.getKey(),
    					"false",
    					GeneralOption.CPD_EXCLUDE_DIRECTORY.getKey(),
    					"test\\resource\\java\\exclude"));
        fixture = new CpdProcessor(config);

        // when
        ReviewResult reviewResult = fixture.process(review());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void givenDirectoryInclusionWithWindowsSeparatorShouldThrowIllegalArgumentException() {
    	// given
    	Configuration config = new ConfigurationSetup()
    			.setUp(ImmutableMap.of(
    					GeneralOption.CPD_CHECK_ONLY_REVIEW_FILES.getKey(),
    					"false",
    					GeneralOption.CPD_INCLUDE_DIRECTORIES.getKey(),
    					"test\\resource\\java\\exclude"));
        fixture = new CpdProcessor(config);

        // when
        ReviewResult reviewResult = fixture.process(review());
    }
    
    @Test
    public void shouldReturnNullIfReviewIsEmpty () {
    	Review review = new Review((List<ReviewFile>)new ArrayList<ReviewFile>(), formatter);
    	ReviewResult reviewResult = fixture.process(review);
    	assertThat(reviewResult).isNull();
    }

   

    @Test
    public void shouldNotThrowReviewExceptionOnNotFoundFile() {
        // when
        catchException(fixture).process(nonexistantReview("NotExistingFile.java"));

        // then
        assertThat(caughtException()).isNull();
    }

    @Test
    public void shouldReturnNullResultWhenNoFilesToReview() {
        // given
        Review review = nonexistantReview("FileWithoutJavaExtension.txt");

        // when
        ReviewResult reviewResult = fixture.process(review);

        // then
        assertThat(reviewResult).isNull();
    }
    
    protected Review review() {
    	ArrayList<ReviewFile> listForReview = new ArrayList<>();
    	listForReview.add(reviewFile("java/TestFile1.java"));
    	listForReview.add(reviewFile("java/TestFile2.java"));
        return review(listForReview);
    }

    protected Review review(List<ReviewFile> filenameList) {
        return new Review(ImmutableList.copyOf(filenameList), formatter);
    }
    
    protected ReviewFile reviewFile(String filename) {
    	return new ReviewFile(Resources.getResource(filename).getFile());
    }
}