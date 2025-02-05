package com.sp.mad;

public class Item {
    private String title;
    private String price;
    private int imageResId;
    private String accountSeller;
    private String condition;
    private String school;
    private String course;
    private String description;

    public Item(String title, String price, int imageResId, String accountSeller, String condition, String school, String course, String description) {
        this.title = title;
        this.price = price;
        this.imageResId = imageResId;
        this.accountSeller = accountSeller;
        this.condition = condition;
        this.school = school;
        this.course = course;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public int getImageResid() {
        return imageResId;
    }
    public String getAccountSeller() {
        return accountSeller;
    }

    public String getCondition() {
        return condition;
    }

    public String getSchool() {
        return school;
    }

    public String getCourse() {
        return course;
    }

    public String getDescription() {
        return description;
    }
}
