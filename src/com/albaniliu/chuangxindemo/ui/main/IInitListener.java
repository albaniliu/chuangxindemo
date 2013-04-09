package com.albaniliu.chuangxindemo.ui.main;

/**
 * Interface of initialization listener
 * 
 * @author neusoft
 * 
 */
public interface IInitListener {
    void onInitStarted();

    void updateMessage( String aMessage );

    void onInitEnded();
}