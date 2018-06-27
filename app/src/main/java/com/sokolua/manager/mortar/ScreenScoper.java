package com.sokolua.manager.mortar;


import com.sokolua.manager.di.DaggerService;
import com.sokolua.manager.flow.AbstractScreen;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import mortar.MortarScope;

public class ScreenScoper {
    private static final String TAG = "ScreenScoper";
    private static final Map<String, MortarScope> sScopeMap = new HashMap<>();

    public static MortarScope getScreenScope(AbstractScreen screen){
        if (!sScopeMap.containsKey(screen.getScopeName())){
            return createScreenScope(screen);
        }else{
            return sScopeMap.get(screen.getScopeName());
        }
    }

    public static void registerScope(MortarScope scope){
        sScopeMap.put(scope.getName(), scope);
    }

    private static void cleanScopeMap(){
        Iterator<Map.Entry<String, MortarScope>> iterator = sScopeMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, MortarScope> entry= iterator.next();
            if (entry.getValue().isDestroyed()){
                iterator.remove();
            }
        }

    }

    public static void destroyScreenScope(String scopeName){
        MortarScope mortarScope = sScopeMap.get(scopeName);
        mortarScope.destroy();
        cleanScopeMap();
    }

    private static String getParentScopeName(AbstractScreen screen){
        try {
            String genericName = ((Class)((ParameterizedType) screen.getClass().getGenericSuperclass()).getActualTypeArguments()[0]).getName();
            String parentScopeName = genericName;

            if (parentScopeName.contains("$")){
                parentScopeName = parentScopeName.substring(0, genericName.indexOf("$"));
            }

            return parentScopeName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static MortarScope createScreenScope(AbstractScreen screen){
        MortarScope parentScope = sScopeMap.get(getParentScopeName(screen));
        Object screenComponent = screen.createScreenComponent(parentScope.getService(DaggerService.SERVICE_NAME));
        MortarScope newScope = parentScope.buildChild()
                .withService(DaggerService.SERVICE_NAME, screenComponent)
                .build(screen.getScopeName());
        registerScope(newScope);
        return newScope;

    }

}
