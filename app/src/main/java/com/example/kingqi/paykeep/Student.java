package com.example.kingqi.paykeep;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Student extends LitePalSupport implements Serializable {
    private int year,month,day;
    private String name;
    private double money;
    private String group;
    private int Firstweekcheck, Secondweekcheck, Thirdweekcheck, Forthweekcheck;
    private boolean isPrivate;
    private String id;
    private boolean uploaded;

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public void setGroup(String group){
        this.group = group;
    }

    public void setCheck(int firstweekcheck, int secondweekcheck, int thirdweekcheck, int forthweekcheck){
        this.Firstweekcheck = firstweekcheck;
        this.Secondweekcheck = secondweekcheck;
        this.Thirdweekcheck = thirdweekcheck;
        this.Forthweekcheck = forthweekcheck;
    }

    @Override
    public String toString() {
        return year+"/"+month+"/"+day+" "+name+" money:"+money+" isPrivate:"+isPrivate+"\n";
    }

    public static List<Student> createTestListStudents(int num){
        List<Student> students = new ArrayList<>();
        for (int i =0;i<num;i++){
            Student student = new Student();
            student.setId("M0123456");
            student.setName("匿名");
            student.setGroup("0");
            student.setCheck(0,0,0,0);
            students.add(student);
            student.save();
        }
        return students;
    }
}
