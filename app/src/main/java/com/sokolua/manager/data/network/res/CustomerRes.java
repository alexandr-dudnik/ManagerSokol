package com.sokolua.manager.data.network.res;

import android.support.annotation.Keep;

import java.util.List;

@Keep
public class CustomerRes {
    private String id;
    private String name;
    private String contact_name;
    private String address;
    private String phone;
    private String email;
    private String category;
    private List<DebtRes> debt;
    private List<NoteRes> notes;
    private List<TaskRes> tasks;
    private List<OrderPlanRes> plan;
    private List<CustomerDiscountRes> discounts;
    private List<VisitRes> visits;
    private Boolean active;

    public CustomerRes(String id, String name, String contact_name, String address, String phone, String email, String category, List<DebtRes> debt, List<NoteRes> notes, List<TaskRes> tasks, List<OrderPlanRes> plan, List<CustomerDiscountRes> discounts, List<VisitRes> visits, Boolean active) {
        this.id = id;
        this.name = name;
        this.contact_name = contact_name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.category = category;
        this.debt = debt;
        this.notes = notes;
        this.tasks = tasks;
        this.plan = plan;
        this.discounts = discounts;
        this.visits = visits;
        this.active = active;
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

    public String getPhone() {
        return phone;
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

    //endregion ====================  Getters  =========================
}
