package com.sokolua.manager.data.managers;

import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.DebtRealm;

import java.util.ArrayList;

import io.reactivex.Observable;

public class RealmManager {

    public Observable<CustomerRealm> getCustomersFromRealm(String filter){
        //RealmResults<CustomerRealm> managedCustomers = getQueryRealmInstance().where(CustomerRealm.class).findAllAsync();

        ArrayList<CustomerRealm> managedCustomers= new ArrayList<>();

        CustomerRealm temp = new CustomerRealm("cust0001","Аверьянов ЧП", "Днепр, пр. Кирова, 119", "123-23-12");
        temp.getDebt().add(new DebtRealm(temp.getCustomerId(),"USD",1250,1250,true));
        temp.getDebt().add(new DebtRealm(temp.getCustomerId(),"UAH",2700,100,true));
        temp.getDebt().add(new DebtRealm(temp.getCustomerId(),"UAH",3500,150,false));
        managedCustomers.add(temp);
        //--------------------------
        temp = new CustomerRealm("cust0002","Автозапчасти магазин", "Каменское, пр. Аношкина, 21", "222-77-55");
        temp.getDebt().add(new DebtRealm(temp.getCustomerId(),"UAH",500,18,false));
        managedCustomers.add(temp);
        //--------------------------
        temp = new CustomerRealm("cust0003","Авиатор охранное агенство", "Днепр, пр. Слобожанский, 77", "");
        managedCustomers.add(temp);
        //--------------------------
        temp = new CustomerRealm("cust0004","Белый ЧП", "", "067-667-88-00");
        temp.getDebt().add(new DebtRealm(temp.getCustomerId(),"UAH",2700,100,true));
        temp.getDebt().add(new DebtRealm(temp.getCustomerId(),"USD",150,150,false));
        managedCustomers.add(temp);
        temp = new CustomerRealm("cust0005","Борода ООО", "Кривой Рог, ул.Ленина, 10", "");
        managedCustomers.add(temp);
        //--------------------------
        temp = new CustomerRealm("cust0006","Владислав ЧП", "Днепр, пр. Богдана Хмельницкого, 150", "");
        temp.getDebt().add(new DebtRealm(temp.getCustomerId(),"USD",130,130,true));
        temp.getDebt().add(new DebtRealm(temp.getCustomerId(),"UAH",250,9.50f,false));
        managedCustomers.add(temp);
        //--------------------------

        return Observable.fromIterable(managedCustomers)
                    .filter(customerRealm -> (filter == null) || (filter.isEmpty() || customerRealm.getCustomerName().toLowerCase().contains(filter.toLowerCase())));

//        return managedProduct
//                .asObservable()  //Получаем последовательность
//                .filter(RealmResults::isLoaded) //получаем только загруженные
//                //.first() //Если нужна холодная последовательность
//                .flatMap(Observable::from); //преобразуем в Obs<ProductRealm>
    }

}
