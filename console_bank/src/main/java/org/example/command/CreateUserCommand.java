package org.example.command;

import org.example.constants.ConsoleOperationType;
import org.example.service.AccountService;
import org.example.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class CreateUserCommand implements OperationCommand {

    public final UserService userService;
    public final AccountService accountService;
    private final Scanner scanner;

    public CreateUserCommand(UserService userService,
                             AccountService accountService,
                             Scanner scanner) {
        this.scanner = scanner;
        this.userService = userService;
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        System.out.println("Придумайте имя учётной записи");
        String login = scanner.nextLine();
        try {
            var newUser = userService.createUser(login);
            var initAccount = accountService.create(newUser.getId());
            userService.addAccountToUser(initAccount, newUser);
            System.out.println("Пользователь создан");
            System.out.println(newUser);
        } catch (IllegalArgumentException e) {
            System.out.printf("Имя учётной записи занято: %s%n", login);
        }
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.USER_CREATE;
    }
}
