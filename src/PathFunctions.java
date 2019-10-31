import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Class with methods to perform algorithms to calculate late event times, early event times, and critical information about tasks
 */
class PathFunctions {
    private final HashSet<Task> TASKS = new HashSet<>();
    private int cpLength;

    /**
     * Procedure to add a task to the set of tasks which are part of the network
     * If null, the task is not added
     * @param t        - The task to be added
     */
    void addTask(Task t){
        if (t != null) {
            TASKS.add(t);
        }
    }

    /**
     * Procedure to remove all the tasks from the HashSet
     */
    void deleteAllTasks(){
        TASKS.clear();
    }
    /**
     * Procedure used to calculate all the information needed about a set of tasks
     * Updates all the Task's durations, if a Task has a duration of -1, it has been removed from the network,
     * so needs to be removed from the HashSet
     * Also sets all the values to be calculated back to 0, ensuring previous calculations do not interfere with the algorithm
     * The algorithm starts by creating the ‘completed’ set, which holds the tasks whose critical costs have been calculated
     * In addition to the ‘remaining’ set, this holds the rest of the tasks.
     * Initially, ‘remaining’ holds all the tasks
     * The first ‘for’ and ‘while’ loops iterate through every task still in ‘remaining’, until ‘remaining’ is empty
     * On each iteration, the code within the loop tries to calculate the criticalCost for the current task
     * To calculate the criticalCost, every task which the current task is dependent on needs to have a calculated criticalCost
     * To verify this, an ‘if’ statement checks if all the dependencies are in the completed set
     * If they are not, the loop re-iterates with the next task
     * However, if the statement returns true, the ‘critical’ variable is set to the largest criticalCost of all the dependencies
     * Then the criticalCost of the current task is calculated, and the task can now be marked as completed
     * So it is added to the set called 'completed', and removed from 'remaining'
     * Progress is marked as true if this happens
     * If no progress is made over the whole of the ‘for’ loop, then the path must contain a cycle, which means there is no path from start to finish.
     * On this occasion, the user will be notified that the path they have created is cyclic
     * Then uses methods to calculate the max cost and early start and finishes of the task
     */

    void criticalPath(){
        Task[] markedToDelete = new Task[TASKS.size()];
        int markedToDeleteIndex = 0;
        for (Task current: TASKS){
            current.updateDuration();
            current.setEarlyStart(0);
            current.setEarlyFinish(0);
            current.setCriticalCost(0);
            current.setLatestStart(0);
            if (current.getDuration() == -1){
                markedToDelete[markedToDeleteIndex] = current;      //Need to add to array and delete after, to avoid concurrent modification exception
                markedToDeleteIndex++;
                System.out.print("DEL");
            }
        }
        for (Task t : markedToDelete){
            TASKS.remove(t);            //Now the deleted tasks can be safely removed from the HashSet
        }
        HashSet<Task> completed = new HashSet<>();
        HashSet<Task> remaining = new HashSet<>(TASKS);
        while (!remaining.isEmpty()) {
            boolean progress = false;
            for (Iterator<Task> it = remaining.iterator(); it.hasNext();) {
                Task task = it.next();
                System.out.println(task.getTASK_VISUALISED().getSTART_NODE().getINDEX());
                if (completed.containsAll(task.getTASK_VISUALISED().getEND_NODE().getSucceedingTasks())) {    // all the tasks dependencies have calculated costs
                    int critical = 0;
                    for (Task t : task.getTASK_VISUALISED().getEND_NODE().getSucceedingTasks()) {     // Find the greatest critical cost preceding the current task
                        if (t.getCriticalCost() > critical) {
                            critical = t.getCriticalCost();
                        }
                    }
                    task.setCriticalCost(critical + task.getDuration());
                    completed.add(task);        // task now has the greatest value of critical cost it can have
                    it.remove();
                    progress = true;
                }
            }
            if (!progress) {      // Cycle exists if no progress
                throw new RuntimeException("Cycle exists in path");
            }
        }
        cpLength();     // calculate cost and early starts
        HashSet<Task> initialTasks = initials(TASKS);
        calculateEarly(initialTasks);
    }

    /**
     * Procedure to set the calculate the early event times for initial tasks to 0, and then start setting the following tasks' event times
     * @param initials      - The initial tasks in the network
     */
    private void calculateEarly(HashSet<Task> initials) {
        for (Task initial : initials) {
            initial.setEarlyStart(0);
            initial.getTASK_VISUALISED().getSTART_NODE().setCalculatedEarlyStart(0);
            System.out.println("Setting initial     " + initial.getDuration());
            initial.setEarlyFinish(initial.getDuration());
            setEarly(initial);
        }
    }

    /**
     * Function to identify the initial tasks in the network
     * Creates a HashSet of all the tasks
     * Iterates through every task, removing tasks which are dependent on the current task from the HashSet
     * The tasks remaining in the HashSet are dependent on nothing, so must be at the start of the network
     * @param tasks     - Every task
     * @return      - The HashSet of initial tasks
     */
    private HashSet<Task> initials(Set<Task> tasks) {
        HashSet<Task> remaining = new HashSet<>(tasks);
        for (Task t : tasks) {
            for (Task td : t.getTASK_VISUALISED().getEND_NODE().getSucceedingTasks()) {
                remaining.remove(td);
            }
        }
        return remaining;
    }

    /**
     * Recursive procedure to set the early start and early finish times of all the non-initial tasks
     * Iterates through the tasks which are directly dependent on the task passed into the procedure
     * Checks if the task has already had its early start time calculated, and that this early start time needs to be changed
     * If not, then uses the task passed in to find the current task's early start and early finish times
     * Uses a recursive call with the current task as a parameter, to calculate the early times for the task which immediately follows the current task
     * This ensures every unique path on the network is followed, and every task gets calculated
     * Note the recursive call doesn't occur if a task already had calculated event times, this stops the procedure from following paths which have already been traversed
     *
     * This algorithm works because, when it is ran, every initial task has their event times calculated,
     * and the first call of the procedure is always using an initial task
     * @param t1        - The task with known early event times, used to calculate following tasks
     */
    private void setEarly(Task t1) {
        int completionTime = t1.getEarlyFinish();
        for (Task t2 : t1.getTASK_VISUALISED().getEND_NODE().getSucceedingTasks()) {
            if (completionTime >= t2.getEarlyStart()) {
                System.out.println("Setting early start and finish     " + t1.getDuration());
                t2.setEarlyStart(completionTime);
                t2.setEarlyFinish(completionTime + t2.getDuration());
                setEarly(t2);
            }

            if (t2.getTASK_VISUALISED().getSTART_NODE().getCalculatedLatestStart() == 0) {
                t2.getTASK_VISUALISED().getSTART_NODE().setCalculatedLatestStart(t2.getLatestStart());
            }

            else if (t2.getTASK_VISUALISED().getSTART_NODE().getCalculatedLatestStart() > t2.getLatestStart()) {
                t2.getTASK_VISUALISED().getSTART_NODE().setCalculatedLatestStart(t2.getLatestStart());
            }

            if (t2.getTASK_VISUALISED().getSTART_NODE().getCalculatedEarlyStart() < t2.getEarlyStart()) {
                t2.getTASK_VISUALISED().getSTART_NODE().setCalculatedEarlyStart(t2.getEarlyStart());
            }
        }
    }

    /**
     * Procedure to calculate the length of the critical path
     * Iterates through the complete list of tasks and finds which task has the greatest criticalCost
     * The greatest critical cost is equal to the critical path length
     */
    private void cpLength() {
        int max = -1;
        for (Task t : TASKS) {
            if (t.getCriticalCost() > max)
                max = t.getCriticalCost();
        }
        cpLength = max;
        System.out.println("Critical path length (cost): " + cpLength);
        for (Task t : TASKS) {
            System.out.println("Setting max cost    " + t.getDuration());
            t.setLatestStart(cpLength);
        }
    }

    /**
     * Function to get the critical path
     * @return      - Critical path length
     */
    int getCpLength() {
        return cpLength;
    }
}