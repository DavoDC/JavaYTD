package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JComboBox;

/**
 * Contains 'back-end' methods
 *
 * @author David C
 */
public class Code {

    // Default option string
    private final String defOpt = "Default/Best";

    // Timer
    private final Timer timer;

    // FileIO helper object
    public static FileIO fio;

    // Path to youtube-dl.exe
    private final String ytdlPath = "youtube-dl.exe";

    // The user's downloads folder path
    public static String downloadsPath;

    /**
     * Create a code object
     */
    public Code() {

        // Output program info
        outputln("Welcome to " + GUI.PROGRAM + " " + GUI.VERSION + ".");
        outputln("This is a GUI for the youtube-dl CLI program.");

        // Initialize timer
        timer = new Timer();

        // Initialize file IO helper
        fio = new FileIO();

        // Check youtube-dl.exe
        checkYTDL();

        // Determine downloads folder path and notify
        downloadsPath = getDwlFolderPath();
        outputln("Downloaded media will be placed here.");
        outputln("");

        // Notify about usage
        outputln("To begin, copy a video URL from your browser into "
                + "your clipboard (using Ctrl+C or right-click). ");
        outputln("Then return to this program, "
                + "click on the URL field, and the URL "
                + "will be pasted into it automatically. ");
        outputln("When you have done so, click the Parse button to"
                + " determine the video's available formats.");

        // Enable various GUI elements
        GUI.gui.getURLField().setEnabled(true);
        GUI.gui.getCustArgField().setEnabled(true);
        GUI.gui.getExportBut().setEnabled(true);
        GUI.gui.getResetBut().setEnabled(true);
        GUI.gui.getCheckbox("exit").setEnabled(true);
        GUI.gui.getCheckbox("cookie").setEnabled(true);
    }

    /**
     * Check youtube-dl.exe requirement
     */
    public final void checkYTDL() {

        // Notify and print
        outputln("\nChecking 'youtube-dl.exe'...");

        // If youtube-dl program does not exist
        if (!fio.isValidPath(ytdlPath)) {

            // Notify
            outputln("Not found. Will be downloaded next to exe and hidden");

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
                Code.outputerr(e);
            }
        } else {

            // Notify
            outputln("Found pre-existing!");

            // Determine version
            Command versionC = new Command(ytdlPath, new String[]{"--version"});
            versionC.run();
            String version = refineCommOutLine(versionC.getOutput());
            outputln("Version: " + version);
            outputln("");
        }
    }

    /**
     * Return the user's download folder path
     *
     * @return
     */
    public final String getDwlFolderPath() {

        // Notify
        outputln("Getting user's Downloads folder path...");

        // Holder for downloads path try
        String dwlPathTry;

        // Try using username
        String username = System.getProperty("user.name");
        dwlPathTry = "C:\\Users\\UN\\Downloads";
        dwlPathTry = dwlPathTry.replace("UN", username);

        // If path is valid
        if (fio.isValidPath(dwlPathTry)) {

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
        if (fio.isValidPath(dwlPathTry)) {

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
        dwlPathTry = refineCommOutLine(dwlPathTry);
        dwlPathTry = dwlPathTry.replace("'", "");

        // If path is valid
        if (fio.isValidPath(dwlPathTry)) {

            // Notify and return
            notifyDwlPath(true, 3, dwlPathTry);
            return dwlPathTry;
        } else {

            // Else if path is not valid, notify
            notifyDwlPath(false, 3, dwlPathTry);
        }

        // If no methods worked, notify and exit
        outputln("\nDownloads folder could not be found");
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

        // Make extra arguments
        ArrayList<String> extra = new ArrayList<>();
        extra.add("--list-formats");

        // Run slow parsing command
        Command parseC = runSlowYTD(refinedURL, extra, "Parsing");

        // If parsing was successful
        if (getYTCommStatus(parseC, "Available formats for")) {

            // Notify
            outputln("\nSuccessfully parsed URL! Choose your desired format "
                    + "in the dropdown format menu, "
                    + "then press the Download button.");

            // Get format list
            String[] formats = parseC.getOutput().split("NL:");

            // Get combo box
            JComboBox formatCB = GUI.gui.getFormatCB();

            // Remove options
            formatCB.removeAllItems();

            // Add default option
            formatCB.addItem(defOpt);

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
            outputln("\nThe URL could not be parsed.");
            outputln("Error Info:" + parseC.getErrOutput());

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

        // Read local directory file list before download
        String[] dirBefore = fio.getFileList("local");

        // Get extra arguments for download
        ArrayList<String> extra = getExtraDownloadArgs();

        // Run slow parsing command
        Command downloadC = runSlowYTD(refinedURL, extra, "Downloading");

        // If downloaded video successfully
        if (getYTCommStatus(downloadC, "100%")) {

            // Notify
            outputln("\nDownloaded video successfully!");

            // Move video to downloads folder
            String finalPath = fio.moveToDownloads(dirBefore);

            // Notify and print about path
            outputln("Path: " + finalPath);

            // Notify and print about size
            double sizeInBytes = new File(finalPath).length();
            double sizeInMB = sizeInBytes / (1024.0 * 1024.0);
            sizeInMB = Math.round(sizeInMB * 100.0) / 100.0;
            outputln("Size: " + sizeInMB + " MB");

            // Exit if desired
            if (GUI.gui.getCheckboxStatus("exit")) {
                System.exit(0);
            }
        } else {
            // Else if video failed to download
            outputln("\nDownload failed!");
            outputln("Error Info:" + downloadC.getErrOutput());
        }
    }

    /**
     * Get extra arguments for download
     *
     * @return Extra arguments list
     */
    private ArrayList<String> getExtraDownloadArgs() {

        // Extract format string option
        JComboBox formatCB = GUI.gui.getFormatCB();
        String formatS = (String) formatCB.getSelectedItem();

        // Extra argument holder
        ArrayList<String> extraArgs = new ArrayList<>();

        // If default format not selected
        if (!formatS.equals(defOpt)) {

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

        // Return extra arguments
        return extraArgs;
    }

    /**
     *
     * Run slow youtube-dl command with default plus given arguments
     *
     * @param refinedURL Refined URL
     * @param extra Extra arguments
     * @param desc Present-tense verb description of process
     * @return
     */
    private Command runSlowYTD(String refinedURL,
            ArrayList<String> extra, String desc) {

        // Arguments
        ArrayList<String> argList = new ArrayList<>();

        // Add URL
        argList.add(refinedURL);

        // Enable verbose logging
        argList.add("--verbose");

        // If cookies enabled
        if (GUI.gui.getCheckboxStatus("cookie")) {
            
            // Add cookie argument
            argList.add("--cookies " + FileIO.cookieFP);
        }

        // Add extra arguments
        argList.addAll(extra);

        // Add custom arguments
        argList.addAll(GUI.gui.getCustomArguments());

        // Create comm
        Command comm = new Command(ytdlPath, argList);

        // Output command to be run
        output(comm.toString() + "\n");

        // Start process notifications
        output(
                "\n" + desc + "...");

        // Create printing dots task
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                Code.output(".");
            }
        };

        // Start printing dots intermittently,
        // to let user know that program is working
        timer.scheduleAtFixedRate(tt,
                10, 777);

        // Run command
        comm.run();

        // Cancel process notifications after parsing finished
        tt.cancel();

        // Space
        outputln(
                "");

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
        outputln(msg);
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
     * Output an exception to both the console and output TA
     *
     * @param e
     */
    public static void outputerr(Exception e) {

        // Convert exception to string
        String s = "Err: " + e.toString();

        // Add to output TA without a new line
        GUI.gui.updateOutput(s, false);

        // Print out
        System.out.print(s);
    }

    /**
     * Output a string on a new line to both the console and output TA
     *
     * @param s
     */
    public static void outputln(String s) {

        // Add to output TA with a new line
        GUI.gui.updateOutput(s, true);

        // Print out as new line
        System.out.println(s);
    }

    /**
     * Output a string to both the console and output TA
     *
     * @param s
     */
    private static void output(String s) {

        // Add to output TA without a new line
        GUI.gui.updateOutput(s, false);

        // Print out
        System.out.print(s);
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
    private String refineCommOutLine(String outputLine) {
        return outputLine.replace("\nNL:", "");
    }
}
