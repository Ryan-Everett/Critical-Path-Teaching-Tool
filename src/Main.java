import javax.swing.*;

/**
 * Main class, simply creates the objects needed for the program to run
 */
class Main {
    /**
     * Main method where Look and feel of GUI, the GUI, the Controller,
     * and the context menu are created
     * @param args      - Array of arguments
     */
    public static void main (String[]args){
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch(Exception e){
            System.out.print("No Layout Manager");
        }
        GUIMain mainGui = new GUIMain();
        ClickContextMenu cCMenu = new ClickContextMenu();
        new Controller(mainGui, cCMenu);
    }
}
