package com.sokolua.manager.mvp.models;

import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.ui.screens.cust_list.CustomerListItem;

import java.util.List;

import io.reactivex.Observable;

public class CustomerListModel extends AbstractModel {
    public CustomerListModel() {
    }

    public Observable<List<CustomerListItem>> getCustomerList(String filter){
        Observable<CustomerRealm> obs = mDataManager.getCustomersFromRealm(filter);
        return obs.map(CustomerListItem::new).toList().toObservable();
    }

    public Observable<List<CustomerListItem>> getHeaderedCustomerList(String filter){
        if (filter != null && !filter.isEmpty()) {
            return getCustomerList(filter);
        }
        Observable<CustomerRealm> obs = mDataManager.getCustomersFromRealm("");

        return obs.map(CustomerListItem::new)
                .groupBy(item -> item.getCustomer().getName().charAt(0))
                .flatMap(grp -> grp.startWith(new CustomerListItem(grp.getKey().toString())))
                .toList()
                .toObservable();
    }

}
