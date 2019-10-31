import javax.swing.*;
import java.awt.*;
import java.util.HashSet;


/**
 * The Class for the Node object
 * ImplementsA the Drawable interface, since it is a drawable object
 * The node object displays to the user their inputted early and late event times
 * It also links Tasks together
 * Each node has a unique index and HashSets of preceding and succeeding tasks
 */
public class Node implements Drawable {
    private final int INDEX;
    private int earlyStartInput, latestStartInput, calculatedEarlyStart, calculatedLatestStart;
    private boolean selected;
    private Vector centre;
    private Drawable nextDraw;
    private Node nextNode;
    private HashSet<Task> succeedingTasks;
    private HashSet<Task> precedingTasks;
    private int selectedArea;
    private boolean checkingAns, displayingAnswers;

    /**
     * Constructor for the node class
     * Sets the centre location and the index of the node
     * Initialises the Task HashSets
     * Sets the node as selected
     * @param index     - The unique identifier for the node
     * @param centre    - The Vector location of the centre of the node
     */
    Node(int index, Vector centre){
        this.INDEX = index;
        this.centre = centre;
        precedingTasks = new HashSet<>();
        succeedingTasks = new HashSet<>();
        this.selected = true;
        checkingAns = false;
    }

    /**
     * Procedure to delete the node
     * Implementation of the abstract function in the Drawable interface
     */
    public void delete(){
        succeedingTasks = null;
        precedingTasks = null;
    }
    /**
     * Procedure to display the node and all its information onto a path component
     * Implementation of the abstract function in the Drawable interface
     *
     * Fills the main circle of the node with the colour of the panel background,
     * This hides the task ends
     * If the user has chosen to check their answers, correct input boxes are filled in green,
     * incorrect input boxes are filled in red
     *
     * Then if the node is selected, the edges for the node and the event time box are filled in
     * with a different shade of red
     * If the node is not selected the edges are coloured black
     * the index is displayed in the middle of the circle
     * It is coloured red if selected, black if not
     * The event times displayed in the boxes are always in the centre of the boxes,
     * this is done by calculating the centre of the text based on how many characters it is#
     * If displaying calculated event times, the event times are written in blue
     * @param g - Graphics object
     */
    public void draw(Graphics g){
        g.setColor(UIManager.getColor ( "Panel.background" ));
        g.fillOval(centre.getIntX() - 20,centre.getIntY() - 20,40,40);
        if (checkingAns){
            if (earlyStartInput == calculatedEarlyStart){
                g.setColor(new Color(23, 135, 5));
            }
            else {
                g.setColor(new Color(165, 5, 15));
            }
            g.fillRect(centre.getIntX() - 30, centre.getIntY() - 50, 30, 25);

            if (latestStartInput == calculatedLatestStart){
                g.setColor(new Color(23, 135, 5));
            }
            else {
                g.setColor(new Color(165, 5, 15));
            }
            g.fillRect(centre.getIntX(), centre.getIntY() - 50, 30, 25);
        }
        else {
            g.fillRect(centre.getIntX() - 30, centre.getIntY() - 50, 30, 25);
            g.fillRect(centre.getIntX(), centre.getIntY() - 50, 30, 25);
        }
        if (selected){
            g.setColor(Color.RED);
        }
        else{
            g.setColor(Color.BLACK);
        }
        g.drawOval(centre.getIntX() - 20,centre.getIntY() - 20,40,40);
        g.drawRect(centre.getIntX() - 30, centre.getIntY() - 50, 30, 25);
        g.drawRect(centre.getIntX(), centre.getIntY() - 50, 30, 25);
        g.drawString(Integer.toString(INDEX), centre.getIntX() - ((Integer.toString(INDEX).length())*g.getFont().getSize()/3), centre.getIntY() + g.getFont().getSize()/3);
        if (checkingAns) {
            g.setColor(Color.BLACK);
        }
        if ((displayingAnswers)){
            g.setColor(Color.BLUE);
            g.drawString(Integer.toString(calculatedEarlyStart), (centre.getIntX() - 15) - ((Integer.toString(calculatedEarlyStart).length())*g.getFont().getSize()/3), (centre.getIntY() - 37) + g.getFont().getSize()/3);
            g.drawString(Integer.toString(calculatedLatestStart), (centre.getIntX() + 15) - ((Integer.toString(calculatedLatestStart).length())*g.getFont().getSize()/3), (centre.getIntY() - 37) + g.getFont().getSize()/3);
        }
        else {
            g.drawString(Integer.toString(earlyStartInput), (centre.getIntX() - 15) - ((Integer.toString(earlyStartInput).length()) * g.getFont().getSize() / 3), (centre.getIntY() - 37) + g.getFont().getSize() / 3);
            g.drawString(Integer.toString(latestStartInput), (centre.getIntX() + 15) - ((Integer.toString(latestStartInput).length()) * g.getFont().getSize() / 3), (centre.getIntY() - 37) + g.getFont().getSize() / 3);
        }

    }

    /**
     * Function to check if a click location is within the node
     * Implementation of the abstract function in the Drawable interface
     *
     * Uses vector mathematics to find the vector from the centre of the node to the click
     * Checks if the magnitude of this vector is shorter or equal to the length of the radius
     * If this is true then the click is within the circle, so returns true and sets selected area to the main circle
     * Else, checks if the click is within one of the event time boxes
     * Returns true and sets the selected area to the corresponding box if this is true
     * Else returns false
     * @param v     - Click Location
     * @return      - If the node is inside the vector
     */
    public boolean checkIfInside(Vector v){
        Vector cToT = v.subtract(centre);
        if (cToT.getMagnitude()<= 20){  //If vector from centre to mouse click has magnitude <= 20
            selectedArea = 1;
            return true;
        }
        else if ((v.getIntX() >= centre.getIntX() - 30) && (v.getIntX() <= centre.getIntX() + 30)){
            if ((v.getIntY() >= centre.getIntY() - 50 )  && (v.getIntY() <= centre.getIntY() - 25)){
                if (v.getIntX() >= centre.getIntX()){
                    selectedArea = 3;            //Clicked on latest Start area
                    return true;
                }
                selectedArea = 2;                //Clicked on earliest start area
                return true;
            }
        }
        return false;           //Not clicked on any node area
    }

    /**
     * Procedure to move a node to a colliding node at the closest point to the mouse
     * Finds the joining unit vector between the click and colliding node
     * Multiplies this vector by the diameter to find the new node centre location which would place the node at the edge of the colliding node
     * @param v     - Mouse location
     * @param collidingNode     - Node which the mouse is inside
     */
    Vector moveToNode(Vector v,Node collidingNode){
        Vector v2 = collidingNode.getCentre();
        Vector vToV2 = v2.subtract(v);
        vToV2 = (vToV2.getUnitVector()).multiply(40);
        return v2.subtract(vToV2);
    }

    /**
     * Procedure to set whether or not the node is displaying the calculated answers
     * @param b     - new value for displayingAnswers
     */
    public void setDisplayingAnswers(boolean b){
        if (b) {
            checkingAns = false;
        }
        displayingAnswers = b;
    }
    /**
     * Function to get the node index
     * @return      - Node index
     */
    int getINDEX() {
        return INDEX;
    }

    /**
     * Function to get the Drawable in the DrawableList which follows this node
     * Implementation of the abstract function in the Drawable interface
     * @return      - Next Drawable
     */
    public Drawable getNextDrawable(){
        return nextDraw;
    }

    /**
     * Function to get the next node in the NodeList
     * @return      - Next Node
     */
    Node getNextNode() {
        return nextNode;
    }

    /**
     * Procedure to set a new following Drawable in the DrawableList
     * Implementation of the abstract procedure in the Drawable interface
     * @param nextDraw      - Next Drawable
     */
    public void setNextDrawable(Drawable nextDraw) {
        this.nextDraw = nextDraw;
    }

    /**
     * Procedure to set a new following node in the NodeList
     * @param nextNode      - Next Node
     */
    void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    /**
     * Procedure to add a Task to the HashSet of preceding tasks
     * @param t     - New Task
     */
    void addToPreceding (Task t){
        precedingTasks.add(t);
    }

    /**
     * Function to get the Preceding task HashSet
     * @return      - Preceding task HashSet
     */
    HashSet<Task> getPrecedingTasks() {
        return precedingTasks;
    }

    /**
     * Procedure to add a Task to the HashSet of succeeding tasks
     * @param t     - New Task
     */
    void addToSucceeding(Task t){
        succeedingTasks.add(t);
    }

    /**
     * Function to get the Succeeding task HashSet
     * @return      - Succeeding task HashSet
     */
    HashSet<Task> getSucceedingTasks() {
        return succeedingTasks;
    }

    /**
     * Function to get the Vector location of the centre of the node
     * @return      - Centre location vector
     */
    Vector getCentre() {
        return centre;
    }

    /**
     * Function to set the Vector location of the centre of the node
     * @param v     - new location for centre
     */
    void setCentre(Vector v){
        centre = v;
    }

    /**
     * Function to get the priority of the object for the DrawableList
     * Implementation of the abstract function in the Drawable interface
     * @return  - The value 2, as 2 is always the priority of Nodes
     */
    public int getObjectPriority(){
        return 2;
    }

    /**
     * Function to compare the priority of a Node with the priority of another Drawable
     * Implementation of the abstract function in the Drawable interface
     * @param d     - Other Drawable
     * @return      - If the node priority is greater or equal
     */
    public boolean priorityGreaterThanOrEqual(Drawable d){
        return 2 > d.getObjectPriority();
    }

    /**
     * Procedure to set the node as selected or unselected
     * @param selected      - New value for selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        if (!selected){
            selectedArea = -1;      //No area selected
        }
    }

    /**
     * Returns integer representing amount of correct inputs
     * @return      - Correct input amount (0 - 2)
     */
    int getMarksForNode(){
        int marks = 0;
        if (calculatedEarlyStart == earlyStartInput){
            marks++;
        }
        if (calculatedLatestStart == latestStartInput){
            marks++;
        }
        return marks;
    }

    /**
     * Function to get which area of the node is selected
     * @return      - The selected area (-1 if node is not selected)
     */
    int getSelectedArea(){
        return selectedArea;
    }

    /**
     * Procedure to set the input value for the early start of the node
     * @param earlyStartInput       - The input
     */
    void setEarlyStartInput(int earlyStartInput) {
        this.earlyStartInput = earlyStartInput;
    }

    /**
     * Procedure to set the input value for the latest start time of the node
     * @param latestStartInput       - The input
     */
    void setLatestStartInput(int latestStartInput) {
        this.latestStartInput = latestStartInput;
    }

    /**
     * Function to get the calculated value for the early start time of the node
     * @return      - The calculated value
     */
    int getCalculatedEarlyStart() {
        return calculatedEarlyStart;
    }

    /**
     * Procedure to set the calculated value for the early start time of the node
     * @param calculatedEarlyStart       - The calculated value
     */
    void setCalculatedEarlyStart(int calculatedEarlyStart){
        this.calculatedEarlyStart = calculatedEarlyStart;
    }

    /**
     * Function to get the calculated value for the latest start time of the node
     * @return      - The calculated value
     */
    int getCalculatedLatestStart() {
        return calculatedLatestStart;
    }

    /**
     * Procedure to set the calculated value for the latest start time of the node
     * @param calculatedLatestStart       - The calculated value
     */
    void setCalculatedLatestStart(int calculatedLatestStart){
        this.calculatedLatestStart = calculatedLatestStart;
    }

    /**
     * Function to see if the node is displaying feedback on inputs
     * @return      - If the answers have been checked
     */
    boolean isCheckingAns(){
        return checkingAns;
    }

    /**
     * Returns true if the node is connected to a network
     * @return      - If connected
     */
    boolean isConnected(){
        return !(succeedingTasks.isEmpty() && precedingTasks.isEmpty());
    }

    /**
     * Procedure to set the node to check answers or to not check answers
     * @param checkingAns       - Boolean to say whether to check answers
     */
    void setCheckingAns(boolean checkingAns) {
        this.checkingAns = checkingAns;
    }
}