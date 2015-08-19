package pl.touk.sputnik.processor.cpd;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

public class CPDFileSystemTest {

	@Test
	public void shouldNotAddUserDirToDirectorieListIfIsAFile() {
		List<String> files = new ArrayList<String>();
		String userDir = Resources.getResource("pl/touk/sputnik/processor/cpd/CpdProcessor.class").getFile();
		CPDFileSystem fileSystem = new CPDFileSystem(userDir, new ArrayList<String>(), File.separator);
		files = fileSystem.scanDirectories();
		assertThat(files).doesNotContain(userDir);
	}

	@Test
	public void GivenOneExclusionDirshouldNotScanDirectorySonar() {
		List<String> files = new ArrayList<String>();
		String userDir = System.getProperty("user.dir");
		CPDFileSystem fileSystem = new CPDFileSystem(userDir, ImmutableList.of("cpd"), File.separator);
		files = fileSystem.scanDirectories();
		assertThat(files).doesNotContain(
				userDir + File.separator + "src/main/java/pl/touk/sputnik/processor/cpd/CpdProcessor.java");
		assertThat(files).doesNotContain(
				userDir + File.separator + "src/test/java/pl/touk/sputnik/processor/cpd/CpdProcessorTest.java");
		assertThat(files).contains(userDir + File.separator + "src/test/resources/java/TestFile.java");
	}
	
	@Test
	public void GivenOneExclusionDirStartingWithSeparatorShouldNotScanDirectorySonar() {
		List<String> files = new ArrayList<String>();
		String userDir = System.getProperty("user.dir");
		CPDFileSystem fileSystem = new CPDFileSystem(userDir, ImmutableList.of("/cpd"), File.separator);
		files = fileSystem.scanDirectories();
		assertThat(files).doesNotContain(
				userDir + File.separator + "src/main/java/pl/touk/sputnik/processor/cpd/CpdProcessor.java");
		assertThat(files).doesNotContain(
				userDir + File.separator + "src/test/java/pl/touk/sputnik/processor/cpd/CpdProcessorTest.java");
		assertThat(files).contains(userDir + File.separator + "src/test/resources/java/TestFile.java");
	}
	
	@Test
	public void GivenOneExclusionComposedDirShouldNotScanDirectorySonar() {
		List<String> files = new ArrayList<String>();
		String userDir = System.getProperty("user.dir");
		CPDFileSystem fileSystem = new CPDFileSystem(userDir, ImmutableList.of("processor/cpd"), File.separator);
		files = fileSystem.scanDirectories();
		assertThat(files).doesNotContain(
				userDir + File.separator + "src/main/java/pl/touk/sputnik/processor/cpd/CpdProcessor.java");
		assertThat(files).doesNotContain(
				userDir + File.separator + "src/test/java/pl/touk/sputnik/processor/cpd/CpdProcessorTest.java");
		assertThat(files).contains(userDir + File.separator + "src/test/resources/java/TestFile.java");
	}
	
	@Test
	public void GivenOneExclusionComposedDirEndingWithSeparatorShouldNotScanDirectorySonar() {
		List<String> files = new ArrayList<String>();
		String userDir = System.getProperty("user.dir");
		CPDFileSystem fileSystem = new CPDFileSystem(userDir, ImmutableList.of("processor/cpd/"), "/");
		files = fileSystem.scanDirectories();
		assertThat(files).doesNotContain(
				userDir + File.separator + "src/main/java/pl/touk/sputnik/processor/cpd/CpdProcessor.java");
		assertThat(files).doesNotContain(
				userDir + File.separator + "src/test/java/pl/touk/sputnik/processor/cpd/CpdProcessorTest.java");
		assertThat(files).contains(userDir + File.separator + "src/test/resources/java/TestFile.java");
		assertThat(files).contains(
				userDir + File.separator + "src/main/java/pl/touk/sputnik/processor/checkstyle/CheckstyleProcessor.java");
	}

	@Test
	public void GivenTwoExclusionDirShouldNotScanDirectories() {
		List<String> files = new ArrayList<String>();
		String userDir = System.getProperty("user.dir");
		CPDFileSystem fileSystem = new CPDFileSystem(userDir, ImmutableList.of("test", "build"), File.separator);
		files = fileSystem.scanDirectories();
		assertThat(files).doesNotContain(
				userDir + File.separator + "src/test/java/pl/touk/sputnik/processor/cpd/CpdProcessorTest.java");
		assertThat(files).contains(
				userDir + File.separator + "src/main/java/pl/touk/sputnik/processor/cpd/CpdProcessor.java");
		assertThat(files).contains(userDir + File.separator + "src/main/resources/example.properties");
	}
	
	@Test
	public void GivenTwoExclusionComposedDirEndingWithSeparatorShouldNotScanDirectories() {
		List<String> files = new ArrayList<String>();
		String userDir = System.getProperty("user.dir");
		CPDFileSystem fileSystem = new CPDFileSystem(userDir, ImmutableList.of("src/test", "/build/"), File.separator);
		files = fileSystem.scanDirectories();
		assertThat(files).doesNotContain(
				userDir + File.separator + "src/test/java/pl/touk/sputnik/processor/cpd/CpdProcessorTest.java");
		assertThat(files).contains(
				userDir + File.separator + "src/main/java/pl/touk/sputnik/processor/cpd/CpdProcessor.java");
		assertThat(files).contains(userDir + File.separator + "src/main/resources/example.properties");
	}
	
	@Test
	public void GivenTwoExclusionComposedDirShouldNotScanDirectoryTest() {
		List<String> files = new ArrayList<String>();
		String userDir = System.getProperty("user.dir");
		CPDFileSystem fileSystem = new CPDFileSystem(userDir, ImmutableList.of("src/test", "/build"), File.separator);
		files = fileSystem.scanDirectories();
		assertThat(files).doesNotContain(
				userDir + File.separator + "src/test/java/pl/touk/sputnik/processor/cpd/CpdProcessorTest.java");
		assertThat(files).contains(
				userDir + File.separator + "src/main/java/pl/touk/sputnik/processor/cpd/CpdProcessor.java");
		assertThat(files).contains(userDir + File.separator + "src/main/resources/example.properties");
	}

	@Test
	public void GivenThreeExclusionDirShouldNotScanDirectoryResources() {
		List<String> files = new ArrayList<String>();
		String userDir = System.getProperty("user.dir");
		CPDFileSystem fileSystem = new CPDFileSystem(userDir, ImmutableList.of("test", "build", "resources"),
				File.separator);
		files = fileSystem.scanDirectories();
		assertThat(files).doesNotContain(
				userDir + File.separator + "src/test/java/pl/touk/sputnik/processor/cpd/CpdProcessorTest.java");
		assertThat(files).doesNotContain(userDir + File.separator + "src/test/resources/java/TestFile.java");
		assertThat(files).contains(
				userDir + File.separator + "src/main/java/pl/touk/sputnik/processor/cpd/CpdProcessor.java");
	}

}
