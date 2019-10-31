import java.awt.*;

import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;

/**
 * Class for the TaskVisualised object
 * Implements the Drawable interface, since it is a drawable object
 * Each TaskVisualised represents a task, and is painted into the PathComponent
 * Painted as a line with a number to represent its duration, and an arrow to show its direction
 * (In comments, 'the Task' refers to the Task which this object represents)
 */
public class TaskVisualised implements Drawable{
    private int duration;
    private final Node START_NODE, END_NODE;
    private Drawable nextDraw;
    private boolean selected, critSelected, displayingAnswers;

    /**
     * Constructor for TaskVisualised object
     * Defined in terms of a start and end node to help with linking the network
     * Duration of task is set to 1 as default
     * The TaskVisualised is set as selected
     * @param nStart        - Node where the Task begins
     * @param nFinish       - Node where the Task finishes
     */
    TaskVisualised (Node nStart, Node nFinish){
        this.START_NODE = nStart;
        this.END_NODE = nFinish;
        this.duration = 1;
        this.selected = true;
        displayingAnswers = false;
        nextDraw = null;
    }

    /**
     * Procedure to delete the TaskVisualised
     * Sets its duration to -1, marking it as deleted
     * Implementation of the abstract function in the Drawable interface
     */
    public void delete(){
        duration = -1;
    }

    /**
     * Procedure to draw the TaskVisualised onto a component
     * Implementation of the abstract procedure in the Drawable interface
     * The colour of the line and duration depends on certain conditions:
     * If the task is bright red, it is selected
     * If the task is dark red, it has either been marked as critical when it shouldn't have,
     * or not been marked as critical when it should have been. In both scenarios the user must have selected to check answers
     * If the task is blue, it has been marked as critical,
     * if the user has chosen to check answers, a blue task shows a task which has been correctly marked as critical
     * A dummy task (task with zero duration), will be drawn as a dashed line
     * @param g     - Graphics object
     */
    public void draw (Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        Stroke defaultStroke = g2d.getStroke();
        if (selected){
            g.setColor(Color.RED);
        }
        else if (duration == 0){
            g.setColor(BLACK);
        }
        else if (displayingAnswers){
            if (((END_NODE.getCalculatedLatestStart() - START_NODE.getCalculatedEarlyStart()) - duration) == 0){
                g.setColor(BLUE);
            }
            else{
                g.setColor(BLACK);
            }
        }
        else if (critSelected){
            g.setColor(BLUE);
            if (END_NODE.isCheckingAns()){
                if (((END_NODE.getCalculatedLatestStart() - START_NODE.getCalculatedEarlyStart()) - duration) != 0) {     //Not critical but marked as critical
                    g.setColor(new Color(165, 5, 15));
                }
            }
        }
        else if (END_NODE.isCheckingAns()) {
            if (((END_NODE.getCalculatedLatestStart() - START_NODE.getCalculatedEarlyStart()) - duration) == 0) {         //Critical but not marked as critical
                g.setColor(new Color(165, 5, 15));
            } else {
                g.setColor(Color.BLACK);
            }
        }
        else{
            g.setColor(Color.BLACK);
        }
        g2d.draw(START_NODE.getCentre().getIntersectingLine(END_NODE.getCentre(), 0.7, 20, 30));          //Draws arrow
        g2d.draw(START_NODE.getCentre().getIntersectingLine(END_NODE.getCentre(), 0.7, 20, -30));
        Vector textLocation = START_NODE.getCentre().getPerpendicularLineEnd(END_NODE.getCentre(), 0.5, 5);
        if (duration == 0){     //Task is a dummy
            Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
            g2d.setStroke(dashed);
        }
        else{
            g2d.drawString("" + this.duration, textLocation.getIntX(), textLocation.getIntY());
        }
        g2d.drawLine((START_NODE.getCentre()).getIntX(), (START_NODE.getCentre()).getIntY(), (END_NODE.getCentre()).getIntX(), (END_NODE.getCentre()).getIntY());
        g2d.setStroke(defaultStroke);
    }

    /**
     * Function to check if a vector is close enough to a task to be considered 'colliding'
     * Implementation of the abstract function in the Drawable interface
     * @param v     - Vector to be compared
     * @return      - If the vector is colliding
     */
    public boolean checkIfInside(Vector v){
        return v.perpendicularDistanceToLineSeg(this.START_NODE.getCentre(), this.END_NODE.getCentre()) < 6;
    }

    /**
     * Function to get the Drawable in the DrawableList which follows this TaskVisualised
     * Implementation of the abstract function in the Drawable interface
     * @return      - Next drawable
     */
    public Drawable getNextDrawable() {
        return nextDraw;
    }

    /**
     * Procedure to set a Drawable to follow this TaskVisualised in the DrawableList
     * Implementation of the abstract procedure in the Drawable interface
     * @param nextDraw      - New Drawable to be set as next
     */
    public void setNextDrawable(Drawable nextDraw) {
        this.nextDraw = nextDraw;
    }

    /**
     * Procedure to get the duration of the Task
     * @return      - Duration
     */
    int getDuration() {
        return duration;
    }

    /**
     * Function to get the node which is at the start of the TaskVisualised
     * @return      - The starting node
     */
    Node getSTART_NODE(){
        return START_NODE;
    }

    /**
     * Function to get the node which is at the end of the TaskVisualised
     * @return      - The finishing node
     */
    Node getEND_NODE(){
        return END_NODE;
    }

    /**
     * Procedure to set whether the TaskVisualised is selected
     * @param selected      - New value for selected
     */
    public void setSelected (boolean selected){
        this.selected = selected;
    }

    /**
     * Procedure to set the duration of the task
     * Verifies if new duration is valid (Non negative) before setting duration
     * @param d     - New value for duration
     */
    void setDuration (int d){
        if (duration >= 0) {
            this.duration = d;
        }
        else{
            System.out.print("Invalid time");                         //Change to something on gui
        }
    }

    /**
     * Procedure to set whether or not the node is displaying the calculated answers
     * @param b     - new value for displayingAnswers
     */
    public void setDisplayingAnswers (boolean b){
        displayingAnswers = b;
    }

    /**
     * Returns 1 if a task is marked correctly
     * 0 Otherwise
     * @return      - Amount of marks
     */
    int checkMark(){
        if (critSelected && (((END_NODE.getCalculatedLatestStart() - START_NODE.getCalculatedEarlyStart()) - duration) == 0)){
            return 1;
        }
        else if (((END_NODE.getCalculatedLatestStart() - START_NODE.getCalculatedEarlyStart()) - duration) != 0){
            return 1;
        }
        return 0;
    }

    /**
     * Procedure to toggle whether the Task is recognised as critical or not
     */
    void toggleCritSelected(){
        this.critSelected = !critSelected;
    }

    /**
     * Function to get the priority of the object for the DrawableList
     * Implementation of the abstract function in the Drawable interface
     * @return  - The value 1, as 1 is always the priority of any TaskVisualised
     */
    public int getObjectPriority(){
        return 1;
    }
    /**
     * Function to compare the priority of a Node with the priority of another Drawable
     * Implementation of the abstract function in the Drawable interface
     * @param d     - Other Drawable
     * @return      - If the node priority is greater or equal
     */
    public boolean priorityGreaterThanOrEqual(Drawable d){
        return 1 > d.getObjectPriority();
    }
}