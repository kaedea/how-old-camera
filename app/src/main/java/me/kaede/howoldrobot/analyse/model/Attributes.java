/*
 * Copyright (c) 2016. Kaede
 */

package me.kaede.howoldrobot.analyse.model;

public class Attributes {

    public int age;
    public String gender;

    @Override
    public String toString() {
        return "Attributes{" +
                "gender='" + gender + '\'' +
                ", age=" + age +
                '}';
    }
}
