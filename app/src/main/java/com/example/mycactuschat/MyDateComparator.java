package com.example.mycactuschat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class MyDateComparator implements Comparator<Chat> {
    public int compare(Chat c1, Chat c2)
    {
        long foo1 = Long.parseLong(c1.getMessageTime());
        long foo2 = Long.parseLong(c2.getMessageTime());

        if (c1.getMessageTime().equals(c2.getMessageTime())) {
            return 0;
        }
        else if (foo1>foo2) {
            return 1;
        }
        else {
            return -1;
        }
    }
}
