package org.example;

import org.example.listener.OperationsConsoleListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext("org.example");
        var oprationListenerThread = new Thread(context.getBean(OperationsConsoleListener.class));
        oprationListenerThread.start();
    }
}
