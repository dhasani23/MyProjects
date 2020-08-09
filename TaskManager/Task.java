/**
 * Task.java 
 * This Task class features components of a Task object to be used in the TaskManager application. A Task
 * object has a name, an estimated time to complete, and a type (time of day).
 * @author David Hasani
 * @version 1.00 - May 15, 2020
 */

public class Task {
    
    /** name of the Task */
    private String name;
    
    /** estimated time to complete the Task (minutes) */
    private int time;
    
    /** when to do the Task; chosen from morning, afternoon, evening, anytime */
    private String type;

    /** Task object constructor to initialize the instance variables
     *  Use trim() to facilitate searching for Tasks by eliminating white space 
     */
    public Task(String tName, int tTime, String tType) {
        name = tName;
        time = tTime;
        type = tType;
    }

    /**
     * Accesses the name of the Task
     * @return the Task name
     */
    public String getName() {
        return name;
    }

    /**
     * Accesses the time of the Task
     * @return the Task time
     */
    public int getTime() {
        return time;
    }

    /**
     * Accesses the type of the Task
     * @return the Task type
     */
    public String getType() {
        return type;
    }

    /**
     * Puts together information about the Task
     * @return the Task description
     */
    @Override
    public String toString() {
        return "[" + name + ", " + time + ", " + type + "]";
    }
}
