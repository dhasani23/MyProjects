/**
 * Comparator for Task objects by type (time of day)
 * Sort as morning before afternoon before evening before anytime
 * @author David Hasani
 * @version 1.00 - May 15, 2020
 */
import java.util.Comparator;
public class TaskTypeComparator implements Comparator<Task> {
    public int compare(Task t1, Task t2) {
        if (!(t1.getType().equals(t2.getType())) && ((t1.getType().equals("morning") || t2.getType().equals("anytime")) || 
            (t1.getType().equals("afternoon") && t2.getType().equals("evening")))) {
            return -1;  // if this is the case, it is guaranteed that t1 comes before t2, or if the types are equal store in same order as insertion
        }
        else {
            return 1;
        }
    }
}
