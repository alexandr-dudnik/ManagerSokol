package com.sokolua.manager.flow;


import com.sokolua.manager.mortar.ScreenScoper;

import flow.ClassKey;

public abstract class AbstractScreen<T> extends ClassKey {

    public String getScopeName(){
        return getClass().getName();
    }

    public abstract Object createScreenComponent(T parentComponent);

    public void unregisterScope(){
        ScreenScoper.destroyScreenScope(getScopeName());
    }

    public int getLayoutResId(){
        int layout = 0;

        Screen screen;
        screen = this.getClass().getAnnotation(Screen.class);
        if (screen == null){
            throw new IllegalStateException(" Annotation @Screen is not set for class " + getScopeName());
        }else{
            layout = screen.value();
        }
        return layout;
    }

}
