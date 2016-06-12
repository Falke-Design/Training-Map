package com.falke.training_map;

import android.app.Application;

import com.pepperonas.andbasx.AndBasx;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class App extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        // jetzt kannst du die library nutzen ;)
        AndBasx.init(this);
    }
}
