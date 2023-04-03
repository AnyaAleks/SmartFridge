package com.example.mycactuschat.repository;

import com.example.mycactuschat.Message;
import com.example.mycactuschat.model.Recipe;

import java.util.ArrayList;
import java.util.List;



/* Пример использования сего "чуда природы"

        RecipesRepository.getInstance().getAllRecipes(recipes -> {
            // Что делаем с этими самыми рецептами. Например заполняем адаптер рекуклера
            // recipes - собственно список рецептов
        });

 */
public class RecipesRepository {

    private static RecipesRepository instance;

    public static RecipesRepository getInstance() {
        if (instance == null) {
            instance = new RecipesRepository();
        }
        return instance;
    }

    private RecipesRepository() {

    }


    // Вместо new ArrayList можно передать сейчас любой список рецептов. Можно использовать как заглушку, пока нет сервера
    public void getAllRecipes(RepoOnCompleteListener<Recipe> listener) {
        listener.onComplete(new ArrayList<Recipe>());
    }

    public void getFavoriteRecipes(RepoOnCompleteListener<Recipe> listener) {
        listener.onComplete(new ArrayList<Recipe>());
    }

    public void getMyRecipes(RepoOnCompleteListener<Recipe> listener) {
        listener.onComplete(new ArrayList<Recipe>());
    }

    public void getPossibleRecipes(RepoOnCompleteListener<Recipe> listener) {
        listener.onComplete(new ArrayList<Recipe>());
    }

}
