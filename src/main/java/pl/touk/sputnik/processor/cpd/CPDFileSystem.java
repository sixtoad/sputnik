package pl.touk.sputnik.processor.cpd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@AllArgsConstructor
@Slf4j
public class CPDFileSystem {

	@NonNull
	private String baseDir;

	@NonNull
	private List<String> directoriesExcluded = new ArrayList<String>();

	private String fileSeparator = File.separator;

	public List<String> scanDirectories() {
		List<String> directoriesList = new ArrayList<String>();
		String[] candidates = new File(getBaseDir()).list();
		if (candidates == null) {
			return directoriesList;
		}
	
		for (int i = 0; i < candidates.length; i++) {
			File tmp = buildFile(getBaseDir(), candidates[i]);
			if (tmp.isDirectory()) {
				boolean exclude = false;
				for (String string : getDirectoriesExcluded()) {
					if (isADirectory(tmp, string) || isEndingWith(tmp, string)) {
						exclude = true;
						log.info("Excluding:" + tmp.getAbsolutePath() + " because " + string);
						break;
					}
				}
				if (!exclude) {
					CPDFileSystem recursive = new CPDFileSystem(tmp.getAbsolutePath(), getDirectoriesExcluded(),
							getFileSeparator());
					directoriesList.addAll(recursive.scanDirectories());
				}
			} else {
				directoriesList.add(tmp.getAbsolutePath());
			}
		}
		return directoriesList;
	}

	private File buildFile(String dir, String fileName) {
		return new File(dir + getFileSeparator() + fileName);
	}

	private boolean isADirectory(File tmp, String directory) {
		return tmp.getAbsolutePath().contains(normalizeDirectory(directory) + getFileSeparator());
	}

	private boolean isEndingWith(File tmp, String directoryToExclude) {
		return tmp.getAbsolutePath().endsWith(normalizeDirectory(directoryToExclude));
	}

	private String normalizeDirectory(String directoryToNormalize) {
		String normalizedDiretory = directoryToNormalize.endsWith(getFileSeparator())
				? directoryToNormalize.substring(0, directoryToNormalize.length() - 1) : directoryToNormalize;
		normalizedDiretory = normalizedDiretory.startsWith(getFileSeparator()) ? normalizedDiretory
				: getFileSeparator() + normalizedDiretory;
		return normalizedDiretory;
	}
}
