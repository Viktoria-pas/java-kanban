package ru.yandex.javacource.pasechnyuk.schedule.task;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String taskInfo, int id, int epicId) {
        super(name, taskInfo, id);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }


    @Override
    public String toString() {
        return "Subtask{" + "name=" + getName() + ", taskInfo=" + getTaskInfo() + ", id=" + getId() +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                '}';
    }
}
