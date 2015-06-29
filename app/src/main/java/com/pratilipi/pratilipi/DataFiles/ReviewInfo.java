package com.pratilipi.pratilipi.DataFiles;

/**
 * Created by Bharatwaaj on 26-06-2015.
 */
public class ReviewInfo {
    public String userName;
    public String review;

    public ReviewInfo(){

    }
    public ReviewInfo(String s , String j){
        this.userName = s;
        this.review=j;
    }

    public void setUserName(String userName){
        this.userName=userName;
    }

    public void setReview(String review){
        this.review=review;
    }

    public String getUserName(){
        return userName;
    }
    public String getReview(){
        return review;
    }

}
