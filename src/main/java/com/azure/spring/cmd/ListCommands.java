package com.azure.spring.cmd;

import lombok.RequiredArgsConstructor;
import org.openrewrite.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.Collection;

@ShellComponent
@RequiredArgsConstructor
public class ListCommands {


    @Autowired
    RewriteRecipeRepository rewriteRecipeRepository;

    @ShellMethod(key = {"list", "l"}, value = "List all existing (applicable and non-applicable) recipes.")
    public String list() {
        Collection<Recipe> recipes = rewriteRecipeRepository.getRecipes(RewriteRecipeRepository.RecipeFilter.BOOT_UPGRADE);
        StringBuffer result = new StringBuffer();
        for (Recipe recipe : recipes) {
            result.append(recipe.getName()).append(":").append(recipe.getDisplayName()).append("\n");
        }
        return result.toString();
    }
}