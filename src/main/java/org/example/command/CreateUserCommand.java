package org.example.command;

import org.example.constants.ConsoleOperationType;
import org.example.model.User;
import org.example.service.UserService;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Scanner;

@Component
public class CreateUserCommand implements OperationCommand {

    public final UserService userService;
    private final Scanner scanner;

    public CreateUserCommand(UserService userService,
                             Scanner scanner) {
        this.scanner = scanner;
        this.userService = userService;
    }

    @Override
    public void execute() {
        System.out.println("Придумайте имя учётной записи");
        String login = scanner.nextLine();
        try {
            User newUser = userService.createUser(login);
            System.out.println("Пользователь создан");
            System.out.println(newUser);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.USER_CREATE;
    }
}
