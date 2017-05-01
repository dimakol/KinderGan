package com.devsoul.dima.kindergarten.model;

import java.io.Serializable;

/**
 * This is the Kid class
 * @params Name, BirthDate, Picture, Class, Created_at
 */
public class Kid implements Serializable
{
    // Variables
    private String Name;
    private String BirthDate;
    private String Picture;
    private String Class;
    private String Parent_ID;
    private String Created_at;

    // Constructor
    public Kid() {}

    public Kid(String Name, String Class)
    {
        this.Name = Name;
        this.Class = Class;
    }

    public Kid(String Name, String Class, String BirthDate)
    {
        this.Name = Name;
        this.Class = Class;
        this.BirthDate = BirthDate;
    }

    public Kid(String Name, String Class, String BirthDate, String Picture)
    {
        this.Name = Name;
        this.Class = Class;
        this.BirthDate = BirthDate;
        this.Picture = Picture;
    }

    // Getters and Setters
    // Name
    public void SetName(String Name)
    {
        this.Name = Name;
    }
    public String GetName()
    {
        return this.Name;
    }

    // Birth Date
    public void SetBirthDate(String BirthDate)
    {
        this.BirthDate = BirthDate;
    }
    public String GetBirthDate()
    {
        return this.BirthDate;
    }

    // Picture
    public void SetPicture(String Pic)
    {
        this.Picture = Pic;
    }
    public String GetPicture()
    {
        return this.Picture;
    }

    // Class
    public void SetClass(String Class)
    {
        this.Class = Class;
    }
    public String GetClass()
    {
        return this.Class;
    }

    // Parent ID
    public void SetParentID(String ID)
    {
        this.Parent_ID = ID;
    }
    public String GetParentID()
    {
        return this.Parent_ID;
    }

    // Created_at
    public void SetCreatedAt(String Created_at)
    {
        this.Created_at = Created_at;
    }
    public String GetCreatedAt()
    {
        return this.Created_at;
    }
}
