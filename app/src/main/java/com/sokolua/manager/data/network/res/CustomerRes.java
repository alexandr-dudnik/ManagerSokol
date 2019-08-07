package com.sokolua.manager.data.network.res;

import java.util.List;

import io.realm.internal.Keep;

@Keep
public class CustomerRes {
    private String id;
    private String name;
    private String contact_name;
    private String address;
    private String email;
    private String category;
    private Boolean active;
    private CustomerConditionRes trade_condition;

    private List<DebtRes> debt;
    private List<NoteRes> notes;
    private List<TaskRes> tasks;
    private List<OrderPlanRes> plan;
    private List<CustomerDiscountRes> discounts;
    private List<VisitRes> visits;
    private List<String> phones;

    public CustomerRes(String id, String name, String contact_name, String address, String email, String category, List<DebtRes> debt, List<NoteRes> notes, List<TaskRes> tasks, List<OrderPlanRes> plan, List<CustomerDiscountRes> discounts, List<VisitRes> visits, List<String> phones, Boolean active, CustomerConditionRes trade_condition) {
        this.id = id;
        this.name = name;
        this.contact_name = contact_name;
        this.address = address;
        this.email = email;
        this.category = category;
        this.debt = debt;
        this.notes = notes;
        this.tasks = tasks;
        this.plan = plan;
        this.discounts = discounts;
        this.visits = visits;
        this.phones = phones;
        this.active = active;
        this.trade_condition = trade_condition;
    }

//region =======================  Getters  =========================

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContactName() {
        return contact_name;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getCategory() {
        return category;
    }

    public List<DebtRes> getDebt() {
        return debt;
    }

    public List<NoteRes> getNotes() {
        return notes;
    }

    public List<TaskRes> getTasks() {
        return tasks;
    }

    public List<OrderPlanRes> getPlan() {
        return plan;
    }

    public List<CustomerDiscountRes> getDiscounts() {
        return discounts;
    }

    public List<VisitRes> getVisits() {
        return visits;
    }

    public Boolean isActive() {
        return active==null?false:active;
    }

    public List<String> getPhones() {
        return phones;
    }

    public CustomerConditionRes getTradeCondition() {
        return trade_condition;
    }

    //endregion ====================  Getters  =========================

    //region ============================== Class CustomerConditionRes =================

    @Keep
    public static class CustomerConditionRes {
        private String price;
        private String cash;
        private String official;

        public CustomerConditionRes(String price, String cash, String official) {
            this.price = price;
            this.cash = cash;
            this.official = official;
        }

        //region ============================== Getters =================

        public String getPrice() {
            return price;
        }

        public String getCash() {
            return cash;
        }

        public String getOfficial() {
            return official;
        }

        //endregion =========================== Getters =================
    }

    //endregion =========================== Class CustomerConditionRes =================

    //region ============================== Class CustomerDiscountsRes =================

    @Keep
    public static class CustomerDiscountRes {
        private int type;
        private String target_id;
        private String target_name;
        private Float percent = 0f;

        public CustomerDiscountRes(int type, String target_id, String target_name, Float percent) {
            this.type = type;
            this.target_id = target_id;
            this.target_name = target_name;
            this.percent = percent;
        }

        //region =======================  Getters  =========================

        public int getType() {
            return type;
        }

        public String getTargetId() {
            return target_id;
        }

        public String getTargetName() {
            return target_name;
        }

        public Float getPercent() {
            return percent;
        }


        //endregion ====================  Getters  =========================
    }

    //endregion =========================== Class CustomerDiscountsRes =================

    //region ============================== Class DebtRes =================

    @Keep
    public static class DebtRes {
        private String currency;
        private float amount;
        private float amountUSD;
        private boolean outdated;

        public DebtRes(String currency, float amount, float amountUSD, boolean outdated) {
            this.currency = currency;
            this.amount = amount;
            this.amountUSD = amountUSD;
            this.outdated = outdated;
        }

        //region =======================  Getters  =========================

        public String getCurrency() {
            return currency;
        }

        public float getAmount() {
            return amount;
        }

        public float getAmountUSD() {
            return amountUSD;
        }

        public boolean isOutdated() {
            return outdated;
        }


        //endregion ====================  Getters  =========================
    }

    //endregion =========================== Class DebtRes =================

    //region ============================== Class VisitRes =================

    @Keep
    public static class VisitRes {
        private String id;
        private String date;
        private boolean done;

        public VisitRes(String id, String date, boolean done) {
            this.id = id;
            this.date = date;
            this.done = done;
        }

        //region =======================  Getters  =========================

        public String getId() {
            return id;
        }

        public String getDate() {
            return date;
        }

        public boolean isDone() {
            return done;
        }


        //endregion ====================  Getters  =========================

    }

    //endregion =========================== Class VisitRes =================

    //region ============================== Class TaskRes =================

    @Keep
    public static class TaskRes {
        private String id;
        private String date;
        private String text;
        private Integer type;
        private boolean done;
        private String result;

        public TaskRes(String id, String date, String text, Integer type, boolean done, String result) {
            this.id = id;
            this.date = date;
            this.text = text;
            this.type = type;
            this.done = done;
            this.result = result;
        }

        //region =======================  Getters  =========================

        public String getId() {
            return id;
        }

        public String getText() {
            return text;
        }

        public Integer getType() {
            return type;
        }

        public String getDate() {
            return date;
        }

        public String getResult() {
            return result;
        }

        public boolean isDone() {
            return done;
        }

        //endregion ====================  Getters  =========================


    }

    //endregion =========================== Class TaskRes =================

    //region ============================== Class OrderPlanRes =================

    @Keep
    public static class OrderPlanRes {
        private String category_id;
        private String category_name;
        private float amount;

        public OrderPlanRes(String category_id, String category_name, float amount) {
            this.category_id = category_id;
            this.category_name = category_name;
            this.amount = amount;
        }

        //region =======================  Getters  =========================

        public String getCategoryId() {
            return category_id;
        }

        public float getAmount() {
            return amount;
        }

        public String getCategoryName() {
            return category_name;
        }

        //endregion ====================  Getters  =========================



    }

    //endregion =========================== Class OrderPlanRes =================

    //region ============================== Class NotesRes =================

    @Keep
    public static class NoteRes {
        private String id;
        private String date;
        private String text;

        public NoteRes(String id, String date, String text) {
            this.id = id;
            this.date = date;
            this.text = text;
        }

        //region =======================  Getters  =========================

        public String getId() {
            return id;
        }

        public String getDate() {
            return date;
        }

        public String getText() {
            return text;
        }


        //endregion ====================  Getters  =========================

    }
    //endregion =========================== Class NotesRes =================
}
