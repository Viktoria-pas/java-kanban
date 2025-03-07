package ru.yandex.javacource.pasechnyuk.schedule.task;
import java.time.*;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int id, int epicId, LocalDateTime startTime, Duration duration ) {
        super(name, description, id, startTime, duration);
        this.epicId = epicId;
    }

    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" + "name=" + getName() + ", description=" + getDescription() + ", id=" + getId() +
                ", status=" + getStatus() +
                ", duration=" + getDuration()+
                ", startTime=" + getStartTime() +
                ", epicId=" + epicId +
                '}';
    }

}
