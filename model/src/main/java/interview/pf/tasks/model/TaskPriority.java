package interview.pf.tasks.model;

public enum TaskPriority {
    LOW(1), MEDIUM(2), HIGH(3);

    private final int priorityCode;

    TaskPriority(int priorityCode){
        this.priorityCode = priorityCode;
    }

    public int getPriorityCode() {
        return priorityCode;
    }

    public static TaskPriority valueOf(int intPriority){
        for (TaskPriority priority : TaskPriority.values()){
            if (priority.getPriorityCode() == intPriority){
                return priority;
            }
        }
        return null;
    }
}
