package com.azure.spring;

import org.openrewrite.Recipe;
import org.openrewrite.config.Environment;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class RewriteRecipeRepository implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Environment environment;

    public Collection<Recipe> getRecipes() {
        return recipes;
    }

    private Collection<Recipe> recipes;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
    }

    public RewriteRecipeRepository(){

        CompletableFuture<Void> firstConfigLoaded = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> loadRecipes());
    }
    private void loadRecipes() {
        if(environment==null) {
            environment = Environment.builder().scanRuntimeClasspath().build();
        }
        recipes = environment.listRecipes();
    }
}
