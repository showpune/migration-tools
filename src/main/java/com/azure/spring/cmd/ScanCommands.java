package com.azure.spring.cmd;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.IOException;


@ShellComponent
@RequiredArgsConstructor
public class ScanCommands {

    @Autowired
    RewriteRecipeRepository rewriteRecipeRepository;

    @ShellMethod(key = {"scan"}, value = "Scan Path")
    public String scan(String path) throws IOException {
        rewriteRecipeRepository.compileWorkspace(path);
       return "Set source code path:"+path;
    }
}