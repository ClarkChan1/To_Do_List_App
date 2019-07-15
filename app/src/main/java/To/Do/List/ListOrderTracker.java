package To.Do.List;

public class ListOrderTracker {
    boolean todoAscending;
    boolean completedAscending;
    boolean overdueAscending;

    public ListOrderTracker(boolean todoAscending, boolean completedAscending, boolean overdueAscending) {
        this.todoAscending = todoAscending;
        this.completedAscending = completedAscending;
        this.overdueAscending = overdueAscending;
    }

}
