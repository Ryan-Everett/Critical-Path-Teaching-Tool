import javax.swing.*;
import java.awt.*;
/**
 * GUI object, which extends JFrame
 * Holds the Component for the Nodes and Tasks, and all the options
 * Class is initialised by the Main class
 */
class GUIMain extends JFrame {
    private final JButton CHECK_BUTTON = new JButton("Check Answers");
    private final JButton CHECK_VALID_BUTTON = new JButton("Check Validity Of Network");
    private final JButton CLEAR_BUTTON = new JButton("Clear all");
    private final PathComponent P_COMPONENT = new PathComponent();
    private final JLabel CRIT_PATH_LENGTH;
    private final JLabel MARK_LABEL;

    /**
     * Constructor for class
     * Configures the frame
     */
    GUIMain(){
        P_COMPONENT.setPreferredSize(new Dimension(700, 500));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel bottomPanel = new JPanel();
        JPanel topPanel = new JPanel();
        CRIT_PATH_LENGTH = new JLabel();
        MARK_LABEL = new JLabel();
        bottomPanel.add(CLEAR_BUTTON);
        bottomPanel.add(CHECK_VALID_BUTTON);
        bottomPanel.add(CHECK_BUTTON);
        CRIT_PATH_LENGTH.setAlignmentX(Component.RIGHT_ALIGNMENT);
        bottomPanel.add(CRIT_PATH_LENGTH);
        topPanel.add(MARK_LABEL);
        this.setTitle("Critical Path Algorithm");
        this.getContentPane().add(topPanel, BorderLayout.NORTH);
        this.getContentPane().add(P_COMPONENT, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        this.pack();
        this.setVisible(true);
        this.setFocusable(true);
        this.requestFocus();
    }

    /**
     * Function to initiate the the drawing of a task
     * Checks if location is valid
     * Starts drawing the line
     * @param v - The location of the start of the line
     * @return  - The start node
     */
    Node startDrawing(Vector v){
        Node startNode = P_COMPONENT.validTaskStart(v);
        if (!(startNode == null)){
            this.P_COMPONENT.drawLine(startNode.getCentre());
            return startNode;
        }
        System.out.println("Invalid start");
        return null;
    }

    /**
     * Procedure to process a double click
     * First checks if a Drawable is selected
     * If a TaskVisualised is selected, then opens a dialogue box to set duration
     * Else if a Node is selected, then the selected zone of the node is checked,
     * allowing the correct dialogue box to open to change the correct event time
     * When a dialogue box receives an input, the input is checked and set to the corresponding variable if valid
     * If no node is selected then no dialogue box is opened
     */
    void doubleClicked (){
        String input = "null";
        Drawable selectedDrawable = P_COMPONENT.getSelected();
        if (selectedDrawable != null) {
            try {
                if (selectedDrawable.getObjectPriority() == 1) {
                    TaskVisualised tVis = (TaskVisualised) selectedDrawable; //Safe to cast because selectedDrawable is proven to be a taskVisualised
                    input = JOptionPane.showInputDialog("New duration for task: ");
                    if ((input != null)&&(Integer.parseInt(input)>= 0)) {
                        tVis.setDuration(Integer.parseInt(input));
                    }
                } else if (selectedDrawable.getObjectPriority() == 2) {
                    Node n = (Node) selectedDrawable;
                    if (n.getSelectedArea() == 2) {
                        input = JOptionPane.showInputDialog("Enter early event time: ");
                        if ((input != null)&&(Integer.parseInt(input)>= 0)) {
                            n.setEarlyStartInput(Integer.parseInt(input));
                        }
                    } else if (n.getSelectedArea() == 3) {
                        input = JOptionPane.showInputDialog("Enter late event time: ");
                        if ((input != null)&&(Integer.parseInt(input)>= 0)) {
                            n.setLatestStartInput(Integer.parseInt(input));
                        }

                    }

                }
                if (Integer.parseInt(input)>= 0) {          //Output on console whether input is valid or not (Testing)
                    System.out.println(input + " is valid");
                }
                else {
                    System.out.println(input + " is invalid (Inputs cannot be smaller than 0)");
                }
            }
            catch (RuntimeException e){
                try {
                    if (!input.equals("null")) {
                        System.out.println(input + " is an invalid input");
                    }
                }
                catch (NullPointerException np){
                    //Empty invalid, no further action needed
                }
            }
        }
        P_COMPONENT.repaint();
    }

    /**
     * Function to get the Paint Component
     * @return  - PaintComponent
     */
    PathComponent getPComponent() {
        return P_COMPONENT;
    }

    /**
     * Function to get the clear button
     * @return  - Clear button
     */
    JButton getCLEAR_BUTTON(){
        return CLEAR_BUTTON;
    }
    /**
     * Function to get the check answer button
     * @return  - Check answer button
     */
    JButton getCHECK_BUTTON() {
        return CHECK_BUTTON;
    }

    /**
     * Function to get the check validity of network button
     * @return  - Check valid button
     */
    JButton getCHECK_VALID_BUTTON() {
        return CHECK_VALID_BUTTON;
    }
    /**
     * Procedure to set the length of the critical path, which is displayed on the bottom panel
     * @param critPathLength    - The new length to be displayed
     */
    void setCRIT_PATH_LENGTH(String critPathLength) {
        this.CRIT_PATH_LENGTH.setText(critPathLength);
    }

    /**
     * Sets the GUI to display the total amount of marks achieved
     * @param marks     -  Array of marks for respective sections
     */
    void setMarkAmount(int[] marks){
        MARK_LABEL.setText(Integer.toString(marks[0]) + " / 4 For nodes, " + Integer.toString(marks[1]) + " /2 For Tasks, " + Integer.toString(marks[0] + marks[1]) + " /6 Total");
    }

    /**
     * Procedure to clear te text on the mark label
     */
    void clearMarkLabel(){
        MARK_LABEL.setText("");
    }
}
