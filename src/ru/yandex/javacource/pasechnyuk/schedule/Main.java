package ru.yandex.javacource.pasechnyuk.schedule;

import ru.yandex.javacource.pasechnyuk.schedule.manager.*;
import ru.yandex.javacource.pasechnyuk.schedule.task.Epic;
import ru.yandex.javacource.pasechnyuk.schedule.task.Subtask;
import ru.yandex.javacource.pasechnyuk.schedule.task.Task;

import java.io.File;
import java.io.IOException;

public class Main {
//    public static void main(String[] args) {
//
//        TaskManager inMemoryTaskManager = Managers.getDefault();
//
//        Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Переезд в новый дом", "Спланировать переезд в новый дом", 1));
//        Subtask subtask1 = inMemoryTaskManager.createSubtask(new Subtask("Упаковка вещи",
//                "Упаковать вещи в коробки, хрупкие вещи в пленку", 2, epic1.getId()));
//        Subtask subtask2 = inMemoryTaskManager.createSubtask(new Subtask("Арендовать грузовик для перевозки вещей",
//                "Позвонить в транспортные компании, узнать цены, заказать машину", 2, epic1.getId()));
//        Subtask subtask3 = inMemoryTaskManager.createSubtask(new Subtask("Разгрузить грузовик",
//                "Дождаться грузовига в новом доме", 3, epic1.getId()));
//
//        Epic epic2 = inMemoryTaskManager.createEpic(new Epic("Приготовить торт", "Приготовить торт по рецепту.", 2));
//
//        inMemoryTaskManager.getSubtaskById(subtask1.getId());
//        inMemoryTaskManager.getEpicById(epic1.getId());
//        inMemoryTaskManager.getSubtaskById(subtask2.getId());
//        inMemoryTaskManager.getSubtaskById(subtask3.getId());
//        System.out.println(inMemoryTaskManager.getHistory());
//
//        inMemoryTaskManager.getSubtaskById(subtask1.getId());
//        System.out.println(inMemoryTaskManager.getHistory());
//
//        inMemoryTaskManager.deleteSubtaskById(subtask3.getId());
//        System.out.println(inMemoryTaskManager.getHistory());
//
//        inMemoryTaskManager.deleteEpicById(epic1.getId());
//        inMemoryTaskManager.getEpicById(epic2.getId());
//        System.out.println(inMemoryTaskManager.getHistory());
//
//
//    }

}

