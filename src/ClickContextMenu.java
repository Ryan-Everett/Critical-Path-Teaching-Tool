import javax.swing.*;

/**
 * Class for the context menu object, which opens when the user 'right-clicks'
 */
class ClickContextMenu extends JPopupMenu{
    private final JMenuItem NEW_TASK;
    private final JMenuItem NEW_NODE;
    /**
     * Constructor for ClickContextMenu
     * Initialises the options and adds them to the menu
     */
    ClickContextMenu(){
        NEW_TASK = new JMenuItem("Add Task");
        NEW_NODE = new JMenuItem("Add/Drag Node");

        add(NEW_TASK);
        add(NEW_NODE);
    }

    /**
     * Get the newNode item
     * @return      - NEW_NODE item
     */
    public JMenuItem getNEW_NODE() {
        return NEW_NODE;
    }

    /**
     * Get the newTask item
     * @return      - NEW_TASK item
     */
    public JMenuItem getNEW_TASK() {
        return NEW_TASK;
    }
}
