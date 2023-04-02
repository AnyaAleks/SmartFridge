package com.example.mycactuschat;

import java.io.Serializable;

public class Message implements Serializable {
    private String m_icon;
    private String m_header;
    private String m_dialog;
    private String m_date;

    public Message(String icon
            ,String header
            ,String dialog
            ,String date
    ){
        m_icon=icon;
        m_header=header;
        m_dialog=dialog;
        m_date=date;
    }

    public String getIconM(){return m_icon;}
    public String getHeaderM() {
        return m_header;
    }
    public String getDialogM() {
        return m_dialog;
    }
    public String getDateM() {
        return m_date;
    }
}
