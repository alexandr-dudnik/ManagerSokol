package com.sokolua.manager.mvp.models;

import com.sokolua.manager.data.storage.dto.CustomerDto;
import com.sokolua.manager.data.storage.realm.CustomerRealm;

import io.reactivex.Observable;

public class CustomerListModel extends AbstractModel {
    public CustomerListModel() {
    }

    public Observable<CustomerRealm> getCustomerList(String filter){
        return mDataManager.getProductFromRealm(filter);
    }

    public CustomerDto getCustomerDtoById(String customerId) {
        return new CustomerDto(mDataManager.getCustomerById(customerId));
    }
}
