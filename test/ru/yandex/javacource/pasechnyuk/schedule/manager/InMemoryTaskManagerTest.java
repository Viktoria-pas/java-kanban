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

    @Test
    void canNotaddMoreThan10tasksToHistory(){
        Task task1 = inMemoryTaskManager.createTask(new Task("Сделать чай", "Необходимо сделать зеленый чай", 1));
        Task task2 = inMemoryTaskManager.createTask(new Task("Съездить за покупками", "Купить подарки к НГ", 2));
        Task task3 = inMemoryTaskManager.createTask(new Task("Приготовить обед", "Сварить суп", 3));
        Task task4 = inMemoryTaskManager.createTask(new Task("Съездить на работу", "Забрать бумаги", 4));
        Task task5 = inMemoryTaskManager.createTask(new Task("Приготовить ужин", "Хаказать доставку", 5));
        Task task6 = inMemoryTaskManager.createTask(new Task("Постирать белье", "Постирать постельное", 6));


        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Переезд в новый дом", "Спланировать переезд в новый дом", 1));
        Subtask subtask1 = inMemoryTaskManager.createSubtask(new Subtask("Упаковка вещи",
                "Упаковать вещи в коробки, хрупкие вещи в пленку", 1, epic1.getId()));
        Subtask subtask2 =  inMemoryTaskManager.createSubtask(new Subtask("Арендовать грузовик для перевозки вещей",
                "Позвонить в транспортные компании, узнать цены, заказать машину", 2, epic1.getId()));

        Epic epic2 = inMemoryTaskManager.createEpic(new Epic("Приготовить торт", "Приготовить торт по рецепту.", 2));
        Subtask subtask3 = inMemoryTaskManager.createSubtask(new Subtask("Купить продукты по рецепту",
                "Сходить за продуктами", 1, epic2.getId()));
        inMemoryTaskManager.getTaskById(task1.getId());
        inMemoryTaskManager.getTaskById(task2.getId());
        inMemoryTaskManager.getTaskById(task3.getId());
        inMemoryTaskManager.getTaskById(task4.getId());
        inMemoryTaskManager.getTaskById(task5.getId());
        inMemoryTaskManager.getEpicById(epic1.getId());
        inMemoryTaskManager.getSubtaskById(subtask1.getId());
        inMemoryTaskManager.getSubtaskById(subtask2.getId());
        inMemoryTaskManager.getEpicById(epic2.getId());
        inMemoryTaskManager.getSubtaskById(subtask3.getId());
        inMemoryTaskManager.getTaskById(task6.getId());
        assertEquals(10, inMemoryTaskManager.getHistory().size());
    }

    @Test
    void oldestTaskInHistoryWillChangeIfAddMoreThan10Tasks(){
        Task task1 = inMemoryTaskManager.createTask(new Task("Сделать чай", "Необходимо сделать зеленый чай", 1));
        Task task2 = inMemoryTaskManager.createTask(new Task("Съездить за покупками", "Купить подарки к НГ", 2));
        Task task3 = inMemoryTaskManager.createTask(new Task("Приготовить обед", "Сварить суп", 3));
        Task task4 = inMemoryTaskManager.createTask(new Task("Съездить на работу", "Забрать бумаги", 4));
        Task task5 = inMemoryTaskManager.createTask(new Task("Приготовить ужин", "Хаказать доставку", 5));
        Task task6 = inMemoryTaskManager.createTask(new Task("Постирать белье", "Постирать постельное", 6));


        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Переезд в новый дом", "Спланировать переезд в новый дом", 1));
        Subtask subtask1 = inMemoryTaskManager.createSubtask(new Subtask("Упаковка вещи",
                "Упаковать вещи в коробки, хрупкие вещи в пленку", 1, epic1.getId()));
        Subtask subtask2 =  inMemoryTaskManager.createSubtask(new Subtask("Арендовать грузовик для перевозки вещей",
                "Позвонить в транспортные компании, узнать цены, заказать машину", 2, epic1.getId()));

        Epic epic2 = inMemoryTaskManager.createEpic(new Epic("Приготовить торт", "Приготовить торт по рецепту.", 2));
        Subtask subtask3 = inMemoryTaskManager.createSubtask(new Subtask("Купить продукты по рецепту",
                "Сходить за продуктами", 1, epic2.getId()));
        inMemoryTaskManager.getTaskById(task1.getId());
        inMemoryTaskManager.getTaskById(task2.getId());
        inMemoryTaskManager.getTaskById(task3.getId());
        inMemoryTaskManager.getTaskById(task4.getId());
        inMemoryTaskManager.getTaskById(task5.getId());
        inMemoryTaskManager.getEpicById(epic1.getId());
        inMemoryTaskManager.getSubtaskById(subtask1.getId());
        inMemoryTaskManager.getSubtaskById(subtask2.getId());
        inMemoryTaskManager.getEpicById(epic2.getId());
        inMemoryTaskManager.getSubtaskById(subtask3.getId());
        inMemoryTaskManager.getTaskById(task6.getId());
        assertTrue(inMemoryTaskManager.getHistory().contains(task6));
        assertFalse(inMemoryTaskManager.getHistory().contains(task1));
    }





}