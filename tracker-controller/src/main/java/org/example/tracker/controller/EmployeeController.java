package org.example.tracker.controller;

import org.example.tracker.dto.employee.EmployeeReq;
import org.example.tracker.dto.employee.EmployeeResp;
import org.example.tracker.dto.employee.EmployeeUpdateReq;

import java.util.List;

public class EmployeeController {

    /**
     * Создание профиля сотрудника. При создании сотрудника должна создаваться карточка сотрудника
     * с перечисленным выше набором атрибутов, также статус сотрудника становится Активный.
     * @param request объект с данными для создания
     */
    public EmployeeResp create(EmployeeReq request) {
        return null;
    }

    /**
     * Изменение сотрудника. При редактировании сотрудника должны редактировать поля профиля сотрудника.
     * Удаленного сотрудника изменить нельзя.
     * @param request объект с данными для обновления
     */
    public EmployeeResp update(EmployeeUpdateReq request) {
        return null;
    }

    /**
     * Удаление сотрудника. При удалении сотрудника, сотрудник переводится в статус Удаленный.
     * @param id идентификатор профиля
     */
    public void delete(Integer id) {
    }

    /**
     * Поиск сотрудников. Поиск осуществляется по текстовому значению,
     * которое проверяется по атрибутам Фамилия, Имя, Отчество, учетной записи,
     * адресу электронной почты и только среди активных сотрудников.
     * @param query поисковый запрос
     */
    public List<EmployeeResp> find(String query) {
        return null;
    }

    /**
     * Получение карточки сотрудника либо по идентификатору профиля, либо по УЗ
     * @param id идентификатор или УЗ
     */
    public EmployeeResp get(String id) {
        return null;
    }
}
