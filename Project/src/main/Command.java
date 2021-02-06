package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Models a command prompt command
 *
 * @author David
 */
public class Command {

    // Command list
    private final ArrayList<String> cmdList;

    // Output
    private String normOutput;
    private String errOutput;

    /**
     * Initialize command with program name and arg array
     *
     * @param progName Program
     * @param args Arguments
     */
    public Command(String progName, String[] args) {

        // Add program name and space to command 
        String commS = progName + " ";

        // Add arguments to command
        for (String curArg : args) {
            commS += curArg + " ";
        }

        // Create command list
        cmdList = new ArrayList<>();
        cmdList.add("cmd");
        cmdList.add("/c");
        cmdList.add(commS);
    }

    /**
     * Initialize command with program name and arg list (wrapper constructor)
     *
     * @param progName Program
     * @param argList
     */
    public Command(String progName, ArrayList<String> argList) {

        this(progName, argList.toArray(new String[0]));
    }

    /**
     * Run command
     */
    public void run() {
        try {

            // Run command
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(cmdList);
            Process p = pb.start();

            // Extract output
            normOutput = getStringFromStream(p.getInputStream());
            errOutput = getStringFromStream(p.getErrorStream());

            // Always print full output (console only)
            printOutput();

        } catch (IOException e) {

            // Print error info
            Code.outputerr(e);
        }
    }

    /**
     * Extract a string from an input stream
     *
     * @param is
     * @return
     * @throws IOException
     */
    private String getStringFromStream(InputStream is) throws IOException {

        // Get reader
        InputStreamReader isr;
        isr = new InputStreamReader(is);
        BufferedReader reader;
        reader = new BufferedReader(isr);

        // Holder
        String output = "";

        // Extract sting
        String curLine;
        while ((curLine = reader.readLine()) != null) {
            output += " \nNL:" + curLine;
        }

        // If line is very short
        if (output.length() < 3) {

            // Notify
            output = "Empty!";
        }

        // Return output;
        return output;
    }

    /**
     * Print both normal and error output
     */
    public void printOutput() {
        System.out.println();
        System.out.println("Output: " + normOutput);
        System.out.println("Errors: " + errOutput);
        System.out.println();
    }

    /**
     * Get string representation of command (as it would be manually typed)
     */
    @Override
    public String toString() {
        return "\nCommand: \n" + cmdList.get(2);
    }

    /**
     * Return output of last run
     *
     * @return
     */
    public String getOutput() {
        return normOutput;
    }

    /**
     * Return error output of last run
     *
     * @return
     */
    public String getErrOutput() {
        return errOutput;
    }
}
