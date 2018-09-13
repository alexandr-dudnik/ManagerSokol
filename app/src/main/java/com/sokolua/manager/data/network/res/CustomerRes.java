package com.sokolua.manager.data.network.res;

import android.support.annotation.Keep;

import java.util.List;

@Keep
public class CustomerRes {
    public String id;
    public String name;
    public String contact_name;
    public String address;
    public String phone;
    public String email;
    public String category;
    private List<DebtRes> debt;
    private List<NoteRes> notes;
    private List<TaskRes> tasks;
    private List<OrderPlanRes> plan;
    private List<CustomerDiscountRes> discounts;
    private List<VisitRes> visits;

    public CustomerRes(String id, String name, String contact_name, String address, String phone, String email, String category, List<DebtRes> debt, List<NoteRes> notes, List<TaskRes> tasks, List<OrderPlanRes> plan, List<CustomerDiscountRes> discounts, List<VisitRes> visits) {
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


    //endregion ====================  Getters  =========================

    //region =======================  Setters  =========================

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContactName(String contact_name) {
        this.contact_name = contact_name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDebt(List<DebtRes> debt) {
        this.debt = debt;
    }

    public void setNotes(List<NoteRes> notes) {
        this.notes = notes;
    }

    public void setTasks(List<TaskRes> tasks) {
        this.tasks = tasks;
    }

    public void setPlan(List<OrderPlanRes> plan) {
        this.plan = plan;
    }

    public void setDiscounts(List<CustomerDiscountRes> discounts) {
        this.discounts = discounts;
    }

    public void setVisits(List<VisitRes> visits) {
        this.visits = visits;
    }


    //endregion ====================  Setters  =========================
}
