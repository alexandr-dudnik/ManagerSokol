package com.sokolua.manager.flow;

import com.sokolua.manager.mortar.ScreenScoper;

import flow.ClassKey;

public abstract class AbstractScreen<T> extends ClassKey {

    public String getScopeName() {
        return getClass().getName();
    }

    public abstract Object createScreenComponent(T parentComponent);

    public void unregisterScope() {
        ScreenScoper.destroyScreenScope(getScopeName());
    }

    abstract public int getLayoutResId();

}
