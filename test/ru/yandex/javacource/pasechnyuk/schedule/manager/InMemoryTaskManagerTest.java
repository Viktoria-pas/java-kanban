package ru.yandex.javacource.pasechnyuk.schedule.manager;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.pasechnyuk.schedule.task.Epic;
import ru.yandex.javacource.pasechnyuk.schedule.task.Subtask;
import ru.yandex.javacource.pasechnyuk.schedule.task.Task;

class InMemoryTaskManagerTest {


    TaskManager inMemoryTaskManager;
    @BeforeEach
    void setUp() {
        inMemoryTaskManager = Managers.getDefault();
    }

    @Test
    void addDifferentTypesOfTasks(){
        Task task1 = inMemoryTaskManager.createTask(new Task("Сделать чай",
                "Необходимо сделать зеленый чай", 1));
        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Переезд в новый дом",
                "Спланировать переезд в новый дом", 2));
        Subtask subtask1 = inMemoryTaskManager.createSubtask(new Subtask("Упаковка вещи",
                "Упаковать вещи в коробки, хрупкие вещи в пленку", 1, epic1.getId()));
        assertEquals(task1, inMemoryTaskManager.getTaskById(task1.getId()));
        assertEquals(epic1, inMemoryTaskManager.getEpicById(epic1.getId()));
        assertEquals(subtask1, inMemoryTaskManager.getSubtaskById(subtask1.getId()));
    }

    @Test
    void taskWithNonConflictingId_CreatesSuccessfully() {
        Task task1 = new Task("Первая задача", "Описание", 1);
        Task task2 = new Task("Вторая задача", "Описание", 2);

        inMemoryTaskManager.createTask(task1);
        Task createdTask = inMemoryTaskManager.createTask(task2);

        assertNotNull(createdTask, "Задача не создана");
        assertEquals(2, createdTask.getId(), "ID не совпадает с заданным");
    }

    @Test
    void noChangeEtemsAfterCreated(){
            Task originalTask = new Task("Сделать чай",
                "Необходимо сделать зеленый чай", 1);

            Task createdTask = inMemoryTaskManager.createTask(originalTask);

            assertEquals(originalTask.getId(), createdTask.getId(),
                    "ID задачи изменился при добавлении");
            assertEquals(originalTask.getName(), createdTask.getName(),
                    "Название задачи изменилось при добавлении");
            assertEquals(originalTask.getDescription(), createdTask.getDescription(),
                    "Описание задачи изменилось при добавлении");
            assertEquals(originalTask.getStatus(), createdTask.getStatus(),
                    "Статус задачи изменился при добавлении");
        }








}