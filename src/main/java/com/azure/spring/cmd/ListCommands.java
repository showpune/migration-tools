package com.azure.spring.cmd;

import com.azure.spring.RewriteRecipeRepository;
import org.openrewrite.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.Collection;

@ShellComponent
public class ListCommands {

    @Autowired
    RewriteRecipeRepository rewriteRecipeRepository;

    @ShellMethod(key = {"list", "l"}, value = "List all existing (applicable and non-applicable) recipes.")
    public String list() {
        Collection<Recipe> recipes = rewriteRecipeRepository.getRecipes();
        StringBuffer result = new StringBuffer();
        for (Recipe recipe : recipes) {
            result.append(recipe.getName()).append(":").append("\n");
        }
        return result.toString();
    }
}