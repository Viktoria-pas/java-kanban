package ru.yandex.javacource.pasechnyuk.schedule.manager;

import ru.yandex.javacource.pasechnyuk.schedule.task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.reload();
        return manager;
    }

    protected void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bw.write("id,type,name,status,description,epic\n");
            for (Task task : getTasks()) {
                bw.write(toString(task) + "\n");
            }
            for (Epic epic : getEpics()) {
                bw.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getSubtask()) {
                bw.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении задач в файл", e);
        }
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
    public ArrayList<Task> getTasks() {
        return super.getTasks();
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return super.getEpics();
    }

    @Override
    public ArrayList<Subtask> getSubtask() {
        return super.getSubtask();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
    }

    @Override
    public Task getTaskById(int id) {
        return super.getTaskById(id);
    }

    @Override
    public Epic getEpicById(int id) {
        return super.getEpicById(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        return super.getSubtaskById(id);
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

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        return super.getEpicSubtasks(epicId);
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    private String toString(Task task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,%s,%s,%s,%s,%d",
                    subtask.getId(), TaskType.SUBTASK, subtask.getName(), subtask.getStatus(), subtask.getDescription(), subtask.getEpicId());
        } else if (task instanceof Epic) {
            return String.format("%d,%s,%s,%s,%s,",
                    task.getId(), TaskType.EPIC, task.getName(), task.getStatus(), task.getDescription());
        } else {
            return String.format("%d,%s,%s,%s,%s,",
                    task.getId(), TaskType.TASK, task.getName(), task.getStatus(), task.getDescription());
        }

    }

    private static Task fromString(String value) {
        String[] split = value.split(",");
        int id = Integer.parseInt(split[0]);
        TaskType type = TaskType.valueOf(split[1]);
        String name = split[2];
        TaskStatus status = TaskStatus.valueOf(split[3]);
        String description = split[4];

        switch (type) {
            case TASK:
                return new Task(name, description, id);
            case EPIC:
                return new Epic(name, description, id);
            case SUBTASK:
                int epicId = Integer.parseInt(split[5]);
                return new Subtask(name, description, id, epicId);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    private void reload() {
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {

            br.readLine();

            while (br.ready()) {
                String line = br.readLine();
                if (line != null && !line.isEmpty()) {
                    Task task = fromString(line);
                    if (task instanceof Epic) {
                        epics.put(task.getId(), (Epic) task);
                    } else if (task instanceof Subtask) {
                        subtasks.put(task.getId(), (Subtask) task);
                    } else {
                        tasks.put(task.getId(), task);
                    }
                }
            }
            for (Subtask subtask : subtasks.values()) {
                Epic epic = epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.addSubtaskId(subtask.getId());
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении задач из файла", e);
        }
    }
    public static void main(String[] args) throws IOException {
        File file = File.createTempFile("test", ".csv");

        FileBackedTaskManager manager1 = new FileBackedTaskManager(file);
        Task task = manager1.createTask(new Task("Task 1", "Description 1", 1));
        Epic epic = manager1.createEpic(new Epic("Epic 1", "Description Epic 1", 2));
        Subtask subtask = manager1.createSubtask(new Subtask("Subtask 1", "Description Subtask 1", 3, epic.getId()));

        FileBackedTaskManager manager2 =FileBackedTaskManager.loadFromFile(file);
        System.out.println(manager1.getTaskById(task.getId()).equals(manager2.getTaskById(task.getId())));
        System.out.println(manager1.getEpicById(epic.getId()).equals(manager2.getEpicById(epic.getId())));
        System.out.println(manager1.getSubtaskById(subtask.getId()).equals(manager2.getSubtaskById(subtask.getId())));
        System.out.println("Менеджер 1: "+ manager1.toString(manager1.getTaskById(task.getId())));
        System.out.println("Менеджер 2: "+manager2.toString(manager2.getTaskById(task.getId())));

    }

}
