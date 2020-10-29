package com.entreprisecorp.proximityv2;

public class Person {

    private String name;
    private String firstname;
    private int age;
    private String email;

    public Person() {
    }

    public Person(String name, String firstname, int age, String email) {
        this.name = name;
        this.firstname = firstname;
        this.age = age;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", firstname='" + firstname + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                '}';
    }
}
