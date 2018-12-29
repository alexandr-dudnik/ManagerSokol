package com.sokolua.manager.mvp.models;

import com.sokolua.manager.ui.screens.customer_list.CustomerListItem;

import java.util.Date;
import java.util.List;

import io.reactivex.Observable;

public class RoutesModel extends AbstractModel {
    public Observable<List<CustomerListItem>> getCustomersByVisitDate(Date day) {
        return mDataManager.getCustomersByVisitDate(day)
                .flatMap(list->
                     Observable.fromIterable(list)
                            .map(CustomerListItem::new)
                            .toList()
                            .toObservable()
                )
                ;
    }
}
