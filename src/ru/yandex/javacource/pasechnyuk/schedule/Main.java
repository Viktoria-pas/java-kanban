package ru.yandex.javacource.pasechnyuk.schedule;

import ru.yandex.javacource.pasechnyuk.schedule.manager.TaskManager;
import ru.yandex.javacource.pasechnyuk.schedule.task.Epic;
import ru.yandex.javacource.pasechnyuk.schedule.task.Subtask;
import ru.yandex.javacource.pasechnyuk.schedule.task.Task;
import ru.yandex.javacource.pasechnyuk.schedule.task.TaskStatus;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();


        Task task1 = taskManager.createTask(new Task("Сделать чай", "Необходимо сделать зеленый чай", 1));
        Task task2 = taskManager.createTask(new Task("Съездить за покупками", "Купить подарки к НГ", 2));


        Epic epic1 = taskManager.createEpic(new Epic("Переезд в новый дом", "Спланировать переезд в новый дом", 1));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Упаковка вещи",
                "Упаковать вещи в коробки, хрупкие вещи в пленку", 1, epic1.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Арендовать грузовик для перевозки вещей",
                "Позвонить в транспортные компании, узнать цены, заказать машину", 2, epic1.getId()));

        Epic epic2 = taskManager.createEpic(new Epic("Приготовить торт", "Приготовить торт по рецепту.", 2));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Купить продукты по рецепту",
                "Сходить за продуктами", 1, epic2.getId()));

        System.out.println("Задачи:" + taskManager.getTasks());

        System.out.println("\nЭпики:" + taskManager.getEpics());

        System.out.println("\nПодзадачи:" + taskManager.getSubtask());

        System.out.println("\nЗадача - Сделать чай :" + taskManager.getTaskById(1));

        System.out.println("\nЭпик - Переезд в новый дом" + taskManager.getEpicById(3));

        System.out.println("\nПодзадача - Упаковка вещи:" + taskManager.getSubtaskById(4));

        task1.setStatus(TaskStatus.DONE);
        taskManager.updateTask(task1);

        task2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task2);

        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);

        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);

        subtask3.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask3);


        taskManager.updateEpicStatus(epic1);
        taskManager.updateEpicStatus(epic2);


        System.out.println("\nОбновлённые статусы задача 1:" + task1.getStatus());
        System.out.println("\nОбновлённые статусы задача 2:" + task2.getStatus());

        System.out.println("\nСтатус эпика Переезд в новый дом: " + epic1.getStatus());
        System.out.println("\nСтатус эпика Приготовить торт: " + epic2.getStatus());

        System.out.println("\n" + taskManager.getTaskById(1));
        System.out.println("\n" + taskManager.getEpicById(3));
        System.out.println("\n" + taskManager.getSubtaskById(4));

        taskManager.deleteTaskById(task2.getId());
        taskManager.deleteEpicById(epic2.getId());
        taskManager.deleteSubtaskById(subtask2.getId());


        System.out.println("\nЗадачи после удаления :" + taskManager.getTasks());
        System.out.println("\nЭпики после удаления :" + taskManager.getEpics());
        System.out.println("\nПодзадачи после удаления :" + taskManager.getSubtask());


        taskManager.clearTasks();
        System.out.println(taskManager.getTasks());

        taskManager.clearEpics();
        System.out.println(taskManager.getEpics());

        taskManager.clearSubtasks();
        System.out.println(taskManager.getSubtask());


    }
}

