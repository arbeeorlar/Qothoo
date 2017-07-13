package com.app.qootho.Model;

/**
 * Created by macbookpro on 08/07/2017.
 */

public class RiderAccount {


    String AccountType;
    String Image;
    boolean isActive;
    String AccountName;

    public void  setAccountType(String AccountType){
        this.AccountType = AccountType;
    }

    public String getAccountType(){
        return AccountType;
    }

    public void  setIsActive(boolean isActive){
        this.isActive = isActive;
    }

    public boolean getIsActive(){
        return isActive;
    }

    public void  setAccountNamee(String AccountName){
        this.AccountName = AccountName;
    }

    public String getAccountName(){
        return AccountName;
    }
}
