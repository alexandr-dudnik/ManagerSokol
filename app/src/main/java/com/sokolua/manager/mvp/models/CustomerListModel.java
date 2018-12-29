package com.sokolua.manager.mvp.models;

import com.sokolua.manager.ui.screens.customer_list.CustomerListItem;

import java.util.List;

import io.reactivex.Observable;

public class CustomerListModel extends AbstractModel {
    public CustomerListModel() {
    }

    public Observable<List<CustomerListItem>> getCustomerList(String filter){
        return mDataManager.getCustomersFromRealm(filter)
                .flatMap(list->Observable.fromIterable(list)
                .map(CustomerListItem::new)
                .toList()
                .toObservable()
                );
    }

    public Observable<List<CustomerListItem>> getCustomerListHeadered(String filter){
        if (filter != null && !filter.isEmpty()) {
            return getCustomerList(filter);
        }

        return mDataManager.getCustomersFromRealm("")
                .flatMap(list->
                                Observable.fromIterable(list)
                                        .map(CustomerListItem::new)
                                        .groupBy(item -> item.getCustomer().getName().charAt(0))
                                        .flatMap(grp -> grp.startWith(new CustomerListItem(grp.getKey().toString())))
                                        .toList()
                                        .toObservable()


                        )
                ;
    }

}
