package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;
import javax.swing.JComboBox;

/**
 * Contains 'back-end' methods
 *
 * @author David C
 */
public class Code {

    // Timer
    private final Timer timer;

    // Path to youtube-dl.exe
    private final String ytdlPath = "youtube-dl.exe";

    // The user's downloads folder path
    private final String downloadsPath;

    /**
     * Create a code object
     */
    public Code() {

        // Initialize timer
        timer = new Timer();

        // Notify
        String welcome = "Welcome to " + GUI.PROGRAM + " " + GUI.VERSION + ".";
        println(welcome);

        // First check youtube-dl.exe
        checkYTDL();

        // Determine downloads folder path
        downloadsPath = getDwlFolderPath();

        // Notify about downloads path
        notifyln("\nAcquired downloads folder path: " + downloadsPath + ".");
        notifyln("Downloaded media will be placed here.");
        notifyln("Additionally, all text files here will be "
                + "combined to create a cookie file, if so desired.");
        notifyAndPrint("");

        // Notify about usage
        notifyln("To begin, copy a video URL from your browser into "
                + "your clipboard. Then come back here, and the URL "
                + "will be automatically pasted into the URL field. "
                + "When you have done so, click the Parse button to"
                + " determine its available formats.");

        // Enable URL field and reset button
        GUI.gui.getURLField().setEnabled(true);
        GUI.gui.getResetBut().setEnabled(true);
    }

    /**
     * Check youtube-dl.exe requirement
     */
    public final void checkYTDL() {

        // Notify and print
        notifyAndPrint("\nChecking 'youtube-dl.exe'...");

        // If youtube-dl program does not exist
        if (!isValidPath(ytdlPath)) {

            // Notify
            notifyAndPrint("Not found. Will be downloaded next to exe and hidden");

            // Get youtube-dl program
            String prog = "curl";
            String[] args = new String[4];
            args[0] = "-L";
            args[1] = "https://yt-dl.org/latest/youtube-dl.exe";
            args[2] = "--output";
            args[3] = ytdlPath;
            Command comm = new Command(prog, args);
            comm.run();

            // Make executable hidden
            Path ppo = Paths.get(ytdlPath);
            try {
                Files.setAttribute(ppo, "dos:hidden", true,
                        LinkOption.NOFOLLOW_LINKS);
            } catch (IOException e) {
                System.err.println("SpecErr: " + comm.getErrOutput());
                reportErr(e);
            }
        } else {

            // Notify
            notifyAndPrint("Found pre-existing!");
            println("");

            // Tell about version
            Command versComm = runYTDL(new String[]{"--version"});
            String version = extractCommOutputLine(versComm.getOutput());
            notifyAndPrint("Version: " + version);
            println("");
        }
    }

    /**
     * Return the user's download folder path
     *
     * @return
     */
    public final String getDwlFolderPath() {

        // Notify
        println("Getting downloads path...");

        // Holder for downloads path try
        String dwlPathTry;

        // Try using username
        String username = System.getProperty("user.name");
        dwlPathTry = "C:\\Users\\UN\\Downloads";
        dwlPathTry = dwlPathTry.replace("UN", username);

        // If path is valid
        if (isValidPath(dwlPathTry)) {

            // Notify and return
            notifyDwlPath(true, 1, dwlPathTry);
            return dwlPathTry;
        } else {

            // Else if path is not valid, notify
            notifyDwlPath(false, 1, dwlPathTry);
        }

        // Try using userhome
        String userhome = System.getProperty("user.home");
        dwlPathTry = Paths.get(userhome, "Downloads").toString();

        // If path is valid
        if (isValidPath(dwlPathTry)) {

            // Notify and return
            notifyDwlPath(true, 2, dwlPathTry);
            return dwlPathTry;
        } else {

            // Else if path is not valid, notify
            notifyDwlPath(false, 2, dwlPathTry);
        }

        // Try using userprofile via console
        Command comm;
        comm = new Command("%USERPROFILE%\\Downloads", new ArrayList<>());
        comm.run();

        // Extract downloads path and refine
        dwlPathTry = comm.getErrOutput().split(" ")[1];
        dwlPathTry = extractCommOutputLine(dwlPathTry);
        dwlPathTry = dwlPathTry.replace("'", "");

        // If path is valid
        if (isValidPath(dwlPathTry)) {

            // Notify and return
            notifyDwlPath(true, 3, dwlPathTry);
            return dwlPathTry;
        } else {

            // Else if path is not valid, notify
            notifyDwlPath(false, 3, dwlPathTry);
        }

        // If no methods worked, notify and exit
        println("\nDownloads folder could not be found");
        System.exit(1);

        // Never reached
        return dwlPathTry;
    }

    /**
     * Parse URL after Parse button is pressed
     *
     * @param refinedURL
     */
    public void parseURL(String refinedURL) {

        // Start parsing process notifications
        TimerTask tt = startProcNot("Pars");

        // Get format list for URL
        ArrayList<String> extraArgList = new ArrayList<>();
        extraArgList.add("--list-formats");
        Command comm = runYTDLwithDef(refinedURL, extraArgList);

        // Cancel process notifications after parsing finished
        tt.cancel();

        // If format list retrieval was successful
        if (getYTCommStatus(comm, "Available formats for")) {

            // Notify
            notifyln("\nSuccessfully parsed URL! Choose your desired format "
                    + "in the dropdown format menu, "
                    + "then press the Download button.");

            // Get format list
            String[] formats = comm.getOutput().split("NL:");

            // Get combo box
            JComboBox formatCB = GUI.gui.getFormatCB();

            // Remove options
            formatCB.removeAllItems();

            // Add default option
            formatCB.addItem("Default");

            // Add certain output lines as formats
            for (String curFmtS : formats) {

                // Conditions for good lines
                boolean lenGood = curFmtS.length() > 3;
                boolean notOther = !curFmtS.contains("[");
                boolean firstCharNum = Character.isDigit(curFmtS.charAt(0));

                // If line passes checks
                if (lenGood && notOther && firstCharNum) {

                    // Add to combo box
                    formatCB.addItem(curFmtS);
                }
            }

            // Enable combo box
            formatCB.setEnabled(true);

            // Disable parse button
            GUI.gui.getParseBut().setEnabled(false);
        } else {

            // Else if URL could not be parsed
            // Notify
            notifyln("\nThe URL could not be parsed.");
            notifyln("Error Info:" + comm.getErrOutput());

            // Remove format options and disable format selector
            JComboBox formatCB = GUI.gui.getFormatCB();
            formatCB.removeAllItems();
            formatCB.setEnabled(false);

            // Disable download button
            GUI.gui.getDownloadBut().setEnabled(false);
        }
    }

    /**
     * Process download request after Download button is pressed
     *
     * @param refinedURL
     */
    public void processDwlReq(String refinedURL) {

        // Start download process notifications
        TimerTask tt = startProcNot("Download");

        // Read local directory file list before download
        String[] dirBefore = getLocalDirFileList();

        // Download video and save status
        boolean gotVideo = downloadVid(refinedURL);

        // Cancel process notifications after download finished
        tt.cancel();

        // If downloaded video successfully
        if (gotVideo) {

            // Notify
            notifyln("\nDownloaded video successfully!");

            // Move video to downloads folder
            String finalPath = moveToDownloads(dirBefore);

            // Notify and print about path
            notifyAndPrint("Path: " + finalPath);

            // Notify and print about size
            double sizeInBytes = new File(finalPath).length();
            double sizeInMB = sizeInBytes / (1024.0 * 1024.0);
            sizeInMB = Math.round(sizeInMB * 100.0) / 100.0;
            notifyAndPrint("Size: " + sizeInMB + " MB");

            // Exit if desired
            if (GUI.gui.getCheckboxStatus("exitCheckbox")) {
                System.exit(0);
            }
        }
    }

    /**
     * Get local directory file list
     *
     * @return
     */
    private String[] getLocalDirFileList() {

        // Holder
        String[] files = null;
        try {

            // Get file list as object array
            Stream<Path> walk = Files.walk(Paths.get("."));
            Object[] filesRaw = walk.map(x -> x.toString()).toArray();

            // Convert object array to string array
            files = Arrays.stream(filesRaw).toArray(String[]::new);

        } catch (IOException e) {
            reportErr(e);
        }
        return files;
    }

    /**
     * Download video
     *
     * @return True if was successful
     */
    private boolean downloadVid(String refinedURL) {

        // Extract format string option
        JComboBox formatCB = GUI.gui.getFormatCB();
        String formatS = (String) formatCB.getSelectedItem();

        // Extra Argument holder
        ArrayList<String> extraArgs = new ArrayList<>();

        // If default format not selected
        if (!formatS.equalsIgnoreCase("Default")) {

            // Extract code of selected format
            formatS = formatS.substring(0, 6).trim();
            int formatCode = Integer.parseInt(formatS);

            // Add to arguments
            extraArgs.add("--format");
            extraArgs.add("" + formatCode + "");
        } else {
            // Else if default is selected,
            // run with no format argument
        }

        // Run YTDL with default args and extra args
        Command comm = runYTDLwithDef(refinedURL, extraArgs);

        // Process output and return status
        return getYTCommStatus(comm, "100%");
    }

    /**
     * Move the downloaded video to the downloads folder
     *
     * @param dirBefore
     */
    private String moveToDownloads(String[] dirBefore) {

        // Read new state of directory file list
        String[] dirAfter = getLocalDirFileList();

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
     * Run youtube-dl command with default plus given arguments
     *
     * @param extraArgs
     * @return
     */
    private Command runYTDLwithDef(String refinedURL,
            ArrayList<String> extraArgs) {

        // Holder
        ArrayList<String> argList = new ArrayList<>();

        // Add URL
        argList.add(refinedURL);

        // Enable verbose logging
        argList.add("--verbose");

        // Add extra arguments
        argList.addAll(extraArgs);

        // Always try to use cookies
        // argList.add("--cookies cookies.txt");
        //
        // Run YTDL with argument list
        return runYTDL(argList);
    }

    /**
     * Start a process that takes a while.
     *
     * @param verbDesc A verb that describes the process.
     * @return TimerTask that should be cancelled after the process is finished
     */
    private TimerTask startProcNot(String verbDesc) {

        // Notify
        notify("\n" + verbDesc + "ing...");

        // Create printing dots task
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                GUI.code.notify(".");
            }
        };

        // Start printing dots intermittently,
        // to let user know that program is working
        timer.scheduleAtFixedRate(tt, 10, 777);

        // Return timer task for cancelling
        return tt;
    }

    /**
     * Run youtube-dl command with an argument list
     *
     * @param extraArgs
     * @return
     */
    private Command runYTDL(ArrayList<String> argList) {

        // Make command
        Command comm = new Command(ytdlPath, argList);

        // Run command
        comm.run();

        // Return command
        return comm;
    }

    /**
     * Run youtube-dl command with an array of arguments
     *
     * @param extraArgs
     * @return
     */
    private Command runYTDL(String[] argArray) {

        // Make command
        Command comm = new Command(ytdlPath, argArray);

        // Run command
        comm.run();

        // Return command
        return comm;
    }

    /**
     * Notify about finding of downloads path
     *
     * @param method
     * @param dwlPathTry
     * @return
     */
    private void notifyDwlPath(boolean successful, int method, String dwlPathTry) {

        // Message holder
        String msg = "Method " + method + " ";

        // Process status
        if (successful) {
            msg += "succeeded";
        } else {
            msg += "failed";
        }

        // Add path
        msg += " (found '" + dwlPathTry + "')";

        // Print message
        println(msg);
    }

    /**
     * Open the given URL
     *
     * @param url
     */
    public void openURL(String url) {

        // Run command to open URL via explorer.exe
        String[] args = {url};
        Command comm = new Command("explorer.exe", args);
        comm.run();
    }

    /**
     * Get the status of a ran youtube-dl command
     *
     * @param comm
     * @param successPart The sub string that means success if found
     * @return True if successful
     */
    private boolean getYTCommStatus(Command comm, String successPart) {

        // Return true if output contains success substring
        return comm.getOutput().contains(successPart);
    }

    /**
     * Extract a line of command output
     *
     * @param outputLine
     * @return
     */
    private String extractCommOutputLine(String outputLine) {
        return outputLine.replace("\nNL:", "");
    }

    /**
     * Report error and exit
     *
     * @param e
     */
    private void reportErr(Exception e) {

        // Print error info and exit
        System.err.print("Err: " + e.toString());
        System.exit(1);
    }

    /**
     * Send a string to both the output area and console, on new lines
     *
     * @param line
     */
    private void notifyAndPrint(String line) {
        notifyln(line);
        println(line);
    }

    /**
     * Send a string to output area only - on new line
     *
     * @param line
     */
    final void notifyln(String line) {
        GUI.gui.updateOutput(line, true);
    }

    /**
     * Send a string to output area only
     *
     * @param line
     */
    private void notify(String line) {
        GUI.gui.updateOutput(line, false);
    }

    /**
     * Return true if path is valid file or folder
     *
     * @param path
     * @return
     */
    private boolean isValidPath(String path) {
        return (new File(path)).exists();
    }

    /**
     * Print a string on a new line (wrapper)
     */
    private void println(String s) {
        System.out.println(s);
    }
}
