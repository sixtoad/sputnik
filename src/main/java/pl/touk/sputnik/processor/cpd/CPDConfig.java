package pl.touk.sputnik.processor.cpd;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableList;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Builder;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pmd.benchmark.Benchmark;
import net.sourceforge.pmd.benchmark.Benchmarker;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.cpd.CPDConfiguration;
import net.sourceforge.pmd.cpd.CPDConfiguration.LanguageConverter;
import net.sourceforge.pmd.cpd.Language;

@Builder
@Getter
@Setter
@Slf4j
public class CPDConfig {

	private static final String SOURCE_NAME = "CPD";

	@NonNull
	List<String> filesToReview;

	@NonNull
	Integer minimumTileSize;

	Language language;

	@NonNull
	CpdCollectorRenderer renderer;

	List<String> directoriesExcluded;

	CPDConfiguration cpdConfiguration;

	String directoryOptionSeparator;

	boolean checkOnlyReviewFiles;

	List<String> includeDirectories;
	
	CPDFileSystem fileSystem;

	CPD cpd;

	private String fileSystemSeparator = File.separator;

	private String rootDirectory = System.getProperty("user.dir");

	public static CPDConfigBuilderFilesToReview builder() {
		return new CPDConfigBuilder();
	}

	public static class CPDConfigBuilder
			implements CPDConfigBuilderOptional, CPDConfigBaseBuilder, CPDConfigBuilderRenderer,
			CPDConfigBuilderFilesToReview, CPDConfigBuilderMinimumTileSize, CPDConfigBuilderLanguage {

		private String directoryOptionSeparator = ",";

		private String fileSystemSeparator = File.separator;

		private String rootDirectory = System.getProperty("user.dir");

		public CPDConfigBuilderLanguage minimumTileSize(String fromString) {
			this.minimumTileSize = Integer.parseInt(fromString);
			return this;
		}

		public CPDConfigBuilderLanguage minimumTileSize(int minimunTileSize) {
			this.minimumTileSize = minimunTileSize;
			return this;
		}

		public CPDConfigBuilderRenderer language(String fromString) {
			this.language = new LanguageConverter().convert(fromString);
			return this;
		}

		public CPDConfigBuilderOptional renderer(CpdCollectorRenderer renderer) {
			this.renderer = renderer;
			return this;
		}

		public CPDConfigBuilderOptional excludeDirectories(String excludeDirectories) {
			if (StringUtils.isNotBlank(excludeDirectories)) {
				if (excludeDirectories.contains("\\")) {
					throw new IllegalArgumentException("Directories to exclude have to use '/' as separator");
				}
				log.debug("Processing exclusions");
				this.directoriesExcluded = ImmutableList
						.copyOf(excludeDirectories.replaceAll("/", this.fileSystemSeparator).split(directoryOptionSeparator));
				log.debug("Excluded " + this.directoriesExcluded.size() + " directories");
			} else {
				this.directoriesExcluded = new ArrayList<String>(0);
			}
			return this;
		}

		public CPDConfigBuilderOptional includeDirectories(String includeDirectories) {
			if (StringUtils.isNotBlank(includeDirectories)) {
				if (includeDirectories.contains("\\")) {
					throw new IllegalArgumentException("Directories to include have to use '/' as separator");
				}
				log.debug("Processing inclusions");
				this.includeDirectories = ImmutableList.copyOf(includeDirectories
						.replaceAll("/", this.fileSystem.getFileSeparator()).split(directoryOptionSeparator));
				log.debug("Included " + this.includeDirectories.size() + " directories");
			} else {
				this.includeDirectories = new ArrayList<String>(0);
			}
			return this;
		}

		@Override
		public CPDConfigBuilderOptional directorySeparator(String directorySeparator) {
			this.directoryOptionSeparator = (StringUtils.isNotBlank(directorySeparator) ? directorySeparator : ",");
			return this;
		}

		@Override
		public CPDConfigBuilderOptional fileSystemSeparator(String fileSystemSeparator) {
			this.fileSystemSeparator = (StringUtils.isNotBlank(fileSystemSeparator) ? fileSystemSeparator
					: File.separator);
			return this;
		}

		@Override
		public CPDConfigBuilderOptional fileSystemRootDirectory(String rootDirectory) {
			this.rootDirectory = (StringUtils.isNotBlank(rootDirectory) ? rootDirectory
					: System.getProperty("user.dir"));
			return this;
		}
	}

	protected void initializeCPD() {
		log.debug("Files to review: " + this.getFilesToReview().size());
		log.info("Language: " + this.getLanguage().getName());

		cpdConfiguration = new CPDConfiguration();
		cpdConfiguration.setFiles(getFilesToReview());
		cpdConfiguration.setMinimumTileSize(getMinimumTileSize());
		cpdConfiguration.setLanguage(getLanguage());
		cpdConfiguration.setRenderer(getRenderer());
		cpdConfiguration.postContruct();
		CPDConfiguration.setSystemProperties(getCpdConfiguration());
		cpd = new CPD(getCpdConfiguration());
		this.fileSystem = new CPDFileSystem(rootDirectory, getDirectoriesExcluded(), getFileSystemSeparator());
		addAllFilesToAnalyze();

	}

	public void doCPD(@NotNull CPDConfig configuration) throws IllegalArgumentException {
		// Load the RuleSets
		long reportStart = System.nanoTime();
		initializeCPD();

		Benchmarker.mark(Benchmark.Reporting, System.nanoTime() - reportStart, 0);
		log.info(getName() + " configured starting analysis");
		cpd.go();
		log.info(getName() + " finished analysis, matches found:" + cpd.getMatches().hasNext());
		if (cpd.getMatches().hasNext()) {
			log.debug(configuration.getRenderer().render(cpd.getMatches()));
		}
	}

	private void addAllFilesToAnalyze() {
		// Add files
		if (null != getFilesToReview() && !getFilesToReview().isEmpty()) {
			if (!this.isCheckOnlyReviewFiles()) {
				List<String> directoriesList = new ArrayList<String>();
				for (Iterator<String> directoriesToInclude = getIncludeDirectories().iterator(); directoriesToInclude
						.hasNext();) {
					String directoryToScan = (String) directoriesToInclude.next();
					CPDFileSystem includedfileSystem = new CPDFileSystem(directoryToScan, getDirectoriesExcluded(),
							getFileSystemSeparator());
					directoriesList.addAll(includedfileSystem.scanDirectories());
				}
				directoriesList.addAll(this.fileSystem.scanDirectories());
				setFilesToReview(directoriesList);
			}

			addSourcesFilesToCPD();
		}
	}

	@NotNull
	public String getName() {
		return SOURCE_NAME;
	}

	/**
	 * Add source files to CPD to analyze
	 */
	protected void addSourcesFilesToCPD() {
		List<String> files = this.getFilesToReview();
		for (String fileString : files) {
			File file = new File(fileString);
			if (!file.exists()) {
				log.error("Couldn't find directory/file '" + file + "'");
			} else if (file.isDirectory()) {
				try {
					log.debug("Addidng all files in " + fileString);
					cpd.addAllInDirectory(fileString);
				} catch (IOException e) {
					log.error("Error adding directory:", e);
				}
			} else {
				// Add a single file if it is accepted by the file filter
				File directory = file.getAbsoluteFile().getParentFile();
				String filename = file.getName();

				if (filenameFilter().accept(directory, filename)) {
					try {
						cpd.add(file);
					} catch (IOException e) {
						log.error("Error adding file:", e);
					}
				}
			}
		}

	}

	public FilenameFilter filenameFilter() {
		return this.cpdConfiguration.filenameFilter();
	}

	public interface CPDConfigBaseBuilder {

	}

	public interface CPDConfigBuilderFilesToReview {
		public CPDConfigBuilderMinimumTileSize filesToReview(List<String> filesToReview);
	}

	public interface CPDConfigBuilderMinimumTileSize {
		public CPDConfigBuilderLanguage minimumTileSize(int minimunTileSize);

		public CPDConfigBuilderLanguage minimumTileSize(String fromString);
	}

	public interface CPDConfigBuilderLanguage {
		public CPDConfigBuilderRenderer language(String fromString);
	}

	public interface CPDConfigBuilderRenderer {
		public CPDConfigBuilderOptional renderer(CpdCollectorRenderer renderer);
	}

	public interface CPDConfigBuilderOptional {
		public CPDConfigBuilderOptional fileSystemSeparator(String fileSystemSeparator);

		public CPDConfigBuilderOptional fileSystemRootDirectory(String rootDirectory);

		public CPDConfigBuilderOptional directorySeparator(String directorySeparator);

		public CPDConfigBuilderOptional excludeDirectories(String excludeDirectories);

		public CPDConfigBuilderOptional includeDirectories(String includeDirectories);

		public CPDConfigBuilderOptional checkOnlyReviewFiles(boolean checkOnlyReviewFiles);

		public CPDConfig build();
	}

}
