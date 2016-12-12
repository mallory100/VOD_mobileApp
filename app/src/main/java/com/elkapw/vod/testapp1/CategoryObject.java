package com.elkapw.vod.testapp1;

import java.io.Serializable;

/**
 * Created by Majka on 2016-09-25.
 */
public class CategoryObject implements Serializable {

    private int categoryID;
    private String categoryName;
    private String categoryDescription;
    private int isFree;


    public CategoryObject(){


    }


    public CategoryObject(int mCategoryID, String mCategoryName, String mCategoryDescription) {
        this.categoryID = mCategoryID;
        this.categoryName = mCategoryName;
        this.categoryDescription = mCategoryDescription;

    }

    public int getCategoryID() {          return categoryID;     }
    public void setCategoryID(int categoryID) {        this.categoryID = categoryID;     }

    public String getCategoryName() {        return categoryName;    }
    public void setCategoryName(String categoryName) {        this.categoryName = categoryName;    }

    public String getCategoryDescription() {        return categoryDescription;    }
    public void setCategoryDescription(String categoryDescription) {        this.categoryDescription = categoryDescription;    }

    public int getIsFree() {         return isFree;     }
    public void setIsFree(int isFree) {        this.isFree = isFree;    }



}
