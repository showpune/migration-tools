package com.azure.spring.cmd;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class ScanCommands {

    @ShellMethod(key = {"list", "l"}, value = "List all existing (applicable and non-applicable) recipes.")
    public int add(int a, int b) {
        return a + b;
    }
}