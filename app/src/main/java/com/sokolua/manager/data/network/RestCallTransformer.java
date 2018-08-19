package com.sokolua.manager.data.network;

import com.sokolua.manager.data.managers.ConstantManager;
import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.data.network.error.ErrorUtils;
import com.sokolua.manager.data.network.error.NetworkAvailableError;
import com.sokolua.manager.utils.NetworkStatusChecker;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import retrofit2.Response;

public class RestCallTransformer<R> implements ObservableTransformer<Response<R>, R> {


    @Override
    public ObservableSource<R> apply(Observable<Response<R>> responseObservable) {
        return NetworkStatusChecker.isInternetAvailiableObs()
                .flatMap(aBoolean -> aBoolean ? responseObservable : Observable.error(new NetworkAvailableError()))
                .flatMap(rResponse ->{
                    switch (rResponse.code()){
                        case 200:
                            String lastModified = rResponse.headers().get(ConstantManager.HEADER_LAST_MODIFIED);
                            if (lastModified != null){
                                DataManager.getInstance().getPreferencesManager().saveLastProductUpdate(lastModified);
                            }
                            return Observable.just(rResponse.body());
                        case 304:
                            return Observable.empty();
                        default:
                            return Observable.error(ErrorUtils.parseError(rResponse));
                    }

                });

    }
}
