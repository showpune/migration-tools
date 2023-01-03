package com.azure.spring.cmd;

import org.openrewrite.ExecutionContext;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.Result;
import org.openrewrite.config.Environment;
import org.openrewrite.internal.StringUtils;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.tree.J;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Component
public class RewriteRecipeRepository implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Environment environment;

    private ExecutionContext ctx;

    private Path projectDir;

    public boolean pathInitialized(){
        return projectDir!=null;
    }

    public Collection<Recipe> getRecipes(RecipeFilter filter) {
        return recipes.stream().filter(RECIPE_LIST_FILTERS.get(filter)).toList();
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
        if(ctx==null) {
            ctx = new InMemoryExecutionContext(Throwable::printStackTrace);
        }
        recipes = environment.listRecipes();
    }

    private static final Pattern P1 = Pattern.compile("(Upgrade|Migrate)SpringBoot_\\d+_\\d+");
    public static final Map<RecipeFilter, Predicate<Recipe>> RECIPE_LIST_FILTERS = new HashMap<>();
    static {
        RECIPE_LIST_FILTERS.put(RecipeFilter.ALL, r -> true);
        RECIPE_LIST_FILTERS.put(RecipeFilter.BOOT_UPGRADE, r -> {
            String n = lastTokenAfterDot(r.getName());
            return P1.matcher(n).matches();
        });
        RECIPE_LIST_FILTERS.put(RecipeFilter.NON_BOOT_UPGRADE, r -> {
            return RECIPE_LIST_FILTERS.get(RecipeFilter.BOOT_UPGRADE).negate().test(r);
        });
    }

    public enum RecipeFilter {
        ALL,
        BOOT_UPGRADE,
        NON_BOOT_UPGRADE
    }

    private static String lastTokenAfterDot(String s) {
        int idx = s.lastIndexOf('.');
        if (idx >= 0 && idx < s.length() - 1) {
            return s.substring(idx + 1);
        }
        return s;
    }


    private List<J.CompilationUnit> cus;

    public void compileWorkspace(String path) throws IOException {
        projectDir = Paths.get(path);
        List<Path> classpath = emptyList();
        List<Path> sourcePaths = Files.find(projectDir, 999, (p, bfa) ->
                        bfa.isRegularFile() && p.getFileName().toString().endsWith(".java"))
                .collect(Collectors.toList());

        // create a JavaParser instance with your classpath
        JavaParser javaParser = JavaParser.fromJavaVersion()
                .classpath(classpath)
                .build();

        // parser the source files into LSTs
        cus = javaParser.parse(sourcePaths, projectDir, ctx);
    }

    public void rewriteWorkspace(String recipeList,boolean dryRun) throws IOException {
        String recipeListString = recipeList;
        if(StringUtils.isBlank(recipeListString)){
            System.out.println("Please input the recipe name as arg1, seperated by ,");
        }
        String[] recipeStrings=recipeListString.split(",");
        for(String recipeString:recipeStrings) {
            //Recipe recipe = environment.activateRecipes("org.openrewrite.java.spring.boot2.UpgradeSpringBoot_2_7");
            Recipe recipe = environment.activateRecipes(recipeString);
            List<Result> results = recipe.run(cus, ctx).getResults();

            for (Result result : results) {
                // print diffs to the console
                if(dryRun) {
                    System.out.println(result.diff(projectDir));
                }

                // or overwrite the file on disk with changes.
                else {
                    Path backPath = Paths.get(projectDir.toString(), result.getAfter().getSourcePath().toString());
                    Files.writeString(backPath,
                            result.getAfter().printAll());
                }
            }
            System.out.println("Recipe "+recipeString+" is finished");
        }
    }

}
