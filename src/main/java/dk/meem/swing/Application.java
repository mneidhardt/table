package dk.meem.swing;

import javax.swing.JFrame;

public class Application {
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("PasswordManager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        PasswordManager newContentPane = new PasswordManager();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

		frame.setJMenuBar(newContentPane.getMenuBar());
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
