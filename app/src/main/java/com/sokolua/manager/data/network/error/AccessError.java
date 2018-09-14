package com.sokolua.manager.data.network.error;

import com.sokolua.manager.R;
import com.sokolua.manager.utils.App;

public class AccessError extends Throwable{
    public AccessError() {
        super(App.getStringRes(R.string.error_auth_error));
    }
}
