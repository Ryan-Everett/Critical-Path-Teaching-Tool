/**
 * Object for the tasks
 * Stores all the information calculated by the path functions
 * Each task is created in terms of a TaskVisualised, with there being exactly one TaskVisualised to represent each task
 */
class Task {
    private int duration;
    private int criticalCost;
    private int earlyStart;
    private int earlyFinish;
    private int latestStart;
    private final TaskVisualised TASK_VISUALISED;
    /**
     * Constructor for the task object
     * Sets the TASK_VISUALISED, the duration of the task, and the tasks which are directly dependant on the new task
     * @param tVis      - The TaskVisualised which represents the Task
     */
    Task(TaskVisualised tVis){
        this.TASK_VISUALISED = tVis;
        this.duration = tVis.getDuration();
    }

    /**
     * Procedure to change the latestStart time of a task
     * @param cpLength   - The critical path length
     */
    void setLatestStart(int cpLength) {
        latestStart = cpLength - criticalCost;
    }

    /**
     * Procedure to update the duration of the task, used when information about the TaskVisualiseds may have been changed
     * If a task is marked as deleted, it is now disconnected from the nodes
     */
    void updateDuration(){
        this.duration = TASK_VISUALISED.getDuration();
        if (duration == -1){
            try {
                TASK_VISUALISED.getSTART_NODE().getSucceedingTasks().remove(this);
            }
            catch (Exception startNodeDeleted){
                //Start node is deleted, no action needed
            }
            try {
                TASK_VISUALISED.getEND_NODE().getPrecedingTasks().remove(this);
            }
            catch (Exception endNodeDeleted){
                //End node is deleted, no action needed
            }
        }
    }

    /**
     * Function to get the task's critical cost
     * @return      - Critical cost
     */
    int getCriticalCost() {
        return criticalCost;
    }

    /**
     * Procedure to set the critical cost of the task
     * @param criticalCost      - New value for critical cost
     */
    void setCriticalCost(int criticalCost) {
        this.criticalCost = criticalCost;
    }

    /**
     * Function to get the task's early start time
     * @return      - Early start time
     */
    int getEarlyStart() {
        return earlyStart;
    }

    /**
     * Procedure to set the early start time of the task
     * @param earlyStart        - New value for early start
     */
    void setEarlyStart(int earlyStart) {
        this.earlyStart = earlyStart;
    }

    /**
     * Function to get the task's early finish
     * @return      - Early finish time of task
     */
    int getEarlyFinish() {
        return earlyFinish;
    }

    /**
     * Procedure to set the early finish time of the task
     * @param earlyFinish       - New value for early finish
     */
    void setEarlyFinish(int earlyFinish) {
        this.earlyFinish = earlyFinish;
    }

    /**
     * Function to get the task's latest start time
     * @return      - Latest start time of task
     */
    int getLatestStart() {
        return latestStart;
    }

    /**
     * Function to get the duration of the task
     * @return      - Task's duration
     */
    int getDuration(){
        return this.duration;
    }

    /**
     * Function to get the task's TaskVisualised
     * @return      - The TaskVisualised which represents the task
     */
    TaskVisualised getTASK_VISUALISED() {
        return TASK_VISUALISED;
    }
}
