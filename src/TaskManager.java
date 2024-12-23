import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
   private int currentId = 0;
   private HashMap<Integer, Task> tasks = new HashMap<>();

    public int generateId() {
        return ++currentId;
    }

    public Task createTask(String name, String taskInfo) {
        int id = generateId();
        Task task = new Task(name, taskInfo, id);
        tasks.put(id, task);
        return task;
    }

    public Epic createEpic(String name, String taskInfo) {
        int id = generateId();
        Epic epic = new Epic( name, taskInfo, id);
        tasks.put(id, epic);
        return epic;
    }


    public Subtask createSubtask(String name, String taskInfo, Epic epic) {
        int id = generateId();
        Subtask subtask = new Subtask( name, taskInfo, id, epic);
        tasks.put(id, subtask);
        return subtask;
    }

    public ArrayList<Task> getAllTasks(){
        ArrayList<Task> allTasks = new ArrayList<>();
        for(Task task : tasks.values()){
            allTasks.add(task);
        }
        return allTasks;
    }

    public void clearTasks(){
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Задача с таким ID не найдена.");
        }
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public ArrayList<Task> getSubtasksForEpic(Epic epic) {
        ArrayList<Task> subtasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task.getClass().equals(Subtask.class) && ((Subtask) task).getEpic().equals(epic)) {  // 3
                subtasks.add(task);
            }
        }
        return subtasks;
    }

    public void updateEpicStatus(Epic epic) {
        ArrayList<Task> subtasks = getSubtasksForEpic(epic);
        boolean allNew = true;
        boolean allDone = true;

        for (Task subtask : subtasks) {
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
        }

        if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

}
