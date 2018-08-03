package com.sokolua.manager.mvp.models;

import com.sokolua.manager.data.storage.dto.CustomerDto;
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

    public CustomerDto getCustomerDtoById(String customerId) {
        return new CustomerDto(mDataManager.getCustomerById(customerId));
    }

}
