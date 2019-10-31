import javax.swing.*;
import java.awt.event.*;
/**
 * Controller class used to process mouse and key inputs
 *
 * Implements action listener to listen to button presses
 */
class Controller implements ActionListener {
    private final GUIMain VIEW_MAIN;
    private final ClickContextMenu VIEW_CON_MENU;
    private boolean drawing;
    private boolean placingNode;
    private boolean selecting;
    private boolean controlHeld;
    private final PathFunctions PATH_FUNCTIONS;
    /**
     * Constructor for controller
     * Creates a PathFunctions object
     * Creates a KeyAdapter which listens to 'ctrl' key
     * Creates a MouseAdapter to listen to mouse location and click behaviour
     * Adds itself as an ActionListener to all buttons
     * @param guiMain   - The main GUI
     * @param cCMenu    - The context menu
     */
    Controller(GUIMain guiMain, ClickContextMenu cCMenu){
        this.VIEW_MAIN = guiMain;
        this.VIEW_CON_MENU = cCMenu;
        this.PATH_FUNCTIONS = new PathFunctions();
        this.drawing = false;
        this.placingNode = true;
        this.selecting = false;

        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    controlHeld = true;
                }
                else if (e.getKeyCode() == KeyEvent.VK_DELETE){
                    System.out.println("CTRL");
                    VIEW_MAIN.getPComponent().deleteSelected();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    controlHeld = false;
                }
            }
        };
        MouseAdapter mainMouseAdapter = new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    System.out.println(e.getX()  + " , " + e.getY());

                    if (!placingNode) {
                        drawing = true;
                        Node n = VIEW_MAIN.startDrawing(new Vector(e.getX(), e.getY()));
                        if (n == null) {
                            VIEW_MAIN.getPComponent().updateSelected(new Vector(e.getX(), e.getY()));
                            drawing = false;
                        }
                    }
                    else if (controlHeld){
                        System.out.println("CTRL CLICK");
                        VIEW_MAIN.getPComponent().ctrlClick(new Vector(e.getX(), e.getY()));
                    }
                    else if (e.getClickCount() == 2){
                        VIEW_MAIN.doubleClicked();
                    }
                    else if (placingNode) {
                        VIEW_MAIN.getPComponent().nodeModeClick(new Vector(e.getX(), e.getY()));
                    }

                }
                else{
                    VIEW_MAIN.getPComponent().selectNull();
                }
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e)) {
                    VIEW_MAIN.getPComponent().dragSelected(new Vector(e.getX(), e.getY()));
                }

            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (selecting){
                    selecting = false;
                }
                else if (drawing) {
                    Task t = VIEW_MAIN.getPComponent().finishDrawing(new Vector(e.getX(), e.getY()));
                    PATH_FUNCTIONS.addTask(t);
                    drawing = false;
                }

            }

        };

        VIEW_MAIN.getPComponent().addMouseListener(mainMouseAdapter);
        VIEW_MAIN.getPComponent().addMouseMotionListener(mainMouseAdapter);
        VIEW_MAIN.getCHECK_BUTTON().addActionListener(this);
        VIEW_MAIN.getCHECK_VALID_BUTTON().addActionListener(this);
        VIEW_MAIN.getCLEAR_BUTTON().addActionListener(this);
        VIEW_MAIN.getPComponent().setComponentPopupMenu(cCMenu);
        VIEW_MAIN.getPComponent().addKeyListener(keyAdapter);
        VIEW_CON_MENU.getNEW_TASK().addActionListener(this);
        VIEW_CON_MENU.getNEW_NODE().addActionListener(this);
    }

    /**
     * Processes button clicks for every button and every phase of each button
     * @param ae    - The action to be processed
     */
    public void actionPerformed(ActionEvent ae)
    {
        if (ae.getSource() == VIEW_CON_MENU.getNEW_TASK()){
            placingNode = false;
            System.out.println("Task Mode");
        }
        else if (ae.getSource() == VIEW_CON_MENU.getNEW_NODE()){
            placingNode = true;
            System.out.println("Node Mode");
        }


        else if (ae.getSource() == VIEW_MAIN.getCHECK_VALID_BUTTON()){
            switch (VIEW_MAIN.getCHECK_VALID_BUTTON().getText()) {
                case "Check Validity Of Network":
                    try {
                        PATH_FUNCTIONS.criticalPath();
                        VIEW_MAIN.setCRIT_PATH_LENGTH("Network is valid");
                    } catch (RuntimeException cyclicDependency) {
                        System.out.println("Runtime");
                        VIEW_MAIN.setCRIT_PATH_LENGTH("Network is invalid");
                    }
                    break;

                case "Show Answers":
                    VIEW_MAIN.getPComponent().getD_LIST().displayAnswers(true);
                    VIEW_MAIN.getCHECK_VALID_BUTTON().setText("Stop Showing Answers");
                    VIEW_MAIN.getPComponent().repaint();
                    break;

                default:
                    VIEW_MAIN.getPComponent().getD_LIST().displayAnswers(false);
                    VIEW_MAIN.getCHECK_VALID_BUTTON().setText("Show Answers");
                    int nodeMarks = VIEW_MAIN.getPComponent().getN_LIST().checkAnswers();
                    VIEW_MAIN.getCHECK_BUTTON().setText("Stop Checking Answers");
                    VIEW_MAIN.getCHECK_VALID_BUTTON().setText("Show Answers");
                    int taskMarks = VIEW_MAIN.getPComponent().getD_LIST().checkTaskMarks();
                    int[] markArray = new int[2];
                    markArray[0] = nodeMarks;
                    markArray[1] = taskMarks;
                    VIEW_MAIN.setMarkAmount(markArray);
                    VIEW_MAIN.getPComponent().repaint();
                    break;
            }
        }

        else if (ae.getSource()==VIEW_MAIN.getCHECK_BUTTON()){
            if (VIEW_MAIN.getCHECK_BUTTON().getText().equals("Check Answers")) {
                try {
                    System.out.println("Starting algorithm");
                    VIEW_MAIN.getPComponent().getN_LIST().setCheckAnswersFalse();
                    PATH_FUNCTIONS.criticalPath();
                    VIEW_MAIN.setCRIT_PATH_LENGTH("Critical path length: " + PATH_FUNCTIONS.getCpLength());
                    int nodeMarks = VIEW_MAIN.getPComponent().getN_LIST().checkAnswers();
                    VIEW_MAIN.getCHECK_BUTTON().setText("Stop Checking Answers");
                    VIEW_MAIN.getCHECK_VALID_BUTTON().setText("Show Answers");
                    int taskMarks = VIEW_MAIN.getPComponent().getD_LIST().checkTaskMarks();
                    int [] markArray = new int[2];
                    markArray[0] = nodeMarks;
                    markArray[1] = taskMarks;
                    VIEW_MAIN.setMarkAmount(markArray);
                    VIEW_MAIN.getPComponent().repaint();
                }
                catch (RuntimeException re){
                    System.out.println("Runtime");
                    VIEW_MAIN.setCRIT_PATH_LENGTH("Cyclic dependency in Network");
                }
            }
            else{
                VIEW_MAIN.getCHECK_BUTTON().setText("Check Answers");
                VIEW_MAIN.getCHECK_VALID_BUTTON().setText("Check Validity Of Network");
                VIEW_MAIN.clearMarkLabel();
                VIEW_MAIN.getPComponent().getN_LIST().setCheckAnswersFalse();
                VIEW_MAIN.getPComponent().getD_LIST().displayAnswers(false);
                VIEW_MAIN.setCRIT_PATH_LENGTH("");
                VIEW_MAIN.getPComponent().repaint();
            }
        }
        else if (ae.getSource() == VIEW_MAIN.getCLEAR_BUTTON()){
            PATH_FUNCTIONS.deleteAllTasks();
            VIEW_MAIN.getPComponent().clearComponent();
            System.out.println("Clear");
        }
        VIEW_MAIN.getPComponent().requestFocusInWindow();
    }
}