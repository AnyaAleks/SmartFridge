package com.example.mycactuschat.repository;

import com.example.mycactuschat.Message;
import com.example.mycactuschat.model.Recipe;

import java.util.List;

public interface RepoOnCompleteListener <T> {
    void onComplete(List<T> result);
}
