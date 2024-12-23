import java.util.Objects;

public class Task {

   private String name;
   private String taskInfo;
   private int id;
   private TaskStatus status;

    public Task(String name, String taskInfo, int id) {
        this.name = name;
        this.taskInfo = taskInfo;
        this.id = id;
        this.status = TaskStatus.NEW;
    }

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", taskInfo='" + taskInfo + '\'' +
                ", id=" + id +
                ", statys=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
