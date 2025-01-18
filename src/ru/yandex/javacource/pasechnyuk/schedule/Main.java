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


        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Переезд в новый дом", "Спланировать переезд в новый дом", 1));
        Subtask subtask1 = inMemoryTaskManager.createSubtask(new Subtask("Упаковка вещи",
                "Упаковать вещи в коробки, хрупкие вещи в пленку", 1, epic1.getId()));
        Subtask subtask2 = inMemoryTaskManager.createSubtask(new Subtask("Арендовать грузовик для перевозки вещей",
                "Позвонить в транспортные компании, узнать цены, заказать машину", 2, epic1.getId()));

        Epic epic2 = inMemoryTaskManager.createEpic(new Epic("Приготовить торт", "Приготовить торт по рецепту.", 2));
        Subtask subtask3 = inMemoryTaskManager.createSubtask(new Subtask("Купить продукты по рецепту",
                "Сходить за продуктами", 1, epic2.getId()));

        System.out.println("Задачи:" + inMemoryTaskManager.getTasks());

        System.out.println("\nЭпики:" + inMemoryTaskManager.getEpics());

        System.out.println("\nПодзадачи:" + inMemoryTaskManager.getSubtask());

        System.out.println("\nЗадача - Сделать чай :" + inMemoryTaskManager.getTaskById(task1.getId()));

        System.out.println("\nЭпик - Переезд в новый дом" + inMemoryTaskManager.getEpicById(epic1.getId()));

        System.out.println("\nПодзадача - Упаковка вещи:" + inMemoryTaskManager.getSubtaskById(subtask1.getId()));

        System.out.println("\nИстория просмотра задач: " + inMemoryTaskManager.getHistory());

        task1.setStatus(TaskStatus.DONE);

        inMemoryTaskManager.updateTask(task1);

        task2.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.updateTask(task2);

        subtask1.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateSubtask(subtask1);

        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(subtask2);

        subtask3.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateSubtask(subtask3);


        inMemoryTaskManager.updateEpicStatus(epic1);
        inMemoryTaskManager.updateEpicStatus(epic2);


        System.out.println("\nОбновлённые статусы задача 1:" + task1.getStatus());
        System.out.println("\nОбновлённые статусы задача 2:" + task2.getStatus());

        System.out.println("\nСтатус эпика Переезд в новый дом: " + epic1.getStatus());
        System.out.println("\nСтатус эпика Приготовить торт: " + epic2.getStatus());

        inMemoryTaskManager.deleteTaskById(task2.getId());
        inMemoryTaskManager.deleteEpicById(epic2.getId());
        inMemoryTaskManager.deleteSubtaskById(subtask2.getId());


        System.out.println("\nЗадачи после удаления :" + inMemoryTaskManager.getTasks());
        System.out.println("\nЭпики после удаления :" + inMemoryTaskManager.getEpics());
        System.out.println("\nПодзадачи после удаления :" + inMemoryTaskManager.getSubtask());


        inMemoryTaskManager.clearTasks();
        System.out.println(inMemoryTaskManager.getTasks());

        inMemoryTaskManager.clearSubtasks();
        System.out.println(inMemoryTaskManager.getSubtask());

        inMemoryTaskManager.clearEpics();
        System.out.println(inMemoryTaskManager.getEpics());

    }
}

