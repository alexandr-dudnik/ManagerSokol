package com.sokolua.manager.mvp.models;

import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.ui.screens.customer_list.CustomerListItem;

import java.util.Date;
import java.util.List;

import io.reactivex.Observable;

public class RoutesModel extends AbstractModel {
    public Observable<List<CustomerListItem>> getCustomersByVisitDate(Date day) {
        Observable<CustomerRealm> obs = mDataManager.getCustomersByVisitDate(day);
        return obs.isEmpty().blockingGet()?Observable.empty(): obs.map(CustomerListItem::new).toList().toObservable();
    }
}
