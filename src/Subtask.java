public class Subtask extends Task {
    Epic epic;

    public Subtask(String name, String taskInfo, int id, Epic epic) {
        super(name, taskInfo, id);
        this.epic = epic;
        epic.addSubtask(this);
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public String toString() {
        return super.toString() + ", epicId=" + epic.getId();
    }
}
