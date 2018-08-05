package com.sokolua.manager.data.managers;

import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.DebtRealm;
import com.sokolua.manager.data.storage.realm.GoodsCategoryRealm;
import com.sokolua.manager.data.storage.realm.NoteRealm;
import com.sokolua.manager.data.storage.realm.OrderPlanRealm;
import com.sokolua.manager.data.storage.realm.TaskRealm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;

public class DebugModule {
    public static void mock_RealmDB() throws ParseException {
        Realm realm = Realm.getDefaultInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        RealmList<CustomerRealm> managedCustomers= new RealmList<>();

        CustomerRealm temp = new CustomerRealm("cust0001","Аверьянов ЧП", "Аверьянов Василий Петрович", "Днепр, пр. Кирова, 119", "123-23-12", "averianov@ukr.net");
        temp.getDebt().add(new DebtRealm(temp,"USD",1250,1250,true));
        temp.getDebt().add(new DebtRealm(temp,"UAH",2700,100,true));
        temp.getDebt().add(new DebtRealm(temp,"UAH",3500,150,false));
        temp.getNotes().add(new NoteRealm(temp, "note0101", dateFormat.parse("2018-05-01"),"Клиент попросил скидку 7% на кабельный канал - обсуждаем с руководством"));
        temp.getNotes().add(new NoteRealm(temp, "note0102",dateFormat.parse("2018-07-05"),"Договорились о поставке крупной партии металлорукава"));
        temp.getTasks().add(new TaskRealm(temp, "task0101","Металлорукав", ConstantManager.TASK_TYPE_RESEARCH));
        temp.getTasks().add(new TaskRealm(temp, "task0102","LED лампы", ConstantManager.TASK_TYPE_RESEARCH));
        temp.getTasks().add(new TaskRealm(temp, "task0103","Забрать дебет", ConstantManager.TASK_TYPE_INDIVIDUAL));
        temp.getTasks().add(new TaskRealm(temp, "task0104","Поздравить 05.08 директора с днем рождения", ConstantManager.TASK_TYPE_INDIVIDUAL));
        temp.getPlan().add(new OrderPlanRealm(temp, new GoodsCategoryRealm("cat001","Кабельно-проводниковая продукция",""), 1500f));
        temp.getPlan().add(new OrderPlanRealm(temp, new GoodsCategoryRealm("cat002","Системы прокладки кабеля",""), 5000f));
        temp.getPlan().add(new OrderPlanRealm(temp, new GoodsCategoryRealm("cat003","Источники света",""), 3500f));
        managedCustomers.add(temp);
        //--------------------------
        temp = new CustomerRealm("cust0002","Автозапчасти магазин", "Денис Олегович", "Каменское, пр. Аношкина, 21", "222-77-55", "orders@ua.fm");
        temp.getDebt().add(new DebtRealm(temp,"UAH",500,18,false));
        managedCustomers.add(temp);
        //--------------------------
        temp = new CustomerRealm("cust0003","Авиатор охранное агенство", "Семен", "Днепр, пр. Слобожанский, 77", "", "info@aviator.ua");
        temp.getNotes().add(new NoteRealm(temp, "note0301",dateFormat.parse("2018-06-03"),"Провел демонстрацию выключателей, попросили оставить образцы для тестов"));
        temp.getNotes().add(new NoteRealm(temp, "note0302",dateFormat.parse("2018-07-01"),"Обсудили условия поставки провода на объекты"));
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

        realm.executeTransaction(db -> db.insertOrUpdate(managedCustomers));
        realm.close();
    }
}
