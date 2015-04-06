package com.learningobjects.sass;

import com.cathive.sass.SassCompilationException;
import com.cathive.sass.SassContext;
import com.cathive.sass.SassFileContext;
import com.cathive.sass.SassOptions;
import com.cathive.sass.SassOutputStyle;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Compiles SASS into CSS.
 * <p>
 * This plugin scans for subdirectories of {@code sassSourceDirectory} called {@code sass} containing {@code .scss}
 * files. For each SASS file, an associated CSS file is created in a relative {@code css} directory under the
 * {@code cssTargetDirectory}.
 * <p>
 * For example, using the default parameters, if there is a SASS input file at {@code /src/main/resources/com/mycompany/sass/styles.scss},
 * then an associated CSS output file will be created at {@code /target/classes/com/mycompany/css/styles.css}.
 *
 * @author Ben Zoller
 */
@Mojo(name = "compile", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class CompileSassMojo extends AbstractMojo {
    private static final SassFilenameFilter sassFilenameFilter = new SassFilenameFilter();

    @Parameter(defaultValue = "${basedir}/src/main/resources")
    private String sassSourceDirectory;

    @Parameter(defaultValue = "${project.build.outputDirectory}")
    private String cssTargetDirectory;

    @Parameter(defaultValue = "${basedir}", readonly = true)
    private String baseDirectory;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Compiling SASS into CSS");
        Map<Path, Path> sassToCssMap = buildSassToCssMap();
        try {
            for (Map.Entry<Path, Path> entry : sassToCssMap.entrySet()) {
                Path sassFilePath = entry.getKey();
                Path cssFilePath = entry.getValue();
                compileSassToCss(sassFilePath, cssFilePath);
            }
        } catch (SassCompilationException | IOException e) {
            throw new MojoExecutionException("Failed to compile: " + e.getMessage(), e);
        }
    }

    private void compileSassToCss(Path sassFilePath, Path cssFilePath) throws IOException {
        logSassToCss(sassFilePath, cssFilePath);

        Files.createDirectories(cssFilePath.getParent());

        SassContext ctx = buildSassContext(sassFilePath);

        try (FileOutputStream out = new FileOutputStream(cssFilePath.toFile())) {
            ctx.compile(out);
        }
    }

    private void logSassToCss(Path sassFilePath, Path cssFilePath) {
        Path basePath = Paths.get(baseDirectory);
        Path sourceRelative = basePath.relativize(sassFilePath);
        Path targetRelative = basePath.relativize(cssFilePath);
        getLog().info(String.format(" %s => %s", sourceRelative, targetRelative));
    }

    private SassContext buildSassContext(Path sassFilePath) {
        SassContext ctx = SassFileContext.create(sassFilePath);

        Path sassDirectory = sassFilePath.getParent();
        SassOptions options = ctx.getOptions();
        options.setIncludePath(sassDirectory);
        options.setOutputStyle(SassOutputStyle.NESTED);
        return ctx;
    }

    private Map<Path, Path> buildSassToCssMap() {
        String[] includedDirectories = getSassDirectories();
        Map<Path, Path> sassToCssMap = new HashMap<>();
        for (String sassDirectoryRelativePath : includedDirectories) {
            addSassFilesInDirectoryToMap(sassDirectoryRelativePath, sassToCssMap);
        }
        return sassToCssMap;
    }

    private String[] getSassDirectories() {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(sassSourceDirectory);
        scanner.setIncludes(new String[]{"**/sass"});
        scanner.addDefaultExcludes();
        scanner.scan();
        return scanner.getIncludedDirectories();
    }

    private void addSassFilesInDirectoryToMap(String sassDirectoryRelativePath, Map<Path, Path> sassToCssMap) {
        Path sassPath = getSassPath(sassDirectoryRelativePath);
        Path cssPath = getCssPath(sassPath);
        File[] sassFiles = sassPath.toFile().listFiles(sassFilenameFilter);
        for (File sassFile : sassFiles) {
            addSassFileToMap(sassFile, cssPath, sassToCssMap);
        }
    }

    private Path getSassPath(String sassDirectoryRelativePath) {
        Path sourcePath = Paths.get(sassSourceDirectory);
        return sourcePath.resolve(sassDirectoryRelativePath);
    }

    private Path getCssPath(Path sassPath) {
        Path parentPath = sassPath.getParent();
        Path sourcePath = Paths.get(sassSourceDirectory);
        Path relativeParentPath = sourcePath.relativize(parentPath);
        return Paths.get(cssTargetDirectory).resolve(relativeParentPath).resolve("css");
    }

    private void addSassFileToMap(File sassFile, Path cssPath, Map<Path, Path> sassToCssMap) {
        String sassFilename = sassFile.getName();
        Path sassFilePath = sassFile.toPath();

        String name = FilenameUtils.removeExtension(sassFilename);
        String cssFilename = name + ".css";
        Path cssFilePath = cssPath.resolve(cssFilename);

        sassToCssMap.put(sassFilePath, cssFilePath);
    }

    private static class SassFilenameFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return !name.startsWith("_") && name.endsWith(".scss");
        }
    }
}
