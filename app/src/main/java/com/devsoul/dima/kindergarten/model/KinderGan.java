package com.devsoul.dima.kindergarten.model;

import java.io.Serializable;

/**
 * This is the KinderGan class
 * @params ID, Name, Address, Classes, Phone
 */
public class KinderGan implements Serializable
{
    // Variables
    private int ID;
    private String Name;
    private String Address;
    private int Classes;
    private String Phone;

    // Constructor
    public KinderGan() {}

    public KinderGan(String Name, String Address)
    {
        this.Name = Name;
        this.Address = Address;
    }

    // Getters and Setters
    // ID
    public void SetID(int ID)
    {
        this.ID = ID;
    }
    public int GetID()
    {
        return this.ID;
    }

    // Name
    public void SetName(String Name)
    {
        this.Name = Name;
    }
    public String GetName()
    {
        return this.Name;
    }

    // Address
    public void SetAddress(String Address)
    {
        this.Address = Address;
    }
    public String GetAddress()
    {
        return this.Address;
    }

    // Classes
    public void SetClasses(int Classes)
    {
        this.Classes = Classes;
    }
    public int GetClasses()
    {
        return this.Classes;
    }

    // Phone
    public void SetPhone(String Phone)
    {
        this.Phone = Phone;
    }
    public String GetPhone()
    {
        return this.Phone;
    }
}
