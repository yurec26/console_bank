package org.example.command;

import org.example.constants.ConsoleOperationType;
import org.example.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class ShowAllUsersCommand implements OperationCommand {

    private final UserService userService;


    public ShowAllUsersCommand(UserService userService) {
        this.userService = userService;

    }

    @Override
    public void execute() {
        System.out.println("Список всех существующих аккаунтов: ");
        userService.getAll().forEach(System.out::println);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.SHOW_ALL_USERS;
    }
}
