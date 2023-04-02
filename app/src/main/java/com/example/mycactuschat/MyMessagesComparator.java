package com.example.mycactuschat;

import java.util.Comparator;

public class MyMessagesComparator implements Comparator<Message> {
    public int compare(Message c1, Message c2)
    {
        long foo1 = Long.parseLong(c1.getDateM());
        long foo2 = Long.parseLong(c2.getDateM());

        if (c1.getDateM().equals(c2.getDateM())) {
            return 0;
        }
        else if (foo1<foo2) {
            return 1;
        }
        else {
            return -1;
        }
    }
}
