package com.sokolua.manager.data.network.error;

import com.sokolua.manager.R;
import com.sokolua.manager.utils.App;

public class AccessDenied extends Throwable{
    public AccessDenied() {
        super(App.getStringRes(R.string.error_access_denied));
    }
}
