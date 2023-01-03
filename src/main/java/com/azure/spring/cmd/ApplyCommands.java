package com.azure.spring.cmd;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.IOException;

@ShellComponent
@RequiredArgsConstructor
public class ApplyCommands {


    @Autowired
    RewriteRecipeRepository rewriteRecipeRepository;

    @ShellMethod(key = {"apply", "a"}, value = "apply list of recipe")
    public String apply(String recipeList) throws IOException {
        rewriteRecipeRepository.rewriteWorkspace(recipeList,false);
        return "Apply Done";
    }

    public Availability applyAvailability() {
        return rewriteRecipeRepository.pathInitialized()
                ? Availability.available()
                : Availability.unavailable("Scan path first");
    }
}