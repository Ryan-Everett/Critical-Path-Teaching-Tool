import javax.swing.*;
import java.awt.*;

/**
 * Class for the PathComponent object
 * This object is a custom child class of JComponent
 * It is considered as the main component of GUIMain
 */
class PathComponent extends JComponent {
    private boolean drawing;
    private int x1, x2, y1, y2;
    private int currentIndex = 0;
    private final DrawableList D_LIST;
    private final NodeList N_LIST;
    private Node currentStartNode;
    private Drawable selectedDrawable;

    /**
     * Constructor for object
     * Initialises the custom linked lists
     * Sets up custom key bindings for holding control and pressing delete
     */
    PathComponent() {
        setFocusable(true);
        this.drawing = false;
        this.D_LIST = new DrawableList();
        this.N_LIST = new NodeList();
    }

    /**
     * Override of the JComponent's paintComponent method
     * Simply draws the object by calling the drawObjects procedure
     *
     * @param g - Graphics object
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawObjects(g);
    }

    /**
     * Procedure to draw every Drawable onto the component
     * Uses the DrawableList's 'drawAll' procedure to draw the objects
     * If a new task is being drawn, draw the task from it's start node to the mouse location
     * Draws the arrow onto the task to show its direction
     * Draws the selection box if multiple things are being selected
     *
     * @param g - The Graphics object passed in
     */
    private void drawObjects(Graphics g) {
        if (drawing) {
            g.setColor(new Color(0, 0, 0));
            g.drawLine(x1, y1, x2, y2);
            Graphics2D g2d = (Graphics2D) g;
            g2d.draw(new Vector(x1, y1).getIntersectingLine(new Vector(x2, y2), 0.7, 20, 30));      //Draws arrow
            g2d.draw(new Vector(x1, y1).getIntersectingLine(new Vector(x2, y2), 0.7, 20, -30));
        }
        D_LIST.drawAll(g);
    }

    /**
     * Function to check whether a potential task start location is valid
     * If a click to make a task lies within a node it is considered valid
     * The NodeList returns which node contains the vector of the click
     * If a node is returned then the task's start is set to the found node and this node is returned
     * Otherwise returns null, as location is not a valid task task
     *
     * @param v - Click Vector
     * @return - Which node contains the vector
     */
    Node validTaskStart(Vector v) {
        Node n = N_LIST.whichNodeContains(v);
        if (!(n == null)) {
            System.out.println("Valid start");
            this.currentStartNode = n;
            return n;
        }
        return null;
    }

    /**
     * Function to change the selected Drawable to a new Drawable
     * Un-selects the currently selected drawable
     * Selects whatever drawable is clicked on
     *
     * @param v - Location to be tested
     */
    void updateSelected(Vector v) {
        if (selectedDrawable != null) {
            selectedDrawable.setSelected(false);
            selectedDrawable = null;
        }
        if (D_LIST.getFirst() != null) {
            selectedDrawable = D_LIST.whichDrawableContains(v);
        }
        if (selectedDrawable != null) {
            selectedDrawable.setSelected(true);
        }
        repaint();
    }

    /**
     * Procedure to begin the drawing of a line
     * Sets start co-ordinates of the line which is being drawn to the start of the new line
     * Sets the end co-ordinates to the start co-ordinates, to give a line of zero length
     *
     * @param v - Start of new line
     */
    void drawLine(Vector v) {
        this.drawing = true;
        x1 = v.getIntX();
        x2 = v.getIntX();
        y1 = v.getIntY();
        y2 = v.getIntY();
    }

    /**
     * Procedure to drag the selected node across the component
     * If a line is being drawn, changes its end co0ordinates to the vector location it is being dragged to
     * Else, if the selected Drawable is a node, the new position is checked to ensure a node isn't being dragged into another node,
     * or through the edge of the component
     * If it is not, then the selected nodes location is set to the new location
     * The component is then repainted
     *
     * @param v - The location to drag the drawable to
     */
    void dragSelected(Vector v) {
        Dimension componentSize = getSize();
        if (drawing) {
            x2 = v.getIntX();
            y2 = v.getIntY();
        } else {
            if (v.getIntX() >= componentSize.getWidth() - 31) {
                v.setX(componentSize.getWidth() - 31);
            }
            if (v.getIntY() >= componentSize.getHeight() - 21) {
                v.setY(componentSize.getHeight() - 21);
            }

            if (v.getIntX() <= 30) {
                v.setX(30);
            }
            if (v.getIntY() <= 50) {
                v.setY(50);
            }
            if (!(selectedDrawable == null)) {
                if (selectedDrawable.getObjectPriority() == 2) {
                    Node selected = (Node) selectedDrawable; //Safe to cast because selectedDrawable is proven to be a node
                    Node collidingNode = N_LIST.circleIntersectCheck(v, selected.getINDEX());
                    if (collidingNode == null) {
                        selected.setCentre(v);
                    } else {
                        Vector potentialCentre = selected.moveToNode(v, collidingNode); //Tries to make new centre which doesn't intersect node
                        if (N_LIST.circleIntersectCheck(potentialCentre, selected.getINDEX()) == null) {
                            if ((potentialCentre.getIntX() < componentSize.getWidth() - 31) && (potentialCentre.getIntX() > 30)) {   //Boundary checks
                                if ((potentialCentre.getIntY() < componentSize.getWidth() - 21) && (potentialCentre.getIntY() > 50)) {
                                    selected.setCentre(potentialCentre);
                                }
                            }
                        }
                    }
                }
            }
        }
        repaint();
    }

    /**
     * Function to finish the drawing of a task
     * Sets the finish node to the node which the mouse is intersecting
     * If the finish node is not valid, tries to make a new Node for the task to finish at
     * Else, creates a TaskVisualised to represent the Task
     * Inserts the task into the DrawableList
     * Creates a new Task using this task visualised
     * Adds the task into the HashSets for the nodes it starts and ends at
     * Sets the TaskVisualised to selected
     * Repaints the component and returns the created task
     *
     * @param v - The vector for the end of the task
     * @return - The new task
     */
    Task finishDrawing(Vector v) {
        Node nFinish = N_LIST.whichNodeContains(v);
        drawing = false;
        if (nFinish == null) {
            if (N_LIST.circleIntersectCheck(v, -1) == null) { //Exclude no nodes
                nFinish = new Node(currentIndex, v);
                D_LIST.insert(nFinish);
                N_LIST.insert(nFinish);
                currentIndex++;
                nFinish.setSelected(false);
            } else {
                System.out.println("Invalid end");
                repaint();
                return null;
            }
        }
        if (currentStartNode == nFinish) {
            System.out.println("Task cannot start and end at the same node");
            repaint();
            return null;
        }
        TaskVisualised tVis = new TaskVisualised(currentStartNode, nFinish);
        if (D_LIST.containsTask(tVis)) {
            System.out.println("Task already found between these nodes");
            repaint();
            return null;
        }
        System.out.println("Valid end");
        D_LIST.insert(tVis);
        System.out.println("Inserted");
        Task t = new Task(tVis);
        tVis.getSTART_NODE().addToSucceeding(t);
        tVis.getEND_NODE().addToPreceding(t);
        if (selectedDrawable != null) {
            selectedDrawable.setSelected(false);
            selectedDrawable = null;
        }
        selectedDrawable = tVis;
        repaint();
        return t;
    }

    /**
     * Procedure to process a click whilst the program is in 'node' mode
     * <p>
     * Creates a node if nothing is selected
     *
     * @param clickPos - The vector location of the click
     */
    void nodeModeClick(Vector clickPos) {
        Dimension componentSize = getSize();
        if (clickPos.getIntX() >= componentSize.getWidth() - 31) {
            clickPos.setX(componentSize.getWidth() - 31);
        }
        if (clickPos.getIntY() >= componentSize.getHeight() - 21) {
            clickPos.setY(componentSize.getHeight() - 21);
        }

        if (clickPos.getIntX() <= 30) {
            clickPos.setX(30);
        }
        if (clickPos.getIntY() <= 50) {
            clickPos.setY(50);
        }
        updateSelected(clickPos);

        if (selectedDrawable == null) {
            if (N_LIST.circleIntersectCheck(clickPos, -1) == null) { //Don't exclude any Nodes from search
                Node n = new Node(currentIndex, clickPos);
                D_LIST.insert(n);
                N_LIST.insert(n);
                currentIndex++;
                selectedDrawable = n;
                repaint();
            } else {
                System.out.println("Potential node location intersects node");
            }
        }
    }

    /**
     * Function to get the selected Drawable
     *
     * @return - Selected Drawable
     */
    Drawable getSelected() {
        return selectedDrawable;
    }

    /**
     * Procedure to delete the selected Drawable from the network
     * Removes the Drawable from the DrawableList
     * If the Drawable is a node, it is removed from the NodeList, and the directly connected Tasks are also removed
     */
    void deleteSelected() {
        if (selectedDrawable != null) {
            if (selectedDrawable.getObjectPriority() == 2) {
                N_LIST.remove((Node) selectedDrawable);
                D_LIST.deleteConnected((Node) selectedDrawable);
            }
            D_LIST.remove(selectedDrawable);
            selectedDrawable.delete();
        }
        System.out.println("Selected Deleted");
        repaint();
    }

    /**
     * Procedure to set the selected drawable to null
     */
    void selectNull() {
        if (selectedDrawable != null) {
            selectedDrawable.setSelected(false);
            repaint();
        }
    }

    /**
     * Procedure to process a ctrlClick
     * If a TaskVisualised is selected, it is toggled to be identified as critical or not critical
     *
     * @param v - The vector location of the mouse click
     */
    void ctrlClick(Vector v) {
        if (selectedDrawable != null) {
            selectedDrawable.setSelected(false);
        }
        if (D_LIST.getFirst() != null) {
            selectedDrawable = D_LIST.whichDrawableContains(v);
            if (selectedDrawable != null) {
                if (selectedDrawable.getObjectPriority() == 1) {    //Safe to cast because selectedDrawable is proven to be a TaskVisualised
                    TaskVisualised tVis = (TaskVisualised) selectedDrawable;
                    tVis.toggleCritSelected();
                }
            }
        }
        repaint();

    }

    /**
     * Procedure to reset the Component
     */
    void clearComponent() {
        D_LIST.removeAll();
        N_LIST.removeAll();
        currentIndex = 0;
        repaint();
    }

    /**
     * Function to get the NodeList
     *
     * @return - The NodeList
     */
    NodeList getN_LIST() {
        return N_LIST;
    }

    /**
     * Function to get the DrawableList
     *
     * @return - The DrawableList
     */
    DrawableList getD_LIST() {
        return D_LIST;
    }
}