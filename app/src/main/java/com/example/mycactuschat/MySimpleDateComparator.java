package com.example.mycactuschat;

import java.util.Comparator;

public class MySimpleDateComparator implements Comparator<SimpleMessageView> {
    public int compare(SimpleMessageView c1, SimpleMessageView c2)
    {
        long foo1 = Long.parseLong(c1.getSimpleTime());
        long foo2 = Long.parseLong(c2.getSimpleTime());

        if (c1.getSimpleTime().equals(c2.getSimpleTime())) {
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
