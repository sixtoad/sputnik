package pl.touk.sputnik.processor.cpd;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pmd.benchmark.Benchmark;
import net.sourceforge.pmd.benchmark.Benchmarker;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.cpd.CPDConfiguration;
import net.sourceforge.pmd.cpd.CPDConfiguration.LanguageConverter;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewException;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.filter.CpdFilter;
import pl.touk.sputnik.review.transformer.FileNameTransformer;

@Slf4j
public class CpdProcessor implements ReviewProcessor {
	private static final String SOURCE_NAME = "CPD";
	private static final String CPD_DIRECTORY_OPTION_PATH_SEPARATOR = ",";
	private CpdCollectorRenderer renderer;

	@NotNull
	private final Configuration configuration;

	public CpdProcessor(Configuration configuration) {
		this.configuration = configuration;
	}

	@Nullable
	@Override
	public ReviewResult process(@NotNull Review review) {
		log.info("Starting with " + getName() + " analysis");
		List<String> filesToReview = review.getFiles(new CpdFilter(), new FileNameTransformer());
		if (filesToReview.isEmpty()) {
			return null;
		}
		CPDConfig configurationCpd = CPDConfig.builder().filesToReview(filesToReview)
				.minimumTileSize(this.configuration.getProperty(GeneralOption.CPD_MINIMUN_TILE_SIZE))
				.language(this.configuration.getProperty(GeneralOption.CPD_LANGUAGE))
				.renderer(new CpdCollectorRenderer(configuration.getProperty(GeneralOption.CPD_LEVEL_DUPLICATION).toUpperCase()))
				.directorySeparator(CPD_DIRECTORY_OPTION_PATH_SEPARATOR)
				.fileSystemSeparator(File.separator)
				.fileSystemRootDirectory(System.getProperty("user.dir"))
				.excludeDirectories(this.configuration.getProperty(GeneralOption.CPD_EXCLUDE_DIRECTORY))
				.includeDirectories(this.configuration.getProperty(GeneralOption.CPD_INCLUDE_DIRECTORIES))
				.checkOnlyReviewFiles(
						Boolean.valueOf(this.configuration.getProperty(GeneralOption.CPD_CHECK_ONLY_REVIEW_FILES)))
				.build();
		try {
			configurationCpd.doCPD(configurationCpd);
		} catch (RuntimeException e) {
			log.error(
					"CPD processing error. Something wrong with configuration or analyzed files are not in workspace.",
					e);
			throw new ReviewException("CPD processing error", e);
		}
		return configurationCpd.getRenderer() != null ? configurationCpd.getRenderer().getReviewResult() : null;
	}

	@NotNull
	@Override
	public String getName() {
		return SOURCE_NAME;
	}

}
