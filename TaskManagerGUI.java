/**
 * A Java GUI for managing a list of tasks.
 * @author David Hasani
 * @version 1.00 - May 5, 2020
 */

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

public class TaskManagerGUI {
    
    /** title within GUI */
    private JLabel title;
    
    /** sub-headings within GUI */
    private JLabel leftHeading;
    private JLabel rightHeading;
    
    /** where to enter the name of the Task */
    private JTextField nameField;
    
    /** where to enter the estimated time to complete the Task (minutes) */
    private JTextField timeField;
    
    /** where to select when to do the Task; chosen from morning, afternoon, evening, anytime */
    private JComboBox<String> typeField;
    
    /** adds a Task to TaskList; name, time, & type must be filled out */
    private JButton add;
    
    /** removes a Task from TaskList; uses only the name supplied
     *  if > 1 Task with same name present, all are removed
     */
    private JButton remove;
    
    /** checks if a Task is in TaskList; uses only the name supplied
     *  if > 1 Task with same name present, first one originally added is returned
     */
    private JButton search;
    
    /**
     * searches for the Tasks with smallest and largest time values
     * if only 1 Task is in TaskList, it is both the shortest and longest
     */
    private JButton searchSL;
    
    /** resets TaskList */
    private JButton clear;
    
    /** leaves the GUI */
    private JButton exit;
    
    /** where the TaskList itself is displayed */
    private JTextPane taskList;
    
    /** allow for scrolling through TaskList */
    private JScrollPane scrollTaskList;
}
