package com.deo.datescalculator;

import android.widget.NumberPicker;

enum FORMAT_TYPE {TYPE_YEAR, TYPE_MONTH, TYPE_DAY};

class Format implements NumberPicker.Formatter {
    private FORMAT_TYPE m_type;

    Format(FORMAT_TYPE type) {
        m_type = type;
    }

    @Override
    public String format(int value) {
        String res = "";
        switch (m_type) {
            case TYPE_YEAR: res = String.valueOf(value) + " г."; break;
            case TYPE_MONTH: res = String.valueOf(value) + " м."; break;
            case TYPE_DAY: res = String.valueOf(value) + " д."; break;
        }
        return res;
    }
}
