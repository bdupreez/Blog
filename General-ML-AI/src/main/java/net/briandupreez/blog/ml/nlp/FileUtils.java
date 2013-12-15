package net.briandupreez.blog.ml.nlp;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * General file reading utils
 * Created by Brian on 2013/12/08.
 */
public class FileUtils {

    /**
     * Saves an object to file.
     *
     * @param path the path
     * @param data the data
     */
    public static void saveBinaryFile(final Path path, final Object data) {
        final FileOutputStream fout;
        try {
            fout = new FileOutputStream(path.toFile());
            final ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(data);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read a binary ser file
     * @param path the path
     * @return the object
     * @throws java.io.IOException
     * @throws ClassNotFoundException
     */
    public static Object readBinaryFile(final Path path) throws IOException, ClassNotFoundException {
        ObjectInputStream objectinputstream = null;

        try {
            final FileInputStream streamIn = new FileInputStream(path.toFile());
            objectinputstream = new ObjectInputStream(streamIn);
            return objectinputstream.readObject();

        } finally {
            if (objectinputstream != null) {
                try {
                    objectinputstream.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Read the whole file into a string
     *
     * @param path the path
     * @return the contents
     */
    public static String readWholeFile(final Path path) {
        try {
            final byte[] fileArray = Files.readAllBytes(path);
            return new String(fileArray);
        } catch (final IOException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    /**
     * Read all the files in a dir
     *
     * @param dir the directory
     * @return list of paths
     */
    public static List<Path> readDirectory(final String dir) {

        final List<Path> pathList = new ArrayList<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(FileSystems.getDefault().getPath(dir))) {
            for (final Path path : ds) {
                pathList.add(path);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return pathList;
    }

    /**
     * Check if a file exists.
     *
     * @param absolutePathToFile the path to a file
     * @return true if true
     */
    public static boolean fileExists(final String absolutePathToFile) {
        final Path path = Paths.get(absolutePathToFile);
        return Files.exists(path) && Files.isRegularFile(path);
    }
}
