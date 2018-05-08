package com.wisekiddo.liquid.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ronald on 28/4/18.
 *
 * Immutable model class for a User.
 */

@Entity(tableName = "User")
public class User {

    @SerializedName("id")
    @Expose
    @PrimaryKey
    @NonNull
    private Integer id;

    @SerializedName("name")
    @Expose
    @Nullable
    @ColumnInfo(name = "name")
    private String name;

    @SerializedName("username")
    @Expose
    @Nullable
    @ColumnInfo(name = "username")
    private String username;

    @Ignore
    @SerializedName("email")
    @Expose
    private String email;

    @Ignore
    @SerializedName("address")
    @Expose
    private Address address;

    @Ignore
    @SerializedName("phone")
    @Expose
    private String phone;

    @Ignore
    @SerializedName("website")
    @Expose
    private String website;

    @Ignore
    @SerializedName("company")
    @Expose
    private Company company;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }


    public User(String name, String username, Integer id) {
        this.id = id;
        this.name = name;
        this.username = username;
    }

}
