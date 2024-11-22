package org.example.listener;

import org.example.constants.ConsoleOperationType;
import org.example.command.OperationCommand;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class OperationsConsoleListener implements Runnable {

    private final Map<ConsoleOperationType, OperationCommand> commandMap;
    private final Scanner scanner;

    public OperationsConsoleListener(List<OperationCommand> commands,
                                     Scanner scanner) {
        this.scanner = scanner;
        commandMap = new HashMap<>();
        commands.forEach(command -> commandMap.put(command.getOperationType(), command));
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("Пожалуйста, выберите необходимую операцию");
            commandMap.keySet().stream().sorted(Comparator.naturalOrder()).forEach(System.out::println);
            System.out.println("EXIT");
            String choice = scanner.nextLine();
            if (choice.equals("EXIT")) break;
            try {
                ConsoleOperationType operation = ConsoleOperationType.valueOf(choice);
                if (commandMap.containsKey(operation)) {
                    commandMap.get(operation).execute();
                } else {
                    System.out.println("Операция не найдена, попробуйте ещё");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Операция не найдена, попробуйте ещё");
            }
            System.out.println("__________________________________________");
        }
    }
}
