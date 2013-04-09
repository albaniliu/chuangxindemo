package com.albaniliu.chuangxindemo.ui.main;

public class WidgetEventArgs {
    public Object Sender; // reference to sender
    public Object Data;

    // constructor
    public WidgetEventArgs( Object aSender, Object aData ) {
        Sender = aSender;
        Data = aData;
    }
}
