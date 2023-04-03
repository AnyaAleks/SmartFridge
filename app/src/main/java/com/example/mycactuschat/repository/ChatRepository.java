package com.example.mycactuschat.repository;

import com.example.mycactuschat.Message;

import java.util.ArrayList;


/* Пример использования сего "чуда природы"

        ChatRepository.getInstance().getAllMessages(messages -> {
            // Что делаем с этими самыми рецептами. Например заполняем адаптер рекуклера
            // messages - собственно список сообщений
        });

 */
public class ChatRepository {

    private static ChatRepository instance;

    public static ChatRepository getInstance() {
        if (instance == null) {
            instance = new ChatRepository();
        }
        return instance;
    }

    private ChatRepository() {

    }


    public void getAllMessages(RepoOnCompleteListener<Message> listener) {
        listener.onComplete(new ArrayList<Message>());
    }


}
