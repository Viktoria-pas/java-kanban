package ru.yandex.javacource.pasechnyuk.schedule;

import ru.yandex.javacource.pasechnyuk.schedule.manager.*;
import ru.yandex.javacource.pasechnyuk.schedule.task.Epic;
import ru.yandex.javacource.pasechnyuk.schedule.task.Subtask;
import ru.yandex.javacource.pasechnyuk.schedule.task.Task;
import ru.yandex.javacource.pasechnyuk.schedule.task.TaskStatus;

public class Main {
    public static void main(String[] args) {

        TaskManager inMemoryTaskManager = Managers.getDefault();

        Task task1 = inMemoryTaskManager.createTask(new Task("Сделать чай", "Необходимо сделать зеленый чай", 1));
        Task task2 = inMemoryTaskManager.createTask(new Task("Съездить за покупками", "Купить подарки к НГ", 2));


        Epic epic1 = (Epic) inMemoryTaskManager.createTask(new Epic("Переезд в новый дом", "Спланировать переезд в новый дом", 1));
        Subtask subtask1 = (Subtask) inMemoryTaskManager.createTask(new Subtask("Упаковка вещи",
                "Упаковать вещи в коробки, хрупкие вещи в пленку", 1, epic1.getId()));
        Subtask subtask2 = (Subtask) inMemoryTaskManager.createTask(new Subtask("Арендовать грузовик для перевозки вещей",
                "Позвонить в транспортные компании, узнать цены, заказать машину", 2, epic1.getId()));

        Epic epic2 = (Epic) inMemoryTaskManager.createTask(new Epic("Приготовить торт", "Приготовить торт по рецепту.", 2));
        Subtask subtask3 = (Subtask) inMemoryTaskManager.createTask(new Subtask("Купить продукты по рецепту",
                "Сходить за продуктами", 1, epic2.getId()));

        System.out.println("Задачи:" + inMemoryTaskManager.getTasks(Task.class));

        System.out.println("\nЭпики:" + inMemoryTaskManager.getTasks(Epic.class));

        System.out.println("\nПодзадачи:" + inMemoryTaskManager.getTasks(Subtask.class));

        System.out.println("\nЗадача - Сделать чай :" + inMemoryTaskManager.getTaskById(task1.getId()));

        System.out.println("\nЭпик - Переезд в новый дом" + inMemoryTaskManager.getTaskById(epic1.getId()));

        System.out.println("\nПодзадача - Упаковка вещи:" + inMemoryTaskManager.getTaskById(subtask1.getId()));

        System.out.println("\nИстория просмотра задач: " + inMemoryTaskManager.getHistory());

        task1.setStatus(TaskStatus.DONE);

        inMemoryTaskManager.updateTask(task1);

        task2.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.updateTask(task2);

        subtask1.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateTask(subtask1);

        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.updateTask(subtask2);

        subtask3.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateTask(subtask3);


        inMemoryTaskManager.updateEpicStatus(epic1);
        inMemoryTaskManager.updateEpicStatus(epic2);


        System.out.println("\nОбновлённые статусы задача 1:" + task1.getStatus());
        System.out.println("\nОбновлённые статусы задача 2:" + task2.getStatus());

        System.out.println("\nСтатус эпика Переезд в новый дом: " + epic1.getStatus());
        System.out.println("\nСтатус эпика Приготовить торт: " + epic2.getStatus());

        inMemoryTaskManager.deleteTaskById(task2.getId());
        inMemoryTaskManager.deleteTaskById(epic2.getId());
        inMemoryTaskManager.deleteTaskById(subtask2.getId());


        System.out.println("\nЗадачи после удаления :" + inMemoryTaskManager.getTasks(Task.class));
        System.out.println("\nЭпики после удаления :" + inMemoryTaskManager.getTasks(Epic.class));
        System.out.println("\nПодзадачи после удаления :" + inMemoryTaskManager.getTasks(Subtask.class));


        inMemoryTaskManager.clearTasks(Task.class);
        System.out.println(inMemoryTaskManager.getTasks(Task.class));

        inMemoryTaskManager.clearTasks(Epic.class);
        System.out.println(inMemoryTaskManager.getTasks(Epic.class));

        inMemoryTaskManager.clearTasks(Subtask.class);
        System.out.println(inMemoryTaskManager.getTasks(Subtask.class));


    }
}

