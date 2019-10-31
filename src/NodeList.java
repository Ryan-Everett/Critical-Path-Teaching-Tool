import java.util.Iterator;

/**
 * Linked List class, of Nodes
 * Used to perform checks on all nodes and to display whether the user has inputted the correct event times
 */
class NodeList {
    private Node first = null;

    /**
     * Empty constructor for list with zero items
     */
    NodeList() {
    }

    /**
     * Function to get whether the list is empty
     * @return      - Whether list is empty
     */
    private boolean isEmpty() {
        return this.first == null;
    }

    /**
     * Function to check whether a vector location of the centre of a node lies within the distance of one radius away from any of the nodes in the list
     * Uses a while loop to iterate through the list and check if the joining vector between the two centres is
     * within the range of 0 to the sum of the radii
     * If true then the nodes intersect, the intersecting node is returned
     * @param v2    - The location of the vector
     * @param exclude       - The node which is excluded from the search (Used to stop a node being dragged from intersecting with itself)
     * @return      - The intersecting node
     */
    Node circleIntersectCheck(Vector v2, int exclude) {
        Node current = this.first;
        while (current != null) {
            if (current.getINDEX() != exclude) {
                double magSquared = current.getCentre().getJoiningVectorMagnitudeSquared(v2);
                if (magSquared <= 1600) {     // (R0 - R1)^2 <= (x0 - x1)^2 + (y0 - y1)^2 <= (R0 + R1)^2     Equation squared to avoid sqrt() function for efficiency
                    // System.out.println(magSquared);
                    return current;
                }
            }
            current = current.getNextNode();
        }
        return null;
    }

    /**
     * Iterates through the loop to find a given node
     * Once found, the node which follows this item is set to follow the previous node instead
     * This process removes the given node from the list
     * @param n      - The drawable to be removed
     */
    void remove (Node n){
        if (!this.isEmpty()){
            Node current = this.first;
            Node previous = this.first;
            if (n == current){
                this.first = current.getNextNode();
            }
            else {
                while (current != null) {
                    if (n == current) {
                        previous.setNextNode(current.getNextNode());
                        current = null;
                    } else {
                        previous = current;
                        current = current.getNextNode();
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
     * Function to return which node contains a vector location
     * Iterates through the list until a node is found to contain the click location
     * If no node contains the click then return null
     * @param vT        - Click location
     * @return      - The intersecting node
     */
    Node whichNodeContains(Vector vT) {
        Node current = this.first;
        while (current != null) {
            if (current.checkIfInside(vT)) {
                return current;
            }
            current = current.getNextNode();
        }
        return null;
    }

    /**
     * Procedure to insert a node into the list
     * Iterates through list to find the last node in the list
     * Sets the last nodes 'next' to the new node
     * @param item      - New Node to be added
     */
    void insert(Node item) {
        Node current = this.first;
        Node previous = this.first;
        if (this.isEmpty()) this.first = item;
        else {
            while (current != null) {
                previous = current;
                current = current.getNextNode();
            }
            previous.setNextNode(item);
        }
    }

    /**
     * Procedure to check the inputted answers for every node
     * Iterates through each node, setting the node into 'checkingAns' mode
     * If a node is at the end of the network, its times are evaluated
     *
     * nodeCount = amount of nodes in the path, used to get fraction of inputs which are correct,
     * This fraction is multiplied by 4 and rounded down to get total marks for nodes
     */
    int checkAnswers(){
        Node current = this.first;
        int nodeCount = 0;
        int marks = 0;
        while (current != null){
            if (current.isConnected()) {
                if (current.getSucceedingTasks().isEmpty()) {
                    Iterator<Task> it = current.getPrecedingTasks().iterator();
                    Task t = it.next();
                    current.setCalculatedEarlyStart(t.getLatestStart() + t.getDuration());
                    current.setCalculatedLatestStart(t.getLatestStart() + t.getDuration());
                }
                System.out.println(current.getINDEX() + "Is connected");
                current.setCheckingAns(true);
                marks += current.getMarksForNode();
                nodeCount++;
            }
            current = current.getNextNode();
        }
        int totalMarks;
        try {
            totalMarks = (4 * marks) / (2 * nodeCount);
        }
        catch (Exception zeroNodes){        //If there is no nodes, the above expression = n/0, which is undefined for all values of n
            totalMarks = 0;
        }
        System.out.println(totalMarks);
        return totalMarks;
    }

    /**
     * Procedure to iterate through all the nodes and stop them from checking inputted answers
     * Removes all calculated times
     */

    void setCheckAnswersFalse(){
        Node current = this.first;
        while (current != null){
            System.out.println(current.getINDEX());
            current.setCheckingAns(false);
            current.setCalculatedEarlyStart(0);
            current.setCalculatedLatestStart(0);
            current = current.getNextNode();
        }
    }
}