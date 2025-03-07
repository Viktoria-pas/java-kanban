package ru.yandex.javacource.pasechnyuk.schedule.manager;

import ru.yandex.javacource.pasechnyuk.schedule.task.Epic;
import ru.yandex.javacource.pasechnyuk.schedule.task.Subtask;
import ru.yandex.javacource.pasechnyuk.schedule.task.Task;
import ru.yandex.javacource.pasechnyuk.schedule.task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {

    protected int currentId = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();


    @Override
    public Task createTask(Task task) {
        add(task);
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        return epic;
    }


    @Override
    public Subtask createSubtask(Subtask subtask) {
        add(subtask);
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        if (subtask.getId() == subtask.getEpicId()) {
            return null;
        }
        int id = generateId();
        subtask.setId(id);
        subtasks.put(id, subtask);
        epic.addSubtaskId(subtask.getId());//сделай метод
        updateEpicStatus(epic);
        return subtask;
    }


    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearSubtasks() {
        epics.values().forEach(epic -> {
            epic.cleanSubtask();
            updateEpicStatus(epic);
        });
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }


    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtasks.get(id);
    }

    @Override
    public void updateTask(Task task) {
        add(task);
        final Task savedTask = tasks.get(task.getId());
        if (savedTask == null) {
            System.out.println("Задача с таким ID не найдена.");
            return;
        }
        task.setStatus(savedTask.getStatus());
        tasks.put(task.getId(), task);
    }


    @Override
    public void updateEpic(Epic epic) {
        final Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        epic.setSubtaskIds(savedEpic.getSubtaskIds());
        epic.setStatus(savedEpic.getStatus());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        add(subtask);
        final int id = subtask.getId();
        final int epicId = subtask.getEpicId();
        final Subtask savedSubtask = subtasks.get(id);
        if (savedSubtask == null) {
            return;
        }
        final Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        subtasks.put(id, subtask);
        updateEpicStatus(epic);
    }

    public void updateEpicTime(Epic epic) {
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(null);
            return;
        }

        Optional<LocalDateTime> earliestStart = epic.getSubtaskIds().stream()
                .map(this::getSubtaskById)
                .filter(Objects::nonNull)
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        Optional<LocalDateTime> latestEnd = epic.getSubtaskIds().stream()
                .map(this::getSubtaskById)
                .filter(Objects::nonNull)
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo);

        if (earliestStart.isPresent() && latestEnd.isPresent()) {
            epic.setStartTime(earliestStart.get());
            epic.setDuration(Duration.between(earliestStart.get(), latestEnd.get()));
        } else {
            epic.setStartTime(null);
            epic.setDuration(null);
        }
    }


    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        final Epic epic = epics.remove(id);
        if (epic == null) {
            return;
        }
        historyManager.remove(id);
        epic.getSubtaskIds().forEach(subtaskId -> {
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        });
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        historyManager.remove(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtaskId(id);
        updateEpicStatus(epic);
    }


    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        return subtasks.values().stream()
                .filter(subtask -> epicId == subtask.getEpicId())
                .collect(Collectors.toCollection(ArrayList::new));
    }


    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(Epic epic) {
        List<Subtask> subtasks = getEpicSubtasks(epic.getId());

        if (subtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (subtasks.stream().allMatch(subtask -> subtask.getStatus() == TaskStatus.NEW)) {
            epic.setStatus(TaskStatus.NEW);
        } else if (subtasks.stream().allMatch(subtask -> subtask.getStatus() == TaskStatus.DONE)) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }

        updateEpicTime(epic);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        TreeSet<Task> prioritizedTasks = new TreeSet<>(
                Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Task::getId)
        );

        tasks.values().stream()
                .filter(task -> task.getStartTime() != null)
                .forEach(prioritizedTasks::add);

        subtasks.values().stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .forEach(prioritizedTasks::add);

        return new ArrayList<>(prioritizedTasks);
    }

    protected void add(Task newTask) throws IllegalArgumentException {
        if (newTask.getStartTime() == null || newTask.getEndTime() == null) {
            return;
        }

        boolean hasOverlap = getPrioritizedTasks().stream()
                .filter(existingTask -> !existingTask.equals(newTask))
                .filter(existingTask -> existingTask.getStartTime() != null)
                .anyMatch(existingTask -> isOnlyOneTaskInTime(newTask, existingTask));

        if (hasOverlap) {
            throw new IllegalArgumentException("Задача пересекается по времени с существующей задачей или подзадачей.");
        }
    }

    protected boolean isOnlyOneTaskInTime(Task task1, Task task2) {
        if (task1.getStartTime() == null || task1.getEndTime() == null ||
                task2.getStartTime() == null || task2.getEndTime() == null) {
            return false;
        }
        return task1.getStartTime().isBefore(task2.getEndTime()) &&
                task2.getStartTime().isBefore(task1.getEndTime());
    }

    protected int generateId() {
        return ++currentId;
    }


}

