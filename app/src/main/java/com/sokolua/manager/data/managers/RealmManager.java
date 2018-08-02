package com.sokolua.manager.data.managers;

import android.support.annotation.Nullable;

import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.DebtRealm;
import com.sokolua.manager.data.storage.realm.NoteRealm;
import com.sokolua.manager.data.storage.realm.TaskRealm;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Case;
import io.realm.RealmList;
import io.realm.RealmResults;

public class RealmManager {
    private RealmList<CustomerRealm>  customers() {
        RealmList<CustomerRealm> managedCustomers= new RealmList<>();

        CustomerRealm temp = new CustomerRealm("cust0001","Аверьянов ЧП", "Аверьянов Василий Петрович", "Днепр, пр. Кирова, 119", "123-23-12", "averianov@ukr.net");
        temp.getDebt().add(new DebtRealm(temp,"USD",1250,1250,true));
        temp.getDebt().add(new DebtRealm(temp,"UAH",2700,100,true));
        temp.getDebt().add(new DebtRealm(temp,"UAH",3500,150,false));
        temp.getNotes().add(new NoteRealm(temp, "note0101","01.05.2018","Клиент попросил скидку 7% на кабельный канал - обсуждаем с руководством"));
        temp.getNotes().add(new NoteRealm(temp, "note0102","07.05.2018","Договорились о поставке крупной партии металлорукава"));
        temp.getTasks().add(new TaskRealm(temp, "task0101","Металлорукав", ConstantManager.TASK_TYPE_RESEARCH));
        temp.getTasks().add(new TaskRealm(temp, "task0102","LED лампы", ConstantManager.TASK_TYPE_RESEARCH));
        temp.getTasks().add(new TaskRealm(temp, "task0103","Забрать дебет", ConstantManager.TASK_TYPE_INDIVIDUAL));
        temp.getTasks().add(new TaskRealm(temp, "task0104","Поздравить 05.08 директора с днем рождения", ConstantManager.TASK_TYPE_INDIVIDUAL));
        managedCustomers.add(temp);
        //--------------------------
        temp = new CustomerRealm("cust0002","Автозапчасти магазин", "Денис Олегович", "Каменское, пр. Аношкина, 21", "222-77-55", "orders@ua.fm");
        temp.getDebt().add(new DebtRealm(temp,"UAH",500,18,false));
        managedCustomers.add(temp);
        //--------------------------
        temp = new CustomerRealm("cust0003","Авиатор охранное агенство", "Семен", "Днепр, пр. Слобожанский, 77", "", "info@aviator.ua");
        temp.getNotes().add(new NoteRealm(temp, "note0301","15.06.2018","Провел демонстрацию выключателей, попросили оставить образцы для тестов"));
        temp.getNotes().add(new NoteRealm(temp, "note0302","01.07.2018","Обсудили условия поставки провода на объекты"));
        managedCustomers.add(temp);
        //--------------------------
        temp = new CustomerRealm("cust0004","Белый ЧП", "директор", "", "067-667-88-00","");
        temp.getDebt().add(new DebtRealm(temp,"UAH",2700,100,true));
        temp.getDebt().add(new DebtRealm(temp,"USD",150,150,false));
        managedCustomers.add(temp);
        temp = new CustomerRealm("cust0005","Борода ООО", "", "Кривой Рог, ул.Ленина, 10", "","golova@boroda.com");
        managedCustomers.add(temp);
        //--------------------------
        temp = new CustomerRealm("cust0006","Владислав ЧП", "Владислав", "Днепр, пр. Богдана Хмельницкого, 150", "","");
        temp.getDebt().add(new DebtRealm(temp,"USD",130,130,true));
        temp.getDebt().add(new DebtRealm(temp,"UAH",250,9.50f,false));
        managedCustomers.add(temp);
        //--------------------------
        return managedCustomers;
    }

    public Observable<List<CustomerRealm>> getCustomersFromRealm(String filter){
        //RealmResults<CustomerRealm> managedCustomers = getQueryRealmInstance().where(CustomerRealm.class).findAllAsync();




        RealmResults<CustomerRealm> managedCustomers =  customers()
                .where()
                .contains("name", filter == null ? "": filter, Case.INSENSITIVE)
                .sort("name")
                .findAllAsync();

        return (Observable)(Observable.just(
                managedCustomers
                .asFlowable()  //Получаем последовательность
                .filter(RealmResults::isLoaded) //получаем только загруженные
                .filter(RealmResults::isValid)));
    }

    @Nullable
    public CustomerRealm getCustomerById(String id) {
        for (CustomerRealm st: customers()) {
            if (st.getCustomerId().equals(id)) {
                return st;
            }
        }
        return null;
    }

    public int getCustomerDebtType(CustomerRealm cust){
        if (cust.getDebt().size() == 0){
            return ConstantManager.DEBT_TYPE_NO_DEBT;
        }
        return cust.getDebt().where().equalTo("outdated",true).findFirst() == null ? ConstantManager.DEBT_TYPE_NORMAL : ConstantManager.DEBT_TYPE_OUTDATED;
    }

}
