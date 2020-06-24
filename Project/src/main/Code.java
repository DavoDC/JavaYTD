package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

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

        // If youtube-dl program does not exist
        File progExe = new File(progPath);
        if (!progExe.exists()) {

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

                // Print error info and exit
                System.err.print(e.toString());
                System.exit(1);
            }
        }
    }

    /**
     * Process input from GUI
     */
    public void processOptions() {

        // Get and disable download button
        JButton dwlBut;
        dwlBut = (JButton) GUI.gui.getComponentByName("downloadBut");
        dwlBut.setEnabled(false);

        // Retrieve URL
        JTextField urlField;
        urlField = (JTextField) GUI.gui.getComponentByName("urlField");
        String url = urlField.getText();

        // Make command
        String[] args = {url};
        Command comm = new Command("youtube-dl.exe", args);

        // Run command
        comm.run();

        // Re-enable download button
        dwlBut.setEnabled(true);

        // Exit if desired
        JCheckBox exitCB;
        exitCB = (JCheckBox) GUI.gui.getComponentByName("exitCheckbox");
        if (exitCB.isSelected()) {
            System.exit(0);
        }
    }
}
