import java.util.ArrayList;

public class Epic extends Task{
    ArrayList<Task> subtasks;

    public Epic(String name, String taskInfo, int id) {
        super(name, taskInfo, id);
        subtasks = new ArrayList<>();
    }

    public void addSubtask(Task subtask){
        subtasks.add(subtask);
    }



    @Override
    public String toString() {
        return super.toString() + ", subtasks=" + subtasks;
    }
}
