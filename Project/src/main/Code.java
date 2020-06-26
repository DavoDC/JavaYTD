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
import java.util.stream.Stream;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

/**
 * Contains 'back-end' methods
 *
 * @author David C
 */
public class Code {

    // Program file path
    private String progPath = "youtube-dl.exe";

    /**
     * Create a code object
     */
    public Code() {

        // Notify
        System.out.println("\nStatus of 'youtube-dl.exe': ");

        // If youtube-dl program does not exist
        File progExe = new File(progPath);
        if (!progExe.exists()) {

            // Notify
            System.out.println("Not found. Will be downloaded");

            // Get youtube-dl program
            String prog = "curl";
            String[] args = new String[4];
            args[0] = "-L";
            args[1] = "https://yt-dl.org/latest/youtube-dl.exe";
            args[2] = "--output";
            args[3] = progPath;
            Command comm = new Command(prog, args);
            comm.run();

            // Make executable hidden
            Path ppo = Paths.get(progPath);
            try {
                Files.setAttribute(ppo, "dos:hidden", true,
                        LinkOption.NOFOLLOW_LINKS);
            } catch (IOException e) {
                System.err.println("SpecErr: " + comm.getErrOutput());
                reportErr(e);
            }
        } else {
            // Notify
            System.out.println("Found pre-existing");
        }

        // Space
        System.out.println("\n");
    }

    /**
     * Verify URL and extract formats if valid
     */
    public void parseURL() {

        // Get format list for URL
        Command comm = runYTDL(new String[]{"--list-formats"});

        // Process output
        boolean success = processYTComm(comm, "Available formats for");

        // If format list retrieval was successful
        if (success) {

            // Get format list
            String[] formats = comm.getOutput().split("NL:");

            // Get combo box
            JComboBox formatCB = GUI.gui.getFormatCB();

            // Remove options
            formatCB.removeAllItems();

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

            // Else if URL was invalid
            // Remove format options and disable format selector
            JComboBox formatCB = GUI.gui.getFormatCB();
            formatCB.removeAllItems();
            formatCB.setEnabled(false);

            // Disable download button
            GUI.gui.getDownloadBut().setEnabled(false);
        }
    }

    /**
     * Move the downloaded video to the downloads folder
     *
     * @param dirBefore
     */
    private void moveToDownloads(String[] dirBefore) {

        // Read new state of directory file list
        String[] dirAfter = getLocalDirFileList();

        // Convert new file list to ArrayList
        ArrayList<String> dirAL = new ArrayList<>();
        Collections.addAll(dirAL, dirAfter);

        // Remove each entry from the file list before
        for (String curFile : dirBefore) {
            dirAL.remove(curFile);
        }

        // If one file remains
        if (dirAL.size() == 1) {

            // Get the string remaining - the new file
            String newFile = dirAL.get(0);
            newFile = newFile.replace(".\\", "");
            newFile = "\"" + newFile + "\"";

            // Move file to downloads
            String prog = "move";
            String[] args = new String[2];
            args[0] = newFile;
            args[1] = "C:\\Users\\%username%\\Downloads";
            Command comm = new Command(prog, args);
            comm.run();
        }
    }

    /**
     * Process youtube-dl command
     *
     * @param comm
     * @param successPart
     * @return True if successful
     */
    private boolean processYTComm(Command comm, String successPart) {

        // Status
        boolean success;

        // Output
        String output = "Output: ";

        // If output indicates success
        if (comm.getOutput().contains(successPart)) {

            // Give last line of successful output and notify
            String lastOut = getLastProcOutLine(comm.getOutput());
            output += lastOut.trim() + " (Successful)";
            success = true;
        } else {

            // Else if not successful,
            // give last error line and notify
            String lastErr = getLastProcOutLine(comm.getErrOutput());
            output += lastErr;
            success = false;
        }

        // Display output line
        JLabel outLabel;
        outLabel = (JLabel) GUI.gui.getComponentByName("outLabel");
        outLabel.setText(output);

        // Return status
        return success;
    }

    /**
     * Run youtube-dl command
     *
     * @return
     */
    private Command runYTDL(String[] extraArgs) {

        // Program name
        String prog = "youtube-dl.exe";

        // Get URL
        String url = GUI.gui.getURLField().getText();
        String[] urlArg = {url};

        // Add extra arguments
        String[] args = Stream.concat(
                Arrays.stream(urlArg),
                Arrays.stream(extraArgs))
                .toArray(String[]::new);

        // Make command and run
        Command comm = new Command(prog, args);
        comm.run();

        // Return command
        return comm;
    }

    /**
     * Download video
     *
     * @return True if was successful
     */
    private boolean downloadVid() {

        // Extract code of selected format
        JComboBox formatCB = GUI.gui.getFormatCB();
        String formatS = (String) formatCB.getSelectedItem();
        formatS = formatS.substring(0, 6).trim();
        int formatCode = Integer.parseInt(formatS);

        // Create arguments
        String[] extraArgs = new String[2];
        extraArgs[0] = "--format";
        extraArgs[1] = "" + formatCode + "";

        // Get format list for URL
        Command comm = runYTDL(extraArgs);

        // Process output and return status
        return processYTComm(comm, "100%");
    }

    /**
     * Process download request
     */
    public void processDwlReq() {

        // Read local directory file list before download
        String[] dirBefore = getLocalDirFileList();

        // Download video
        boolean success = downloadVid();

        // If downloaded successfully
        if (success) {

            // Move video to downloads folder
            moveToDownloads(dirBefore);
        }

        // Exit if desired
        JCheckBox exitCB;
        exitCB = (JCheckBox) GUI.gui.getComponentByName("exitCheckbox");
        if (exitCB.isSelected()) {
            System.exit(0);
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
     * Get last line of process output
     *
     * @param full
     * @return
     */
    private String getLastProcOutLine(String full) {

        // Split into lines
        String[] outputLines;
        outputLines = full.split("NL:");

        // Get last line
        String lastLine;
        int index = outputLines.length - 1;
        lastLine = outputLines[index];

        // Return line
        return lastLine;
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
     * Report error and exit
     *
     * @param e
     */
    private void reportErr(Exception e) {

        // Print error info and exit
        System.err.print("Err: " + e.toString());
        System.exit(1);
    }
}
