package org.example.command;

import org.example.constants.ConsoleOperationType;


public interface OperationCommand {
    void execute();

    ConsoleOperationType getOperationType();
}
