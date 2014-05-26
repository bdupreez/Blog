package net.briandupreez.blog.java8.io;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * RecursiveFileLineReader
 * Created by Brian on 2014-05-26.
 */
public class RecursiveFileLineReader {

    private transient static final Log LOG = LogFactory.getLog(RecursiveFileLineReader.class);

    /**
     * Get all the non empty lines from all the files with the specific extension, recursively.
     *
     * @param path      the path to start recursion
     * @param extension the file extension
     * @return list of lines
     */
    public static List<String> readAllLineFromAllFilesRecursively(final String path, final String extension) {
        final List<String> lines = new ArrayList<>();
        try (final Stream<Path> pathStream = Files.walk(Paths.get(path), FileVisitOption.FOLLOW_LINKS)) {
            pathStream
                    .filter((p) -> !p.toFile().isDirectory() && p.toFile().getAbsolutePath().endsWith(extension))
                    .forEach(p -> fileLinesToList(p, lines));
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return lines;
    }

    private static void fileLinesToList(final Path file, final List<String> lines) {
        try (Stream<String> stream = Files.lines(file, Charset.defaultCharset())) {
            stream
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(lines::add);
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }


}
