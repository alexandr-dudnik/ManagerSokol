package com.sokolua.manager.data.network;

import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.data.network.error.AccessDenied;
import com.sokolua.manager.data.network.error.AccessError;
import com.sokolua.manager.data.network.error.ErrorUtils;
import com.sokolua.manager.data.network.error.NetworkAvailableError;
import com.sokolua.manager.utils.NetworkStatusChecker;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import retrofit2.Response;

public class RestCallTransformer<T> implements ObservableTransformer<Response<T>, T> {


    @Override
    public ObservableSource<T> apply(Observable<Response<T>> responseObservable) {
        return NetworkStatusChecker.isInternetAvailableObs()
                .flatMap(aBoolean -> aBoolean ? responseObservable : Observable.error(new NetworkAvailableError()))
                .flatMap(rResponse ->{
                    switch (rResponse.code()){
                        case 200:
                            if (rResponse.body()!=null) {
                                String lastModified = rResponse.headers().get(ConstantManager.HEADER_LAST_MODIFIED);
                                if (lastModified != null) {
                                    Class module = null;
                                    if (rResponse.body() instanceof ArrayList){
                                        if (!((ArrayList) rResponse.body()).isEmpty()){
                                            module =(((ArrayList) rResponse.body()).get(0)).getClass();
                                        }
                                    } else {
                                        module = rResponse.body().getClass();
                                    }
                                    if (module != null) {
                                        DataManager.getInstance().setLastUpdate(module.getSimpleName(), lastModified);
                                    }
                                }
                                return Observable.just(rResponse.body());
                            }else{
                                return Observable.empty();
                            }
                        case 304:
                            return Observable.empty();
                        case 401:
                            return Observable.error(AccessError::new);
                        case 403:
                            return Observable.error(AccessDenied::new);
                        default:
                            return Observable.error(ErrorUtils.parseError(rResponse));
                    }

                })
                ;

    }
}
