/**
 * A Java GUI for managing a list of tasks.
 * @author David Hasani
 * @version 1.00 - May 15, 2020
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class TaskManagerGUI extends JFrame {
    
    /** Holds all components in left half of GUI */
    private JPanel leftPan;
    
    /** Holds all time labels and fields in left half of GUI */
    private JPanel leftTimePan;
    
    /** Holds all components in right half of GUI */
    private JPanel rightPan;
    
    /** Holds all components at bottom of GUI */
    private JPanel bottomPan;
    
    /** Welcome label within GUI */
    private JLabel welcome;
    
    /** Left heading within GUI */
    private JLabel leftHead;
    
    /** Right heading within GUI */
    private JLabel rightHead;
    
    /** Task name label */
    private JLabel nameLabel;
    
    /** Task time label */
    private JLabel timeLabel;
    
    /** Task time label (hours) */
    private JLabel timeLabelHr;
    
    /** Task time label (minutes) */
    private JLabel timeLabelMin;
    
    /** Task type label */
    private JLabel typeLabel;
    
    /** Output label; shows results of the searches and removals */
    private JLabel outputLabel;
    
    /** Where to enter the name of the Task */
    private JTextField nameField;
    
    /** Where to enter the estimated time (hours portion) to complete the Task */
    private JTextField timeFieldHr;
    
    /** Where to enter the estimated time (minutes portion) to complete the Task */
    private JTextField timeFieldMin;
    
    /** Where to select when to do the Task; chosen from morning, afternoon, evening, anytime */
    private JComboBox<String> typeBox;
    
    /** Adds a Task to Task List; name, time, & type must be filled out */
    private JButton add;
    
    /** Removes a Task from Task List; uses only the name supplied
     *  If > 1 Task with same name present, first one (by type aka time of day) is removed
     */
    private JButton remove;
    
    /** Checks if a Task is in Task List; uses only the name supplied
     *  If > 1 Task with same name present, first one (by type aka time of day) is displayed
     */
    private JButton search;
    
    /**
     * Searches for the Tasks with smallest and largest time values
     * If only 1 Task is in Task List, it is both the shortest and longest
     * If Tasks share a time value, the first Task is chosen
     */
    private JButton searchSL;
    
    /** Resets GUI */
    private JButton clear;
    
    /** Where the Task List itself is displayed */
    private JTextPane taskPane;
    
    /** Allow for scrolling through Task List */
    private JScrollPane taskScrollPane;
    
    /** Array of Strings representing the options for type of Task */
    private String[] types = {"morning", "afternoon", "evening", "anytime"};
    
    /** Data structure to hold the Task objects
     *  Kept sorted by type aka time of day: morning -> afternoon -> evening -> anytime
     */
    private ArrayList<Task> tasks = new ArrayList<Task>();
    
    /** Configure the GUI properly */
    private void configureGUI() {
        GridBagConstraints con = new GridBagConstraints();
        con.insets = new Insets(10,10,10,10); // spacing between components
        con.gridx = 0;
        con.gridy = 0;
        // add left heading
        leftPan = new JPanel(new GridBagLayout()); // overall left panel
        leftHead = new JLabel("Individual Tasks");
        leftPan.add(leftHead, con);
        // add task creation components on left side, starting with the name label
        con.gridy++;
        nameLabel = new JLabel("Name:");
        leftPan.add(nameLabel, con);
        // add time label
        con.gridy++;
        timeLabel = new JLabel("Time (fill in both):"); // example acceptable input = 1,30 to represent 1 hour and 30 minutes
        leftPan.add(timeLabel, con);
        // add type label
        con.gridy++;
        typeLabel = new JLabel("Type:");
        leftPan.add(typeLabel, con);
        // add name field
        con.gridx++;
        con.gridy = 1;      
        nameField = new JTextField(15);
        leftPan.add(nameField, con);
        // add time labels and fields panel
        con.gridy++;
        leftTimePan = new JPanel(); // uses flow layout by default
        timeFieldHr = new JTextField(3);
        timeFieldMin = new JTextField(3);
        timeLabelHr = new JLabel("hr");
        timeLabelMin = new JLabel("min");
        leftTimePan.add(timeFieldHr);
        leftTimePan.add(timeLabelHr);
        leftTimePan.add(timeFieldMin);
        leftTimePan.add(timeLabelMin);
        leftPan.add(leftTimePan, con);
        // add type combo box
        con.gridy++;
        typeBox = new JComboBox<String>(types);
        leftPan.add(typeBox, con);
        // add right heading
        con.gridx = 0;
        con.gridy = 0;
        rightPan = new JPanel(new GridBagLayout());
        rightHead = new JLabel("Overall Task List");
        rightPan.add(rightHead, con);
        // add text/scroll pane on right side
        con.gridy++;
        taskPane = new JTextPane();
        taskPane.setPreferredSize(new Dimension(350,175));
        taskPane.setEditable(false);
        taskScrollPane = new JScrollPane(taskPane);
        taskScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        rightPan.add(taskScrollPane, con);
        con.gridy++;
        outputLabel = new JLabel("");
        rightPan.add(outputLabel, con);
        // add buttons along bottom of frame
        bottomPan = new JPanel(); // uses flow layout by default
        add = new JButton("Add");
        // add the task to the list
        add.addActionListener(e -> {
           if (add.getActionCommand().equals("Add")) {
               outputLabel.setText(""); // reset output label
               String name = nameField.getText().trim();
               if (name.length() == 0) { // invalid input
                   outputLabel.setText("Add result:  invalid Name input");
               } else {
                   try {
                       int hr = Integer.parseInt(timeFieldHr.getText().trim()); // get hours input
                       int min = Integer.parseInt(timeFieldMin.getText().trim()); // get minutes input
                       if (hr < 0 || hr > 23 || min < 0 || min > 59 || (hr == 0 && min == 0)) { // invalid input, display "ERROR!"
                           throw new NumberFormatException();
                       } else {
                           String type = (String) typeBox.getSelectedItem(); // get type input
                           Task t = new Task(name, hr*60 + min, type); // create a Task, storing time internally as total # minutes only
                           if (isDuplicate(t)) { // check if it is a duplicate
                               outputLabel.setText("Add result:  exact same Task already in Task List");
                           } else {  // getting here means that exact same Task is not already in Task List (matching name and time and type)
                               tasks.add(t); // add Task to list
                               Collections.sort(tasks, new TaskTypeComparator()); // sort tasks by type aka time of day
                               taskPane.setText(printList()); // print the list in the text pane
                               // reset Task creation fields
                               clearFields();
                           }
                       }   
                   } catch (NumberFormatException x) { // invalid input
                       outputLabel.setText("Add result:  invalid Time input");
                   }
               }
            }
        });
        remove = new JButton("Remove (by Name only)");
        // remove task from the list, searching by name only (first task in list with matching name is removed)
        remove.addActionListener(e -> {
            if (remove.getActionCommand().equals("Remove (by Name only)")) {
                outputLabel.setText(""); // reset output label
                searchOrRemove("Remove result:  ", "  removed");
                tasks.remove(findTask(nameField.getText().replaceAll("\\s", ""))); // remove Task from ArrayList
                taskPane.setText(printList()); // update list of Tasks
                clearFields();
            }
        });
        search = new JButton("Search (by Name only)");
        // search for a task by name only (first task in list with matching name is displayed)
        search.addActionListener(e -> {
           if (e.getActionCommand().equals("Search (by Name only)")) {
               searchOrRemove("Search result:  ", "  found");
               clearFields();
            }
        });
        searchSL = new JButton("Search for Shortest & Longest");  
        // search for the shortest and longest tasks (by time)
        searchSL.addActionListener(e -> {
            if (searchSL.getActionCommand().equals("Search for Shortest & Longest")) {
                outputLabel.setText(""); // reset output label
                if (!checkEmpty("Search for S & L result:  ", " ")) { // check if list is empty
                    Task shortT = tasks.get(0);
                    Task longT = tasks.get(0);
                    for (int i = 1; i < tasks.size(); i++) { // find shortest and longest Tasks, if multiple Tasks with same time: finds Task that appears first in list
                        Task t1 = tasks.get(i);
                        Task t2 = tasks.get(i);
                        if (t1.getTime() < shortT.getTime()) {
                            shortT = t1;
                        }
                        if (t2.getTime() > longT.getTime()) {
                            longT = t2;
                        }
                    }
                    String s = "Shortest Task is " + shortT.toString() + "\nLongest Task is " + longT.toString();
                    outputLabel.setText("<html>" + s.replaceAll("\n", "<br/>") + "</html>"); // inserts new line in HTML   
                }
                clearFields();
            }
        });
        clear = new JButton("Clear All");
        // empty text pane, task creation fields, and ArrayList when clear button is pressed
        clear.addActionListener(e -> {
            if (e.getActionCommand().equals("Clear All")) {
                outputLabel.setText(""); // reset output label
                taskPane.setText("");
                tasks.clear();
                clearFields();
            }
        });
        
        // add buttons to bottom panel
        bottomPan.add(add);
        bottomPan.add(remove);
        bottomPan.add(search);
        bottomPan.add(searchSL);
        bottomPan.add(clear);
        bottomPan.setOpaque(true);
        
        welcome = new JLabel("Welcome to Task Manager!", SwingConstants.CENTER);
        
        formatAll(); // format all components with Fonts
        
        // add all panels to the GUI
        this.add(leftPan, BorderLayout.WEST);
        this.add(rightPan, BorderLayout.EAST);
        this.add(bottomPan, BorderLayout.SOUTH);
        
        // add a Welcome label on top
        this.add(welcome, BorderLayout.NORTH);
    }  
    
    /**
     * Method to check if Task check is an exact duplicate of a Task already in list
     * @param check the Task to check
     * @return whether or not check is an exact duplicate
     */
    public boolean isDuplicate(Task check) {
        boolean b = false;
        for (Task t : tasks) {
            if ((t.getName().replaceAll("\\s", "").equalsIgnoreCase(check.getName().replaceAll("\\s", ""))) && (t.getTime() == check.getTime())
                    && (t.getType().equals(check.getType()))) {
                b = true;
            }
        }
        return b;
    }
    
    /**
     * Method to print the contents of the Task List, with a new line between elements
     * @return the String summary of the Task List
     */
    public String printList() {
        String s = "";
        for (Task t : tasks) {
            s += t.toString() + "\n";
        }
        return s;
    }
    
    /** Method to clear the task creation fields */
    public void clearFields() {
        nameField.setText("");
        timeFieldHr.setText("");
        timeFieldMin.setText("");
        typeBox.setSelectedIndex(0);
    }
    
    /**
     * Method used to both search and remove a Task
     * Updates the output label with the results of the search / removal
     * @param start the beginning of the output label (either "Search result:  " or "Removal result:  ")
     * @param end the end of the output label (either "  found" or "  removed")
     */
    public void searchOrRemove(String start, String end) {
        String nameToCheck = nameField.getText().replaceAll("\\s", ""); // remove spaces to check for a match
        Task t = findTask(nameToCheck); // check if Task is present
        if (!checkEmpty(start, nameToCheck)) { // check if list is empty or no name is entered
            if (t != null) { // Task is present
                outputLabel.setText(start + t.toString() + end);
            } else {
                outputLabel.setText(start + "Task  '" + nameField.getText() + "'  not found");
            }
        }
    }
    
    /**
     * Method to check if either the list of tasks or Name input is empty
     * @param start the beginning of the output label (either "Search result:  " or "Removal result:  ")
     * @param name the trimmed text in the Name field
     * @return whether or not one is empty
     */
    public boolean checkEmpty(String start, String name) {
        boolean b = false;
        if (tasks.size() == 0) {
            outputLabel.setText(start + "Task List is empty");
            b = true;
        } else if (name.length() == 0) {
            outputLabel.setText(start + "invalid Name input");
            b = true;
        }
        return b;
    }
    
    /**
     * Method to find a task in the Task List for searching or removing
     * If > 1 Task with same name, first one found is returned
     * @param name the Task name to find
     * @return the Task, or null if it is not found
     */
    public Task findTask(String name) {
        Task tsk = null;
        for (Task t : tasks) {
            if (t.getName().replaceAll("\\s", "").equalsIgnoreCase(name)) { // check for matching Task name
                return t;
            }
        }
        return tsk;
    }
    
    /** Method to format all components with Fonts */
    public void formatAll() {
        welcome.setFont(new Font("Arial", Font.BOLD, 22));
        leftHead.setFont(new Font("Arial", Font.ITALIC, 18));
        rightHead.setFont(new Font("Arial", Font.ITALIC, 18));
        Font f = new Font("Georgia", Font.BOLD, 14);
        Font f2 = new Font("Georgia", Font.PLAIN, 14);
        nameLabel.setFont(f);
        nameField.setFont(f2);
        timeLabel.setFont(f);
        timeFieldHr.setFont(f2);
        timeLabelHr.setFont(f);
        timeFieldMin.setFont(f2);
        timeLabelMin.setFont(f);
        typeLabel.setFont(f);
        typeBox.setFont(f);
        add.setFont(f);
        remove.setFont(f);
        search.setFont(f);
        searchSL.setFont(f);
        clear.setFont(f);
        taskPane.setFont(f2);
        outputLabel.setFont(f);
    }

    /** Display the GUI */
    public static void main(String[] args) {
        TaskManagerGUI applet = new TaskManagerGUI();
        applet.setTitle("TaskManager");
        applet.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        applet.setLayout(new BorderLayout());
        javax.swing.SwingUtilities.invokeLater(()->applet.configureGUI());
        applet.pack();
        applet.setSize(875,500);
        applet.setVisible(true);
    } 
}
