package org.example.command;

import org.example.constants.ConsoleOperationType;
import org.example.service.AccountService;
import org.example.service.UserService;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Scanner;

@Component
public class AccountTransferCommand implements OperationCommand {

    public final UserService userService;
    public final AccountService accountService;
    private final Scanner scanner;

    public AccountTransferCommand(UserService userService,
                                  AccountService accountService,
                                  Scanner scanner) {
        this.scanner = scanner;
        this.userService = userService;
        this.accountService = accountService;
    }

    @Override
    public void execute() {
        System.out.println("Введите id счёта для списания: ");
        int idAccountFrom = Integer.parseInt(scanner.nextLine());
        try {
            var accountFrom = accountService.findAccountById(idAccountFrom);
            System.out.println("Введите id счёта для пополнения: ");
            int idAccountTo = Integer.parseInt(scanner.nextLine());
            try {
                var accountTo = accountService.findAccountById(idAccountTo);
                System.out.println("Введите сумму для пополнения: ");
                long amount = Long.parseLong((scanner.nextLine()));
                try {
                    accountService.transfer(accountFrom, accountTo, amount);
                    System.out.println("Деньги успешно переведены");
                    System.out.println(accountFrom);
                    System.out.println(accountTo);
                } catch (IllegalArgumentException e) {
                    System.out.printf("Не хватает денег на счёте списания: id=%s," +
                                    " баланс на счету=%s, введённая сумма=%s%n",
                            accountFrom.getId(), accountFrom.getMoneyAmount(),
                            amount);
                }
            } catch (NoSuchElementException e) {
                System.out.printf("счёт с таким id не найден : %s%n", idAccountTo);
            }
        } catch (NoSuchElementException e) {
            System.out.printf("счёт с таким id не найден : %s%n", idAccountFrom);
        }
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_TRANSFER;
    }
}
