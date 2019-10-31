import java.awt.*;

/**
 * Linked List class, of Drawable objects
 * Used to draw the objects into the PathComponent
 * Sorted using a priority system to hold every TaskVisualised first
 */
class DrawableList {
    private Drawable first = null;

    /**
     * Constructor of list for 0 items
     */
    DrawableList() {}

    /**
     * Checks if list is empty
     * @return  - If empty return true
     */
    private boolean isEmpty(){
        return  this.first == null;
    }

    /**
     * Insert procedure. When a Drawable is created, it needs to be added to the list
     * This procedure is called to complete this task
     * The method iterates through the list until it finds the correct priority of item
     * Once the correct priority is reached, the new drawable is inserted at this priority level
     * The inserted location will always either be the first location in the list or the last
     * The next drawable for the items around the inserted drawable are changed accordingly
     * @param drawable  - Item to be inserted
     */
    void insert (Drawable drawable){
        Drawable current = this.first;
        Drawable previous = this.first;
        if (this.isEmpty()) this.first = drawable;
        else if (this.first.priorityGreaterThanOrEqual(drawable))
        {
            drawable.setNextDrawable(this.first);
            this.first = drawable;
        }
        else
        {
            while (current !=null && !(current.priorityGreaterThanOrEqual(drawable)))
            {
                previous = current;
                current = current.getNextDrawable();
            }
            drawable.setNextDrawable(current);
            previous.setNextDrawable(drawable);
        }
    }

    /**
     * Iterates through the loop to find a given Drawable
     * Once found, the drawable which follows this item is set to follow the previous drawable instead
     * This process removes the given drawable from the list
     * @param drawable      - The drawable to be removed
     */
    void remove (Drawable drawable){
        if (!this.isEmpty()){
            Drawable current = this.first;
            Drawable previous = this.first;
            if (drawable == current){

                this.first = current.getNextDrawable();
            }
            else {
                while (current != null) {
                    if (drawable == current) {
                        previous.setNextDrawable(current.getNextDrawable());
                        current = null;
                    } else {
                        previous = current;
                        current = current.getNextDrawable();
                    }
                }
            }
        }
    }

    /**
     * Removed every item in the list
     * By removing the first item, all other items are removed
     */
    void removeAll(){
        first = null;
    }

    /**
     * Function to check whether any TaskVisualised has either the same start and end nodes as the inputted TaskVisualised,
     * or if it's end node is the same as another's start, and it's start the same as that tasks end
     * @param tVis      - The task to be found
     * @return          - True if a task matches, false if not
     */
    boolean containsTask (TaskVisualised tVis){
        if (!this.isEmpty()){
            Drawable current = this.first;
            while ((current != null) &&(current.getObjectPriority() ==1)) {
                if (tVis.getSTART_NODE() == ((TaskVisualised)current).getSTART_NODE()) {
                    if (tVis.getEND_NODE() == ((TaskVisualised) current).getEND_NODE()) {
                        return true;
                    }
                }
                else if (tVis.getSTART_NODE() == ((TaskVisualised)current).getEND_NODE()) {
                    if (tVis.getEND_NODE() == ((TaskVisualised) current).getSTART_NODE()) {
                        return true;
                    }
                }
                current = current.getNextDrawable();
            }
        }
        return false;
    }

    /**
     * Procedure to delete all the Tasks directly connected to a certain Node
     * Iterates through every TaskVisualised, and checks if it starts or ends at the given node
     * If it does, then it is deleted from the DrawableList, and gets its duration set to -1
     * Its duration is set to -1 to show that it has been deleted
     * @param n     - The given Node
     */
    void deleteConnected (Node n){
        if (!this.isEmpty()){
            Drawable current = this.first;
            if (current.getObjectPriority() == 1) {
                if ((n == ((TaskVisualised) current).getEND_NODE()) || (n == ((TaskVisualised) current).getSTART_NODE())) {
                    remove(current);
                }
                while ((current != null) && (current.getObjectPriority() == 1)) {
                    if ((n == ((TaskVisualised) current).getEND_NODE()) || (n == ((TaskVisualised) current).getSTART_NODE())) {
                        remove(current);
                        current.delete();
                    }
                    current = current.getNextDrawable();
                }
            }
        }
    }

    /**
     * Procedure used to draw objects on screen
     * Iterates through the list and runs the draw procedure for every item in list
     * @param g - Graphics object
     */
    void drawAll(Graphics g){
        Drawable current = this.first;
        while (current != null){
            current.draw(g);
            current = current.getNextDrawable();
        }
    }

    /**
     * Function to get the amount of marks awarded for highlighting tasks correctly
     * Sums the amount of tasks marked correctly
     * Divides this by the amount of tasks
     * Multiplies by 2 and rounds down to nearest integer (As tasks are marked out of two)
     * Note that dummy tasks are ignored by this algorithm
     * @return      - Rounded mark
     */
    int checkTaskMarks(){
        int totalMark = 0;
        int amountOfTasks = 0;
        if (!this.isEmpty()) {
            Drawable current = this.first;
            while ((current != null) && (current.getObjectPriority() == 1)) {
                TaskVisualised tV = (TaskVisualised) current;
                if (tV.getDuration() != 0) {
                    amountOfTasks++;
                    if (tV.checkMark() == 1){
                        totalMark++;
                    }
                }

                current = current.getNextDrawable();
            }
        }
        return (2 * totalMark) / (amountOfTasks);
    }
    /**
     * Function to find if a click location is within the area of an object
     * Iterates through the nodes first, running the checkIfInside function for each node
     * Then iterates through the TaskVisualised objects, and runs their checkIfInside functions
     * @param v - Click Vector location
     * @return  - Which drawable contains the vector, returns null if none contain
     */
    Drawable whichDrawableContains(Vector v) {
        Drawable current = this.first;
        while (current != null) {
            if (current.getObjectPriority() == 2) {  //Check nodes first
                if (current.checkIfInside(v)) {
                    return current;
                }
            }
            current = current.getNextDrawable();
        }
        current = this.first;
        while (current.getObjectPriority() == 1){
            if (current.checkIfInside(v)) {
                return current;
            }
            current = current.getNextDrawable();
        }
        return null;
    }

    /**
     * Procedure to iterate through all Drawables and make them display the calculated answers/if critical
     * @param b     - True if answers should be shown, false if they should stop being shown
     */
    void displayAnswers(boolean b){
        Drawable current = this.first;
        while (current != null){
            current.setDisplayingAnswers(b);
            current = current.getNextDrawable();
        }
    }

    /**
     * Function to get the first item in list
     * @return  - First
     */
    Drawable getFirst() {
        return first;
    }
}