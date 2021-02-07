package main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
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

    // Substring used to determine whether a text file is an exported cookie file
    private final String validCookieSS = "# Netscape HTTP Cookie File";

    /**
     * Generate cookie file if cookies are enabled
     *
     * @param regen Delete and regenerate option
     */
    public void genCookieFile(boolean regen) {

        // If cookies are not enabled
        if (!GUI.gui.isCookieFileEnabled()) {

            // Do not process further
            return;
        }

        // Space
        outputln("");

        // If previous cookies file exists
        if (isValidPath(cookieFP)) {

            // Notify
            outputln("Previous '" + cookieFP + "' file was found.");

            // If regeneration is wanted
            if (regen) {

                // Delete cookie file
                deleteFile(cookieFP);

                // Notify
                outputln("Deleted previous cookies file. Regenerating...");

            } else {

                // Else if regeneration is not wanted
                outputln("This cookies file will be used. To make "
                        + "a new cookies file, use the regenerate button.");

                // Do not process further
                return;
            }

        } else {

            // Else if no cookies file exists,
            // notify
            outputln("No previous cookies file found. Generating...");
        }

        // Get the list of file paths in downloads
        String[] paths = getFileList(downloadsPath);

        // Notify about number of downloads files
        int numF = paths.length;
        outputln("Detected " + numF + " files in Downloads folder.");
        int fLimit = 30;
        if (numF > fLimit) {
            outputln("Since this program scans all Downloads folder files, "
                    + "it would be a good idea to "
                    + "reduce the number of files present "
                    + "(to below " + fLimit + ")");
        }

        // Cookies file string
        String cookiesFS = "";

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

                    // Process text file
                    String contents = processTxtFileinDwl(fp, file);

                    // If contents is not null
                    if (contents != null) {

                        // Add to cookie file string
                        cookiesFS += contents;
                    }
                }
            }
        }

        // If cookie file string is empty
        if (cookiesFS.isEmpty()) {
            outputln("No exported cookie text files were "
                    + "found in the Downloads folder.");
            outputln("Cookies file could not be generated.");
            return;
        }

        // Save combined file contents to cookies file
        saveStringToFile(cookieFP, cookiesFS);

        // Notify about success
        outputln("Successfully generated a cookies file!");
    }

    /**
     * Move the downloaded video to the Downloads folder
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

        // Move command arguments
        ArrayList<String> movArgs = new ArrayList<>();

        // If one file remains
        if (dirAL.size() == 1) {

            // Get the string remaining - the new file
            // This must be the downloaded media file
            String newFile = dirAL.get(0);
            newFile = newFile.replace(".\\", "");

            // Move file to downloads
            String prog = "move";
            movArgs.add(Code.quoteS(newFile));
            movArgs.add(Code.quoteS(downloadsPath));
            Command comm = new Command(prog, movArgs);
            comm.run();

            // Deduce final path and return
            String finalPath = movArgs.get(1) + "\\";
            finalPath += movArgs.get(0);
            finalPath = finalPath.replace("\"", "");
            return finalPath;
        } else {

            // Else, there is no new file detected
            outputln("Error: Could not move file to downloads");
            outputln("Debug Info:");
            outputln("DirBefore: " + Arrays.toString(dirBefore));
            outputln("DirAfter: " + Arrays.toString(dirAfter));

            // Return where file is
            return "next to exe";
        }
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
     * Process a text file in the Downloads folder
     *
     * @param fp The file path
     * @param file The file's name and extension
     * @return Contents if valid cookie file, otherwise null
     */
    private String processTxtFileinDwl(String fp, String file) {

        // Read in contents
        String curContents = getFileAsString(fp);

        // If seems like cookie file
        if (curContents.contains(validCookieSS)) {

            // Notify
            outputln("Retrieved data from valid exported cookie file: "
                    + file);

            // Return contents for adding to cookies file
            return curContents;
        } else {

            // If non-valid cookie text file was found, notify
            outputln("Found text file (" + file + "), but was not valid "
                    + "exported cookie file.");
            outputln("(i.e. didn't contain: '"
                    + validCookieSS + "')");

            // Return null
            return null;
        }
    }

    /**
     * Get readable size of a given file
     *
     * @param fp File path
     * @return
     */
    public static String getReadableFileSize(String fp) {

        // Get raw file size
        long size = 0;
        try {
            size = Files.size(Paths.get(fp));
        } catch (IOException ex) {
            outputerr(ex);
        }

        // If size is 0
        if (size <= 0) {

            // Return 0 bytes
            return "0 B";
        }

        // Add size
        int dg = (int) (Math.log10(size) / Math.log10(1024));
        String rs;
        rs = new DecimalFormat("#,##0.#").format(size / Math.pow(1024, dg));

        // Add unit
        String[] units = {"B", "kB", "MB", "GB", "TB"};
        rs += " " + units[dg];

        // Return readable size
        return rs;
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
     * Hide a given file
     *
     * @param fp File path
     */
    public static void hideFile(String fp) {
        try {
            
            // Get path
            Path path = Paths.get(fp);
            
            // Set hidden attribute to true
            LinkOption lo = LinkOption.NOFOLLOW_LINKS;
            Files.setAttribute(path, "dos:hidden", true, lo);
        } catch (IOException e) {
            Code.outputerr(e);
        }
    }

    /**
     * Delete file at given file path
     *
     * @param fp
     * @return True if successful
     */
    public boolean deleteFile(String fp) {

        // If file does not exist
        if (!isValidPath(fp)) {

            // Return false as cannot delete
            return false;
        }

        // Return true if file was deleted
        return new File(fp).delete();
    }

    /**
     * Return true if path is valid file or folder
     *
     * @param path
     * @return True if valid
     */
    public boolean isValidPath(String path) {
        return (new File(path)).exists();
    }
}
