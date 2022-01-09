package main;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JComboBox;
import static main.GUI.*;

/**
 * Contains 'back-end' methods
 *
 * @author David C
 */
public class Code {

    // Path to youtube-dl.exe
    private final String ytdlPath = "youtube-dl.exe";

    // Term used to describe youtube-dl.exe
    private final String ytdlTerm = "Program Requirement";

    // Default option string
    private final String defOpt = "Default (Best Audio and Video)";

    // Timer
    private final Timer timer;

    // FileIO helper object
    public static FileIO fio;

    // The user's Downloads folder path
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

        // Determine Downloads folder path and notify
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
        enableInterfaceElements();
    }

    /**
     * Check youtube-dl.exe requirement
     */
    public final void checkYTDL() {

        // Combine strings
        String ytdlDesc = ytdlTerm + ": '" + ytdlPath + "'";

        // Notify and print
        outputln("\nChecking " + ytdlDesc + "...");

        // If youtube-dl program does not exist
        if (!fio.isValidPath(ytdlPath)) {

            // Notify
            outputln("Not found. Will be downloaded next to exe and hidden.");

            // Run slow download command
            String prog = "curl";
            ArrayList<String> argList = new ArrayList<>();
            argList.add("-L");
            argList.add("https://yt-dl.org/latest/youtube-dl.exe");
            argList.add("--output");
            argList.add(ytdlPath);
            String desc = ytdlTerm + ": Downloading";
            runSlowComm(prog, argList, desc);

            // If download didn't work
            if (!fio.isValidPath(ytdlPath)) {

                // Notify
                handleCritErr(ytdlDesc + " could not be downloaded. "
                        + "\nCheck your internet connection.");
            }

            // Make executable hidden
            FileIO.hideFile(ytdlPath);

        } else {

            // Notify
            outputln("Found pre-existing!");

            // Determine version
            ArrayList<String> vArgList = new ArrayList<>();
            vArgList.add("--version");
            Command versionC = new Command(ytdlPath, vArgList);
            versionC.run();
            String version = Command.refineCommOutLine(versionC.getOutput());
            outputln("Version: " + version);

            // Notify
            outputln("If this version date looks too old, "
                    + "delete the hidden '" + ytdlPath + "', "
                    + "\nso it may be re-downloaded when the "
                    + "program starts next.");
        }

        // Space
        outputln("");
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
        dwlPathTry = Command.refineCommOutLine(dwlPathTry);
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
        handleCritErr("Downloads folder could not be found");

        // Never reached
        return null;
    }

    /**
     * Parse URL after Parse button is pressed
     *
     * @param refinedURL
     */
    public void parseURL(String refinedURL) {

        // If playlist detected
        if (refinedURL.contains("playlist?list=")) {

            // Notify
            outputln("\nThis program does not support playlists");
            return;
        }

        // Make extra arguments
        ArrayList<String> extra = new ArrayList<>();
        extra.add("--list-formats");

        // Run slow parsing command
        Command parseC = runSlowYTD(refinedURL, extra, "Parsing");

        // Retrieve combo box and remove items
        JComboBox formatCB = GUI.gui.getFormatCB();
        formatCB.removeAllItems();

        // If parsing was successful
        if (getYTCommStatus(parseC, "Available formats for")) {

            // Add default option
            formatCB.addItem(defOpt);

            // Get format list
            String[] formats = parseC.getOutput().split("NL:");

            // Format count
            int formatCount = 0;

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

                    // Increase format count
                    formatCount++;
                }
            }

            // Notify
            outputln("\nSuccessfully parsed URL! "
                    + formatCount + " formats discovered."
                    + "\nChoose your desired format "
                    + "in the dropdown format menu, "
                    + "then press the Download button.");

            //// Update interface
            // Enable combo box
            formatCB.setEnabled(true);

            // Disable parse button
            GUI.gui.getParseBut().setEnabled(false);

            // Enable download button
            GUI.gui.getDownloadBut().setEnabled(true);
        } else {

            // Else if URL could not be parsed
            // Notify
            outputln("\nThe URL could not be parsed.");
            outputln("Error Info:" + parseC.getErrOutput());

            // Disable combo box
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

            // Move video to Downloads folder
            String finalPath = fio.moveToDownloads(dirBefore);

            // Notify and print about path
            outputln("Path: " + finalPath);

            // Notify and print about size
            outputln("Size: " + FileIO.getReadableFileSize(finalPath));

            // If exit wanted
            if (GUI.gui.getCheckboxStatus("exit")) {

                // Exit normally
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

        // If format string is null
        // (occurs when program is untouched, needed for CLI mode)
        if (formatS == null) {

            // Return empty extra arguments
            return extraArgs;
        }

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
     * @param desc Description of process
     * @return
     */
    private Command runSlowYTD(String refinedURL, ArrayList<String> extra,
            String desc) {

        // Arguments
        ArrayList<String> argList = new ArrayList<>();

        // Add URL
        argList.add(refinedURL);

        // Enable verbose logging
        argList.add("--verbose");

        // If cookies enabled
        if (GUI.gui.isCookieFileEnabled()) {

            // Add cookie argument
            argList.add("--cookies " + FileIO.cookieFP);
        }

        // Add extra arguments
        argList.addAll(extra);

        // Add custom arguments
        argList.addAll(GUI.gui.getCustomArguments());

        // Run slow command and return
        return runSlowComm(ytdlPath, argList, desc);
    }

    /**
     * Run a slow command, giving user progress indication during execution
     *
     * @param prog Program name
     * @param argList Argument list
     * @param desc Description of process (e.g. Downloading)
     * @return
     */
    private Command runSlowComm(String prog, ArrayList<String> argList,
            String desc) {

        // Create command
        Command comm = new Command(prog, argList);

        // Output command to be run
        output(comm.toString() + "\n");

        // Start process notifications
        output("\n" + desc + "...");

        // Create printing dots task
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                Code.output(".");
            }
        };

        // Start printing dots intermittently,
        // to let user know that program is working
        timer.scheduleAtFixedRate(tt, 10, 639);

        // Run command
        comm.run();

        // Cancel process notifications (when command finished)
        tt.cancel();

        // Space
        outputln("");

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
     * Enable interface elements after initialization is finished
     */
    private void enableInterfaceElements() {

        // Enable text fields
        gui.getURLField().setEnabled(true);
        gui.getCustArgField().setEnabled(true);

        // Enable buttons
        gui.getExportBut().setEnabled(true);
        gui.getRegenBut().setEnabled(true);
        gui.getResetBut().setEnabled(true);

        // Enable checkboxes
        gui.getCheckbox("exit").setEnabled(true);
        gui.getCheckbox("cookie").setEnabled(true);
    }

    /**
     * Open the given URL
     *
     * @param url
     */
    public void openURL(String url) {

        // Run command to open URL via explorer.exe
        ArrayList<String> args = new ArrayList<>();
        args.add(url);
        Command comm = new Command("explorer.exe", args);
        comm.run();
    }

    /**
     * When critical error occurs, stop execution but keep window open until it
     * is closed by user
     *
     * @param desc
     */
    public void handleCritErr(String desc) {

        // Notify
        outputln("CRITICAL ERROR: " + desc);
        outputln("Program stopped. "
                + "Take note of the error and close the program.");

        // Sleep until user closes window
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }

    /**
     * Output an exception to both the console and output TA
     *
     * @param e
     */
    public static void outputerr(Exception e) {

        // Convert exception to string
        String s = "Err: " + e.toString();

        // Print out
        System.out.print(s);

        // Add to output TA without a new line
        GUI.gui.updateOutput(s, false);
    }

    /**
     * Output a string on a new line to both the console and output TA
     *
     * @param s
     */
    public static void outputln(String s) {

        // Print out as new line
        System.out.println(s);

        // Add to output TA with a new line
        GUI.gui.updateOutput(s, true);
    }

    /**
     * Output a string to both the console and output TA
     *
     * @param s
     */
    private static void output(String s) {

        // Print out
        System.out.print(s);

        // Add to output TA without a new line
        GUI.gui.updateOutput(s, false);
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
     * Return string in quotes
     *
     * @param s
     * @return
     */
    public static String quoteS(String s) {
        return "\"" + s + "\"";
    }
}
