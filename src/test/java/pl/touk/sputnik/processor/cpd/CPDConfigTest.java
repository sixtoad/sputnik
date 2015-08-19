package pl.touk.sputnik.processor.cpd;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.io.Resources;

import net.sourceforge.pmd.cpd.CPD;

public class CPDConfigTest {

	@Test
	public void shouldReturnNullIfCPDAddAllInDirectoryThrowIOException() {
		List<String> files = new ArrayList<String>();
		files.add(Resources.getResource("java").getFile());
		CPDConfig cpdConfig = CPDConfig.builder()
				.filesToReview(files)
				.minimumTileSize(25)
				.language("java")
				.renderer(new CpdCollectorRenderer("INFO"))
				.checkOnlyReviewFiles(false)
				.build();

		CPD cpd = mock(CPD.class);
		cpdConfig.setCpd(cpd);
		try {
			doThrow(IOException.class).when(cpd).addAllInDirectory(any(String.class));
			cpdConfig.addSourcesFilesToCPD();
		} catch (Exception e) {
			Assert.fail("Unexpected exception throwed"+e.getMessage());
		}
	}
	
	@Test
	public void shouldReturnNullIfCPDAddFileThrowIOException() {
		List<String> files = new ArrayList<String>();
		files.add("src/main/java/pl/touk/sputnik/processor/cpd/CpdProcessor.java");
		CPDConfig cpdConfig = CPDConfig.builder()
				.filesToReview(files)
				.minimumTileSize(25)
				.language("java")
				.renderer(new CpdCollectorRenderer("INFO"))
				.checkOnlyReviewFiles(false)
				.excludeDirectories("")
				.includeDirectories("")
				.build();
		cpdConfig.initializeCPD();
		CPD cpd = mock(CPD.class);
		cpdConfig.setCpd(cpd);
		try {
			doThrow(IOException.class).when(cpd).add(any(File.class));
			cpdConfig.addSourcesFilesToCPD();
		} catch (Exception e) {
			Assert.fail("Unexpected exception throwed:"+e.getMessage());
		}
	}

}
