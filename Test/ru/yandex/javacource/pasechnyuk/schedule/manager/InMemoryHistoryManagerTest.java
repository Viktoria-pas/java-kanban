package ru.yandex.javacource.pasechnyuk.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.pasechnyuk.schedule.task.Epic;
import ru.yandex.javacource.pasechnyuk.schedule.task.Subtask;
import ru.yandex.javacource.pasechnyuk.schedule.task.Task;
import ru.yandex.javacource.pasechnyuk.schedule.task.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    TaskManager inMemoryTaskManager;
    @BeforeEach
    void setUp() {
        inMemoryTaskManager = Managers.getDefault();
    }
    @Test
    void cheakTaskWasAddedToHistory(){
        Task task1 = (Task) inMemoryTaskManager.createTask(new Task("Сделать чай",
                "Необходимо сделать зеленый чай", 1));
        Epic epic1 = (Epic) inMemoryTaskManager.createTask(new Epic("Переезд в новый дом",
                "Спланировать переезд в новый дом", 2));
        Subtask subtask1 = (Subtask) inMemoryTaskManager.createTask(new Subtask("Упаковка вещи",
                "Упаковать вещи в коробки, хрупкие вещи в пленку", 1, epic1.getId()));
        inMemoryTaskManager.getTaskById(task1.getId());
        inMemoryTaskManager.getTaskById(epic1.getId());
        inMemoryTaskManager.getTaskById(subtask1.getId());
        assertTrue(inMemoryTaskManager.getHistory().contains(task1));
        assertTrue(inMemoryTaskManager.getHistory().contains(epic1));
        assertTrue(inMemoryTaskManager.getHistory().contains(subtask1));
    }
    @Test
    void checkTaskHistoryPreservesPreviousState() {
        Task task1 = (Task) inMemoryTaskManager.createTask(new Task("Сделать чай",
                "Необходимо сделать зеленый чай", 1));
        inMemoryTaskManager.getTaskById(task1.getId());

        Task originalTask = new Task(task1.getName(), task1.getDescription(), task1.getId());
        originalTask.setStatus(task1.getStatus());

        task1.setDescription("Сделать травяной чай");
        task1.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.updateTask(task1);

        Task historyTask = (Task) inMemoryTaskManager.getHistory().get(0);
        assertEquals(originalTask.getName(), historyTask.getName(), "Название задачи в истории изменилось");
        assertEquals(originalTask.getDescription(), historyTask.getDescription(), "Описание задачи в истории изменилось");
        assertEquals(originalTask.getStatus(), historyTask.getStatus(), "Статус задачи в истории изменился");
        assertEquals(originalTask.getId(), historyTask.getId(), "ID задачи в истории изменился");
    }


}