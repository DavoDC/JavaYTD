package main;

import java.io.File;
import java.io.IOException;
import java.net.CookieStore;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import static java.util.regex.Pattern.quote;
import java.util.stream.Stream;
import static main.Code.*;

/**
 * File I/O helper methods
 *
 * @author David
 */
public class FileIO {

    // Cookies file path
    public static final String cookieFP = "cookies.txt";

    /**
     * Generate cookie file
     */
    public void genCookieFile() {

        // Space
        outputln("");

        // If previous cookies file exists
        if (isValidPath(cookieFP)) {

            // Notify
            outputln("Previous '" + cookieFP
                    + "' file was found next to exe! "
                    + "This file will be used.");
            outputln("If you wish to regenerate the cookies file, "
                    + "delete this file and try enabling cookies again.");

            // Do not process further
            return;
        } else {

            // Else if no cookies file exists,
            // notify
            outputln("No previous cookies file found. Generating...");
        }

        // Get the list of file paths in downloads
        String[] paths = getFileList(downloadsPath);

        // Notify about number of downloads files
        int numF = paths.length;
        outputln("Detected " + numF + " files in downloads folder.");
        int fLimit = 30;
        if (numF > fLimit) {
            outputln("Since this program scans all downloads folder files, "
                    + "it would be a good idea to "
                    + "reduce the number of files present "
                    + "(to below " + fLimit + ")");
        }

        // Cookies file string
        String cookiesFS = "";

        // Valid cookie file substring
        String cookieSubstr = "# Netscape HTTP Cookie File";

        // For each filepath in paths
        for (String fp : paths) {

            // If file is probably a text file
            if (fp.contains(".txt")) {

                // Split by slash to get path parts
                String[] pathParts = fp.split(quote("\\"));

                // Get last part - the file only
                String file = pathParts[pathParts.length - 1];

                // If file is definitely a text file
                if (file.endsWith(".txt")) {

                    // Read in contents
                    String curContents = getFileAsString(fp);

                    // If seems like cookie file
                    if (curContents.contains(cookieSubstr)) {

                        // Notify
                        outputln("Retrieved data from "
                                + "valid exported cookie file: " + file);

                        // Add to cookie file string
                        cookiesFS += curContents;
                    } else {

                        // If non-valid cookie text file was found, notify
                        outputln("Found text file (" + file
                                + "), but was not "
                                + "valid exported cookie file.");
                        outputln("(i.e. didn't contain: '"
                                + cookieSubstr + "')");
                    }
                }
            }
        }

        // If cookie file string is empty
        if (cookiesFS.isEmpty()) {
            outputln("No exported cookie text files were "
                    + "found in the downloads folder.");
            outputln("Cookies file could not be generated.");
            return;
        }

        // Save combined file contents to cookies file
        saveStringToFile(cookieFP, cookiesFS);

        // Notify about success
        outputln("Successfully generated a cookies file!");
    }

    /**
     * Move the downloaded video to the downloads folder
     *
     * @param dirBefore
     * @return
     */
    public String moveToDownloads(String[] dirBefore) {

        // Read new state of directory file list
        String[] dirAfter = getFileList("local");

        // Convert new file list to ArrayList
        ArrayList<String> dirAL = new ArrayList<>();
        Collections.addAll(dirAL, dirAfter);

        // Remove each entry from the file list before
        for (String curFile : dirBefore) {
            dirAL.remove(curFile);
        }

        // Arguments
        String[] args = new String[2];

        // If one file remains
        if (dirAL.size() == 1) {

            // Get the string remaining - the new file
            String newFile = dirAL.get(0);
            newFile = newFile.replace(".\\", "");
            newFile = "\"" + newFile + "\"";

            // Move file to downloads
            String prog = "move";
            args[0] = newFile;
            args[1] = downloadsPath;
            Command comm = new Command(prog, args);
            comm.run();
        }

        // Deduce final path and return
        args[0] = args[0].replace("\"", "");
        String finalPath = args[1] + "\\" + args[0];
        return finalPath;
    }

    /**
     * Get the file list for a directory.
     *
     * @param dirPath Put path or "local" for local path
     * @return
     */
    public String[] getFileList(String dirPath) {

        // Path holder
        Path p;

        // If local path wanted
        if (dirPath.equalsIgnoreCase("local")) {

            // Use dot notation
            p = Paths.get(".");
        } else {

            // Else, get custom path
            p = Paths.get(dirPath);

            // If path is not a directory
            if (!Files.isDirectory(p)) {

                // Throw exception
                throw new IllegalArgumentException("Path was not directory");
            }
        }

        // Holder for file strings
        String[] fileStrings = null;
        try {

            // Get file list as object array
            Stream<Path> walk = Files.walk(p);
            Object[] filesRaw = walk.map(x -> x.toString()).toArray();

            // Convert object array to string array
            fileStrings = Arrays.stream(filesRaw).toArray(String[]::new);

        } catch (IOException e) {
            outputerr(e);
        }
        return fileStrings;
    }

    /**
     * Get contents of a file as a string
     *
     * @param fpS File path string
     * @return File contents
     */
    public String getFileAsString(String fpS) {

        // Holder
        String content = "";

        // Read in all data
        try {
            content = new String(Files.readAllBytes(Paths.get(fpS)));
        } catch (IOException e) {
            outputerr(e);
        }

        // Return string
        return content;
    }

    /**
     * Save a given string to a given file path
     *
     * @param fpS File path string
     * @param outputS String to be saved
     */
    public void saveStringToFile(String fpS, String outputS) {

        try {

            // Get path
            Path fp = Paths.get(fpS);

            // Get data
            byte[] data = outputS.getBytes(StandardCharsets.UTF_8);

            // Save data to path by creating new file
            Files.write(fp, data, StandardOpenOption.CREATE);

        } catch (IOException e) {
            outputerr(e);
        }
    }

    /**
     * Delete file at given file path
     *
     * @param fp
     * @return True if successful
     */
    public boolean deleteFile(String fp) {
        return new File(fp).delete();
    }

    /**
     * Return true if path is valid file or folder
     *
     * @param path
     * @return
     */
    public boolean isValidPath(String path) {
        return (new File(path)).exists();
    }

}
