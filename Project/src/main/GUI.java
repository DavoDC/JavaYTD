package main;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

/**
 * Entry class and GUI
 *
 * @author David C
 */
public class GUI extends JFrame {

    // Program details
    public static final String PROGRAM = "JavaYTD";
    public static final double VERSION = 1.0;

    // Main objects
    private static Code code;
    public static GUI gui;

    /**
     * Main method entry point
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        // Set look and feel
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.print(e.toString());
            System.exit(1);
        }

        // Create GUI
        java.awt.EventQueue.invokeLater(() -> {
            try {
                // Initialize GUI
                gui = new GUI();
                gui.setVisible(true);
                gui.setTitle(PROGRAM + " V" + VERSION + " - by David C, 2020");
                Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                int frameXPos = ((int) screen.getWidth() / 2) - (gui.getWidth() / 2);
                gui.setLocation(frameXPos, 39);
                gui.setResizable(false);
                gui.setDefaultCloseOperation(EXIT_ON_CLOSE);

                // Initialize code
                code = new Code();

            } catch (HeadlessException e) {
                System.err.print(e.toString());
                System.exit(1);
            }
        });

    }

    /**
     * Retrieve a component by its name
     *
     * @param nameQuery The wanted component's name
     * @return
     */
    public Component getComponentByName(String nameQuery) {

        // Return variable
        Component comp = null;

        // Get all components
        JRootPane jrp = (JRootPane) gui.getComponents()[0];
        Container cp = (Container) jrp.getContentPane();
        JPanel jp = (JPanel) cp.getComponents()[0];
        Component[] parts = jp.getComponents();

        // Iterate over all parts
        for (Component curComp : parts) {

            // When name matches, save and stop
            if (nameQuery.equalsIgnoreCase(curComp.getName())) {
                comp = curComp;
                break;
            }
        }

        // Return component
        return comp;
    }

    /**
     * Creates GUI
     */
    public GUI() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JMenuBar jMenuBar2 = new javax.swing.JMenuBar();
        javax.swing.JMenu jMenu3 = new javax.swing.JMenu();
        javax.swing.JMenu jMenu4 = new javax.swing.JMenu();
        javax.swing.JMenuItem jMenuItem1 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem jMenuItem2 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem jMenuItem3 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem jMenuItem4 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem jMenuItem6 = new javax.swing.JMenuItem();
        javax.swing.ButtonGroup buttonGroup1 = new javax.swing.ButtonGroup();
        javax.swing.JMenuBar jMenuBar3 = new javax.swing.JMenuBar();
        javax.swing.JMenu jMenu1 = new javax.swing.JMenu();
        javax.swing.JMenu jMenu2 = new javax.swing.JMenu();
        javax.swing.JMenu jMenu5 = new javax.swing.JMenu();
        javax.swing.JMenuBar jMenuBar4 = new javax.swing.JMenuBar();
        javax.swing.JMenu jMenu6 = new javax.swing.JMenu();
        javax.swing.JMenu jMenu7 = new javax.swing.JMenu();
        javax.swing.JMenuBar jMenuBar5 = new javax.swing.JMenuBar();
        javax.swing.JMenu jMenu8 = new javax.swing.JMenu();
        javax.swing.JMenu jMenu9 = new javax.swing.JMenu();
        javax.swing.JMenuBar jMenuBar6 = new javax.swing.JMenuBar();
        javax.swing.JMenu jMenu11 = new javax.swing.JMenu();
        javax.swing.JMenu jMenu12 = new javax.swing.JMenu();
        javax.swing.JMenuBar jMenuBar7 = new javax.swing.JMenuBar();
        javax.swing.JMenu jMenu13 = new javax.swing.JMenu();
        javax.swing.JMenu jMenu14 = new javax.swing.JMenu();
        javax.swing.JMenuItem jMenuItem5 = new javax.swing.JMenuItem();
        javax.swing.JPanel panel = new javax.swing.JPanel();
        javax.swing.JLabel title = new javax.swing.JLabel();
        javax.swing.JButton downloadBut = new javax.swing.JButton();
        javax.swing.JTextField urlField = new javax.swing.JTextField();
        javax.swing.JLabel urlLabel = new javax.swing.JLabel();
        javax.swing.JCheckBox exitCheckbox = new javax.swing.JCheckBox();
        javax.swing.JMenuBar jMenuBar1 = new javax.swing.JMenuBar();
        javax.swing.JMenu menu = new javax.swing.JMenu();

        jMenu3.setText("File");
        jMenuBar2.add(jMenu3);

        jMenu4.setText("Edit");
        jMenuBar2.add(jMenu4);

        jMenuItem1.setText("jMenuItem1");

        jMenuItem2.setText("jMenuItem2");

        jMenuItem3.setText("jMenuItem3");

        jMenuItem4.setText("jMenuItem4");

        jMenuItem6.setText("jMenuItem6");

        jMenu1.setText("File");
        jMenuBar3.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar3.add(jMenu2);

        jMenu5.setText("jMenu5");

        jMenu6.setText("File");
        jMenuBar4.add(jMenu6);

        jMenu7.setText("Edit");
        jMenuBar4.add(jMenu7);

        jMenu8.setText("File");
        jMenuBar5.add(jMenu8);

        jMenu9.setText("Edit");
        jMenuBar5.add(jMenu9);

        jMenu11.setText("File");
        jMenuBar6.add(jMenu11);

        jMenu12.setText("Edit");
        jMenuBar6.add(jMenu12);

        jMenu13.setText("File");
        jMenuBar7.add(jMenu13);

        jMenu14.setText("Edit");
        jMenuBar7.add(jMenu14);

        jMenuItem5.setText("jMenuItem5");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(new java.awt.Dimension(800, 450));

        panel.setBackground(new java.awt.Color(153, 153, 153));
        panel.setForeground(new java.awt.Color(255, 255, 255));
        panel.setName("panel"); // NOI18N
        panel.setPreferredSize(new java.awt.Dimension(800, 450));

        title.setFont(new java.awt.Font("Segoe UI", 3, 48)); // NOI18N
        title.setForeground(new java.awt.Color(0, 0, 0));
        title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        title.setText("JavaYTD");
        title.setName("title"); // NOI18N

        downloadBut.setBackground(new java.awt.Color(255, 51, 51));
        downloadBut.setFont(new java.awt.Font("Segoe UI", 3, 36)); // NOI18N
        downloadBut.setForeground(new java.awt.Color(0, 0, 0));
        downloadBut.setText("Download");
        downloadBut.setToolTipText("");
        downloadBut.setAlignmentY(0.0F);
        downloadBut.setBorder(null);
        downloadBut.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        downloadBut.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        downloadBut.setIconTextGap(0);
        downloadBut.setMargin(new java.awt.Insets(0, 0, 0, 0));
        downloadBut.setName("downloadBut"); // NOI18N
        downloadBut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadButAction(evt);
            }
        });

        urlField.setBackground(new java.awt.Color(255, 255, 255));
        urlField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        urlField.setForeground(new java.awt.Color(0, 0, 0));
        urlField.setName("URLfield"); // NOI18N
        urlField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                urlFieldFocusGained(evt);
            }
        });

        urlLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        urlLabel.setForeground(new java.awt.Color(0, 0, 0));
        urlLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        urlLabel.setText("URL");
        urlLabel.setName("urlLabel"); // NOI18N

        exitCheckbox.setBackground(new java.awt.Color(51, 51, 51));
        exitCheckbox.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        exitCheckbox.setForeground(new java.awt.Color(0, 0, 0));
        exitCheckbox.setSelected(true);
        exitCheckbox.setText("Exit When Done");
        exitCheckbox.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        exitCheckbox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        exitCheckbox.setIconTextGap(8);
        exitCheckbox.setName("exitCheckbox"); // NOI18N

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(downloadBut, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelLayout.createSequentialGroup()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGap(266, 266, 266)
                        .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(exitCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelLayout.createSequentialGroup()
                                .addComponent(urlLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(urlField, javax.swing.GroupLayout.PREFERRED_SIZE, 830, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(urlField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(urlLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 284, Short.MAX_VALUE)
                .addComponent(exitCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(downloadBut, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jMenuBar1.setBorder(null);
        jMenuBar1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        menu.setText("Supported Sites");
        menu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menuClick(evt);
            }
        });
        jMenuBar1.add(menu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, 963, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, 519, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * When download button is pressed, process options
     *
     * @param evt
     */
    private void downloadButAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadButAction

        // Process options
        code.processOptions();
    }//GEN-LAST:event_downloadButAction

    /**
     * When URL field gains focus, extract data from clipboard automatically
     *
     * @param evt
     */
    private void urlFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_urlFieldFocusGained
        try {

            // Get clipboard string
            String data = (String) Toolkit.getDefaultToolkit()
                    .getSystemClipboard().getData(DataFlavor.stringFlavor);

            // Get textfield and change text
            JTextField txtField = (JTextField) evt.getSource();
            txtField.setText(data);
        } catch (UnsupportedFlavorException | IOException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_urlFieldFocusGained

    /**
     * When menu item is clicked, open supported sites page
     *
     * @param evt
     */
    private void menuClick(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuClick

        // Supported sites page
        String sites;
        sites = "https://github.com/ytdl-org/youtube-dl/";
        sites += "blob/master/docs/supportedsites.md";

        // Make command
        String[] args = {sites};
        Command comm = new Command("explorer.exe", args);

        // Run command
        comm.run();
    }//GEN-LAST:event_menuClick


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
