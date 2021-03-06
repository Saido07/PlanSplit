package com.example.plansplit.Models.Objects;

import android.net.Uri;

import com.example.plansplit.R;

public class Friend{
    private String person_image;
    private String name;
    private String amount;
    private int amount_text;
    private int layout_background;
    private int image_background;
    private int color;
    private String key;
    private String mail;
    private String friendshipsKey;
    private int cardView_addgroupsPicture;

    public int getCardView_addgroupsPicture() {
        return cardView_addgroupsPicture;
    }

    public String getMail() {
        return mail;
    }

    public String getFriendshipsKey() {
        return friendshipsKey;
    }

    public void setFriendshipsKey(String friendshipsKey) {      //pay classında group id yi grup elemanından almak gerekiyordu o durumdam buraya group id geliyor.
        this.friendshipsKey = friendshipsKey;
    }

    public Friend(String person_image, String name, float amount, String key){
        if(!person_image.isEmpty()){

            this.person_image = person_image;
        }

        this.name = name;
        this.key = key;

        this.amount = amount + " TL";

        //eğer kullanıcı arkadaşa borçlu ise
        if(amount <= 0){
            this.layout_background = R.drawable.itemview_bg_border_red;
            this.image_background = R.drawable.circle_background_red;
            this.color = R.color.red;
            this.amount_text =  R.string.friend_amount_user_owes;//borçlusun
        }else {
            this.layout_background = R.drawable.itemview_bg_border_green;
            this.image_background = R.drawable.circle_background_green;
            this.color = R.color.brightGreen;
            this.amount_text = R.string.friend_amount_friend_owes;//borçlu
        }
    }

    public Friend(String person_image, String name, String key){
        this.person_image=person_image;
        this.name=name;
        this.key=key;
    }

    public Uri getPerson_image(){
        return Uri.parse(person_image);
    }

    public String getName(){
        return name;
    }

    public String getAmount(){
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = String.valueOf(amount);
        if(amount>0){
            this.layout_background = R.drawable.itemview_bg_border_red;
            this.image_background = R.drawable.circle_background_red;
            this.color = R.color.red;
            this.amount_text =  R.string.friend_amount_user_owes;//borçlusun
        }else {
            this.layout_background = R.drawable.itemview_bg_border_green;
            this.image_background = R.drawable.circle_background_green;
            this.color = R.color.brightGreen;
            this.amount_text = R.string.friend_amount_friend_owes;//borç yok
        }

    }

    public int getAmount_text(){
        return amount_text;
    }

    public int getLayout_background(){
        return layout_background;
    }

    public int getImage_background(){
        return image_background;
    }

    public int getColor(){
        return color;
    }

    public String getKey(){return  this.key;}
}