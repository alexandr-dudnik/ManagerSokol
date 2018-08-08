package com.sokolua.manager.data.managers;

import com.sokolua.manager.data.storage.realm.BrandsRealm;
import com.sokolua.manager.data.storage.realm.CustomerRealm;
import com.sokolua.manager.data.storage.realm.DebtRealm;
import com.sokolua.manager.data.storage.realm.GoodsCategoryRealm;
import com.sokolua.manager.data.storage.realm.GoodsGroupRealm;
import com.sokolua.manager.data.storage.realm.ItemRealm;
import com.sokolua.manager.data.storage.realm.NoteRealm;
import com.sokolua.manager.data.storage.realm.OrderLineRealm;
import com.sokolua.manager.data.storage.realm.OrderPlanRealm;
import com.sokolua.manager.data.storage.realm.OrderRealm;
import com.sokolua.manager.data.storage.realm.TaskRealm;
import com.sokolua.manager.data.storage.realm.VisitRealm;

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
        RealmList<OrderRealm> managedOrders= new RealmList<>();
        RealmList<ItemRealm> managedItems= new RealmList<>();
        RealmList<GoodsCategoryRealm> managedCats= new RealmList<>();
        RealmList<BrandsRealm> managedBrands= new RealmList<>();
        RealmList<GoodsGroupRealm> managedGroups= new RealmList<>();

        GoodsCategoryRealm cat1 = new GoodsCategoryRealm("cat001","01. кабельно-проводниковая продукция","");
        GoodsCategoryRealm cat2 = new GoodsCategoryRealm("cat002","02.1. кабельные каналы","");
        GoodsCategoryRealm cat3 = new GoodsCategoryRealm("cat003","02.2.1 металлорукав РЗЦ","");
        GoodsCategoryRealm cat4 = new GoodsCategoryRealm("cat004","02.2.2. металлорукав РЗЦ-Х","");
        GoodsCategoryRealm cat5 = new GoodsCategoryRealm("cat005","SCHNEIDER","");
        GoodsCategoryRealm cat6 = new GoodsCategoryRealm("cat006","07.1.1. встроенная инсталяция 220","");
        GoodsCategoryRealm cat7 = new GoodsCategoryRealm("cat007","09.1. лампы LED 220","");
        GoodsCategoryRealm cat8 = new GoodsCategoryRealm("cat008","999.06. источники света","");
        GoodsCategoryRealm cat9 = new GoodsCategoryRealm("cat009","08.1.2 LED панели 220","");
        managedCats.add(cat1);
        managedCats.add(cat2);
        managedCats.add(cat3);
        managedCats.add(cat4);
        managedCats.add(cat5);
        managedCats.add(cat6);
        managedCats.add(cat7);
        managedCats.add(cat8);
        managedCats.add(cat9);

        BrandsRealm brand0 = new BrandsRealm("br000", "-","");
        BrandsRealm brand1 = new BrandsRealm("br001", "220TM","");
        BrandsRealm brand2 = new BrandsRealm("br002", "Schneider","");
        BrandsRealm brand3 = new BrandsRealm("br003", "SOKOL","");
        BrandsRealm brand4 = new BrandsRealm("br004", "Maxus","");
        BrandsRealm brand5 = new BrandsRealm("br005", "Титан","");
        managedBrands.add(brand0);
        managedBrands.add(brand1);
        managedBrands.add(brand2);
        managedBrands.add(brand3);
        managedBrands.add(brand4);
        managedBrands.add(brand5);

        GoodsGroupRealm mgrp1 = new GoodsGroupRealm("mgrp0001", "Кабель и провод", null, "");
        GoodsGroupRealm mgrp2 = new GoodsGroupRealm("mgrp0002", "Кабельные трассы", null, "");
        GoodsGroupRealm mgrp3 = new GoodsGroupRealm("mgrp0003", "Фурнитура", null, "");
        GoodsGroupRealm mgrp4 = new GoodsGroupRealm("mgrp0004", "Источники света", null, "");
        managedGroups.add(mgrp1);
        managedGroups.add(mgrp2);
        managedGroups.add(mgrp3);
        managedGroups.add(mgrp4);

        GoodsGroupRealm grp1 = new GoodsGroupRealm("grp0001", "ШВВП", mgrp1, "");
        GoodsGroupRealm grp2 = new GoodsGroupRealm("grp0002", "ПВС", mgrp1, "");
        GoodsGroupRealm grp3 = new GoodsGroupRealm("grp0003", "Кабельные каналы", mgrp2, "");
        GoodsGroupRealm grp4 = new GoodsGroupRealm("grp0004", "Металлорукав РЗЦ", mgrp2, "");
        GoodsGroupRealm grp5 = new GoodsGroupRealm("grp0005", "Металлорукав РЗЦ-Х", mgrp2, "");
        GoodsGroupRealm grp6 = new GoodsGroupRealm("grp0006", "Розетки", mgrp3, "");
        GoodsGroupRealm grp7 = new GoodsGroupRealm("grp0007", "Выключатели", mgrp3, "");
        GoodsGroupRealm grp8 = new GoodsGroupRealm("grp0008", "LED Лампы", mgrp4, "");
        GoodsGroupRealm grp9 = new GoodsGroupRealm("grp0009", "LED Панели", mgrp4, "");
        managedGroups.add(grp1);
        managedGroups.add(grp2);
        managedGroups.add(grp3);
        managedGroups.add(grp4);
        managedGroups.add(grp5);
        managedGroups.add(grp6);
        managedGroups.add(grp7);
        managedGroups.add(grp8);
        managedGroups.add(grp9);



        ItemRealm item1 = new ItemRealm("it00001", "Провід ШВВП 2х 1,5  220тм", "84255", 12.65f, 9.35f, 1200f, 5000f, 4000f, cat1, grp1, brand1);
        ItemRealm item2 = new ItemRealm("it00002", "Провід ШВВП 2х 4,0  220тм", "87722", 19.94f, 14.76f, 0f, 1000f, 400f, cat1, grp1, brand1);
        ItemRealm item3 = new ItemRealm("it00003", "Провід ШВВП 3х 2,5  Титан", "94295", 31.63f, 23.38f, 100f, 3000f, 5000f, cat1, grp1, brand5);
        ItemRealm item4 = new ItemRealm("it00004", "Провід ПВС 2х 4,0  220тм", "88516", 21.18f, 15.90f, 530f, 4700f, 6000f, cat1, grp2, brand1);
        ItemRealm item5 = new ItemRealm("it00005", "Провід ПВС 3х 2,5  220тм", "88518", 33.28f, 24.75f, 660f, 1300f, 3500f, cat1, grp2, brand1);
        ItemRealm item6 = new ItemRealm("it00006", "Кабельний канал  12х12 (200)  220тм", "65663", 4.68f, 3.03f, 20000f, 5000f, 50000f, cat2, grp3, brand3);
        ItemRealm item7 = new ItemRealm("it00007", "Кабельний канал  40х16 (80)  220тм", "65670", 12.38f, 8.25f, 15000f, 35000f, 55000f, cat2, grp3, brand3);
        ItemRealm item8 = new ItemRealm("it00008", "Кабельний канал  12х12 (200) преміум 220тм", "86486", 5.38f, 4.25f, 1000f, 300f, 5000f, cat2, grp3, brand1);
        ItemRealm item9 = new ItemRealm("it00009", "Металорукав РЗЦ з протяжкою d 09 (50) 220тм", "67416", 8.80f, 5.78f, 15000f, 17300f, 25000f, cat3, grp4, brand1);
        ItemRealm item10 = new ItemRealm("it00010", "Металорукав РЗЦХ з протяжкою d 11 (50) 220тм", "35181", 9.80f, 6.78f, 0f, 3000f, 5000f, cat4, grp5, brand1);
        ItemRealm item11 = new ItemRealm("it00011", "EPH2900121 розетка 1-а з/з біл. Schneider  ASFORA", "88196", 80.40f, 60.55f, 10f, 30f, 20f, cat5, grp6, brand2);
        ItemRealm item12 = new ItemRealm("it00012", "EPH0300121 вимикач 2кл. біл. Schneider  ASFORA", "96450", 75.40f, 70.35f, 2f, 40f, 100f, cat5, grp7, brand2);
        ItemRealm item13 = new ItemRealm("it00013", "Вимикач двухклавішний з підсвічуванням ТМ \"220\" білий", "74424", 60.50f, 42.90f, 200f, 50f, 1000f, cat6, grp7, brand1);
        ItemRealm item14 = new ItemRealm("it00014", "Вимикач одноклавішний ТМ \"220\"  крем", "74534", 48.13f, 34.10f, 120f, 1050f, 325f, cat6, grp7, brand1);
        ItemRealm item15 = new ItemRealm("it00015", "Розетка зі шторками з заземленням з кришкою ТМ \"220\" біла", "74544", 61.88f, 44.10f, 15f, 125f, 5f, cat6, grp6, brand1);
        ItemRealm item16 = new ItemRealm("it00016", "Розетка зі шторками подвійна з заземленням ТМ \"220\" крем", "74554", 96.25f, 68.48f, 15f, 125f, 5f, cat6, grp6, brand1);
        ItemRealm item17 = new ItemRealm("it00017", "LED лампа G45  5.0W 220В E14 3000К 220тм", "90135", 45.38f, 30.53f, 5000f, 12500f, 50000f, cat7, grp8, brand1);
        ItemRealm item18 = new ItemRealm("it00018", "LED лампа A60 10.0W 220В E27 4100К 220тм", "86627", 53.63f, 36.03f, 3500f, 0f, 53000f, cat7, grp8, brand1);
        ItemRealm item19 = new ItemRealm("it00019", "144-01 Лампа LED MR16 3W 4100K 220V GU5.3 Maxus", "84227", 109.18f, 87.45f, 3f, 10f, 5f, cat8, grp8, brand4);
        ItemRealm item20 = new ItemRealm("it00020", "563 (461) Лампа LED A65 12W 3000K 220V E27 Maxus", "77913", 271.43f, 217.25f, 4f, 0f, 4f, cat8, grp8, brand4);
        ItemRealm item21 = new ItemRealm("it00021", "Світлодіодна LED панель 40.0W 220В 3000lm 600х600х8,5 IP 20 5000К Sokol", "95433", 605.00f, 405.35f, 40f, 10f, 400f, cat9, grp9, brand3);
        ItemRealm item22 = new ItemRealm("it00022", "Світлодіодна LED панель 36.0W 220В 3000lm 300х1200х8,5 IP 20", "83632", 1502.33f, 1006.78f, 25f, 150f, 300f, cat9, grp9, brand3);


        CustomerRealm temp = new CustomerRealm("cust0001","Аверьянов ЧП", "Аверьянов Василий Петрович", "Днепр, пр. Кирова, 119", "123-23-12", "averianov@ukr.net");
        temp.getDebt().add(new DebtRealm(temp,"USD",1250,1250,true));
        temp.getDebt().add(new DebtRealm(temp,"UAH",2700,100,true));
        temp.getDebt().add(new DebtRealm(temp,"UAH",3500,150,false));
        temp.getNotes().add(new NoteRealm(temp, "note0101", dateFormat.parse("2018-05-01"),"Клиент попросил скидку 7% на кабельный канал - обсуждаем с руководством"));
        temp.getNotes().add(new NoteRealm(temp, "note0102",dateFormat.parse("2018-07-05"),"Договорились о поставке крупной партии металлорукава"));
        temp.getTasks().add(new TaskRealm(temp, "task0101","Металлорукав", ConstantManager.TASK_TYPE_RESEARCH,false,""));
        temp.getTasks().add(new TaskRealm(temp, "task0102","LED лампы", ConstantManager.TASK_TYPE_RESEARCH,false,""));
        temp.getTasks().add(new TaskRealm(temp, "task0103","Забрать дебет", ConstantManager.TASK_TYPE_INDIVIDUAL,false,""));
        temp.getTasks().add(new TaskRealm(temp, "task0104","Поздравить 05.08 директора с днем рождения", ConstantManager.TASK_TYPE_INDIVIDUAL,true,"Отправил открытку и СМС-ку"));
        temp.getPlan().add(new OrderPlanRealm(temp, cat1, 1500f));
        temp.getPlan().add(new OrderPlanRealm(temp, cat2, 5000f));
        temp.getPlan().add(new OrderPlanRealm(temp, cat3, 3500f));
        temp.getVisits().add(new VisitRealm(temp, "visit00001", dateFormat.parse("2018-08-01"), true));
        temp.getVisits().add(new VisitRealm(temp, "visit00002", dateFormat.parse("2018-08-05"), true));
        temp.getVisits().add(new VisitRealm(temp, "visit00003", dateFormat.parse("2018-08-09"), false));
        temp.getVisits().add(new VisitRealm(temp, "visit00004", dateFormat.parse("2018-08-12"), false));
        temp.getVisits().add(new VisitRealm(temp, "visit00005", dateFormat.parse("2018-08-15"), false));
        temp.getVisits().add(new VisitRealm(temp, "visit00006", dateFormat.parse("2018-08-16"), false));
        temp.getVisits().add(new VisitRealm(temp, "visit00007", dateFormat.parse("2018-08-21"), false));
        temp.getVisits().add(new VisitRealm(temp, "visit00008", dateFormat.parse("2018-08-25"), false));
        temp.getVisits().add(new VisitRealm(temp, "visit00009", dateFormat.parse("2018-08-31"), false));
        managedCustomers.add(temp);

        OrderRealm tmpOrder = new OrderRealm("ord00001", temp, dateFormat.parse("2018-08-01"), dateFormat.parse("2018-08-11"), ConstantManager.ORDER_STATUS_CART, ConstantManager.ORDER_PAYMENT_OFFICIAL, 1520f, "Заказ взял, клиет должен уточнить по количеству");
        tmpOrder.getLines().add(new OrderLineRealm(tmpOrder, item1, 10f, 12.5f));
        tmpOrder.getLines().add(new OrderLineRealm(tmpOrder, item21, 1f, 500f));
        tmpOrder.getLines().add(new OrderLineRealm(tmpOrder, item3, 5f, 30f));
        tmpOrder.getLines().add(new OrderLineRealm(tmpOrder, item5, 25f, 29.8f));
        managedOrders.add(tmpOrder);

        tmpOrder = new OrderRealm("ord00002", temp, dateFormat.parse("2018-07-03"), dateFormat.parse("2018-07-04"), ConstantManager.ORDER_STATUS_DELIVERED, ConstantManager.ORDER_PAYMENT_CASH, 10020.50f, "Отгрузка на среду, платит по факту");
        tmpOrder.getLines().add(new OrderLineRealm(tmpOrder, item11, 10f, 81.95f));
        tmpOrder.getLines().add(new OrderLineRealm(tmpOrder, item16, 30f, 90f));
        tmpOrder.getLines().add(new OrderLineRealm(tmpOrder, item22, 5f, 1300.2f));
        managedOrders.add(tmpOrder);

        tmpOrder = new OrderRealm("ord00003", temp, dateFormat.parse("2018-07-23"), dateFormat.parse("2018-07-25"), ConstantManager.ORDER_STATUS_SENT, ConstantManager.ORDER_PAYMENT_CASH, 6013.75f, "Отгрузка на среду, оплата на месте по факту");
        tmpOrder.getLines().add(new OrderLineRealm(tmpOrder, item6, 1000f, 4.5f));
        tmpOrder.getLines().add(new OrderLineRealm(tmpOrder, item17, 35f, 43.25f));
        managedOrders.add(tmpOrder);

        tmpOrder = new OrderRealm("ord00004", temp, dateFormat.parse("2018-08-05"), dateFormat.parse("2018-08-17"), ConstantManager.ORDER_STATUS_IN_PROGRESS, ConstantManager.ORDER_PAYMENT_OFFICIAL, 57770.00f, "Нужно привезти в пятницу, берет под клиента.");
        tmpOrder.getLines().add(new OrderLineRealm(tmpOrder, item10, 1500f, 9.5f));
        tmpOrder.getLines().add(new OrderLineRealm(tmpOrder, item18, 20f, 54f));
        tmpOrder.getLines().add(new OrderLineRealm(tmpOrder, item2, 2000f, 20f));
        tmpOrder.getLines().add(new OrderLineRealm(tmpOrder, item7, 200f, 12.2f));
        managedOrders.add(tmpOrder);

        //--------------------------
        temp = new CustomerRealm("cust0002","Автозапчасти магазин", "Денис Олегович", "Каменское, пр. Аношкина, 21", "222-77-55", "orders@ua.fm");
        temp.getDebt().add(new DebtRealm(temp,"UAH",500,18,false));
        temp.getVisits().add(new VisitRealm(temp, "visit00010", dateFormat.parse("2018-08-05"), true));
        temp.getVisits().add(new VisitRealm(temp, "visit00011", dateFormat.parse("2018-08-09"), false));
        temp.getVisits().add(new VisitRealm(temp, "visit00012", dateFormat.parse("2018-08-12"), false));
        temp.getVisits().add(new VisitRealm(temp, "visit00013", dateFormat.parse("2018-08-15"), false));
        temp.getVisits().add(new VisitRealm(temp, "visit00014", dateFormat.parse("2018-08-20"), false));
        managedCustomers.add(temp);

        tmpOrder = new OrderRealm("ord00005", temp, dateFormat.parse("2018-06-15"), dateFormat.parse("2018-06-20"), ConstantManager.ORDER_STATUS_DELIVERED, ConstantManager.ORDER_PAYMENT_OFFICIAL, 1652.15f, "");
        tmpOrder.getLines().add(new OrderLineRealm(tmpOrder, item11, 1f, 81.95f));
        tmpOrder.getLines().add(new OrderLineRealm(tmpOrder, item16, 3f, 90f));
        tmpOrder.getLines().add(new OrderLineRealm(tmpOrder, item22, 1f, 1300.2f));
        managedOrders.add(tmpOrder);

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
        temp.getVisits().add(new VisitRealm(temp, "visit00015", dateFormat.parse("2018-08-01"), true));
        temp.getVisits().add(new VisitRealm(temp, "visit00016", dateFormat.parse("2018-08-10"), false));
        temp.getVisits().add(new VisitRealm(temp, "visit00015", dateFormat.parse("2018-08-15"), false));
        temp.getVisits().add(new VisitRealm(temp, "visit00018", dateFormat.parse("2018-08-25"), false));
        temp.getVisits().add(new VisitRealm(temp, "visit00019", dateFormat.parse("2018-08-30"), false));
        managedCustomers.add(temp);
        //--------------------------
        temp = new CustomerRealm("cust0006","Владислав ЧП", "Владислав", "Днепр, пр. Богдана Хмельницкого, 150", "","");
        temp.getDebt().add(new DebtRealm(temp,"USD",130,130,true));
        temp.getDebt().add(new DebtRealm(temp,"UAH",250,9.50f,false));
        temp.getVisits().add(new VisitRealm(temp, "visit00020", dateFormat.parse("2018-08-01"), true));
        temp.getVisits().add(new VisitRealm(temp, "visit00021", dateFormat.parse("2018-08-05"), false));
        temp.getVisits().add(new VisitRealm(temp, "visit00022", dateFormat.parse("2018-08-15"), false));
        temp.getVisits().add(new VisitRealm(temp, "visit00023", dateFormat.parse("2018-08-25"), false));
        temp.getVisits().add(new VisitRealm(temp, "visit00024", dateFormat.parse("2018-08-30"), false));
        managedCustomers.add(temp);

        tmpOrder = new OrderRealm("ord00006", temp, dateFormat.parse("2018-08-07"), dateFormat.parse("2018-08-25"), ConstantManager.ORDER_STATUS_IN_PROGRESS, ConstantManager.ORDER_PAYMENT_OFFICIAL, 8945.00f, "плановый заказ");
        tmpOrder.getLines().add(new OrderLineRealm(tmpOrder, item10, 150f, 9.5f));
        tmpOrder.getLines().add(new OrderLineRealm(tmpOrder, item18, 20f, 54f));
        tmpOrder.getLines().add(new OrderLineRealm(tmpOrder, item2, 200f, 20f));
        tmpOrder.getLines().add(new OrderLineRealm(tmpOrder, item7, 200f, 12.2f));
        managedOrders.add(tmpOrder);


        realm.executeTransaction(db -> db.insertOrUpdate(managedCats));
        realm.executeTransaction(db -> db.insertOrUpdate(managedBrands));
        realm.executeTransaction(db -> db.insertOrUpdate(managedGroups));
        realm.executeTransaction(db -> db.insertOrUpdate(managedItems));
        realm.executeTransaction(db -> db.insertOrUpdate(managedCustomers));
        realm.executeTransaction(db -> db.insertOrUpdate(managedOrders));
        realm.close();
    }
}
