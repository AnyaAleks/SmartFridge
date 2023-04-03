package com.example.mycactuschat.model;

import java.util.List;

public class Recipe {

    private String id;
    private String author;
    private String description;

    // TODO: Как лучше передавать ингредиенты?
    private List<String> ingredients;

    private String instruction;

    // TODO: Стоит ли добавлять список картинок, или одной хватит?
    private String imageUrl;
}
