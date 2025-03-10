package ru.yandex.javacource.pasechnyuk.schedule.manager;

import ru.yandex.javacource.pasechnyuk.schedule.task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.time.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    private static final String HEADER = "id,type,name,status,description, startTime, duration, epic";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }


    public static FileBackedTaskManager loadFromFile(File file) {
        final FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.size() < 2) {
                return taskManager;
            }

            taskManager.currentId = lines.stream()
                    .skip(1)
                    .filter(line -> !line.isEmpty())
                    .map(FileBackedTaskManager::fromString)
                    .peek(task -> taskManager.addAnyTask(task))
                    .mapToInt(Task::getId)
                    .max()
                    .orElse(0);

            taskManager.subtasks.values().forEach(subtask ->
                    taskManager.epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId()));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла: " + file.getName(), e);
        }

        return taskManager;
    }


    protected void addAnyTask(Task task) {
        final int id = task.getId();
        switch (task.getTaskType()) {
            case TASK:
                tasks.put(id, task);
                break;
            case SUBTASK:
                subtasks.put(id, (Subtask) task);
                break;
            case EPIC:
                epics.put(id, (Epic) task);
                break;
        }
    }

    protected static String toString(Task task) {
        return task.getId() + "," + task.getTaskType() + "," + task.getName() + "," + task.getStatus() + "," +
                task.getDescription() + "," + (task.getStartTime() != null ? task.getStartTime() : "") + "," +
                (task.getDuration() != null ? task.getDuration().toMinutes() : "") + ","
                + (task.getTaskType().equals(TaskType.SUBTASK) ? ((Subtask) task).getEpicId() : "");
    }


    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask newSubtask = super.createSubtask(subtask);
        save();
        return newSubtask;
    }


    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bw.write(HEADER);
            bw.newLine();

            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                bw.write(toString(entry.getValue()));
                bw.newLine();
            }

            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                bw.write(toString(entry.getValue()));
                bw.newLine();
            }

            for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                bw.write(toString(entry.getValue()));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении задач в файл", e);
        }
    }


    private static Task fromString(String value) {
        String[] split = value.split(",");
        int id = Integer.parseInt(split[0]);
        TaskType type = TaskType.valueOf(split[1]);
        String name = split[2];
        TaskStatus status = TaskStatus.valueOf(split[3]);
        String description = split[4];
        LocalDateTime startTime = split[5].isEmpty() ? null : LocalDateTime.parse(split[5]);
        Duration duration = split[6].isEmpty() ? null : Duration.ofMinutes(Long.parseLong(split[6]));

        switch (type) {
            case TASK:
                Task task = new Task(name, description, id, startTime, duration);
                task.setStatus(status);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description, id);
                epic.setStatus(status);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(split[7]);
                Subtask subtask = new Subtask(name, description, id, epicId, startTime, duration);
                subtask.setStatus(status);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }


    public static void main(String[] args) throws IOException {

        File file = File.createTempFile("test", ".csv");

        FileBackedTaskManager manager1 = new FileBackedTaskManager(file);
        Task task = manager1.createTask(new Task("Task 1", "Description 1", 1,
                LocalDateTime.of(2025, 5, 2, 10, 0), Duration.ofMinutes(60)));
        Epic epic = manager1.createEpic(new Epic("Epic 1", "Description Epic 1", 2));
        Subtask subtask = manager1.createSubtask(new Subtask("Subtask 1", "Description Subtask 1", 3, epic.getId(),
                LocalDateTime.of(2025, 5, 21, 18, 45), Duration.ofMinutes(60)));

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);

        System.out.println(manager1.getTaskById(task.getId()).equals(manager2.getTaskById(task.getId())));
        System.out.println(manager1.getEpicById(epic.getId()).equals(manager2.getEpicById(epic.getId())));
        System.out.println(manager1.getSubtaskById(subtask.getId()).equals(manager2.getSubtaskById(subtask.getId())));
        System.out.println("Менеджер 1: " + manager1.toString(manager1.getSubtaskById(subtask.getId())));
        System.out.println("Менеджер 2: " + manager2.toString(manager2.getSubtaskById(subtask.getId())));

    }

}
