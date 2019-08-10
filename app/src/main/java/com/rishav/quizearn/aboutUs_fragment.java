package com.rishav.quizearn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class aboutUs_fragment extends Fragment {

    public aboutUs_fragment(){
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Element versionElement = new Element();
        versionElement.setTitle("Version 1.0");
        return new AboutPage(getContext())
                .isRTL(false)
                .setDescription("Choose any category of your choice and play Quiz . Get your Score and Earn money according to your performance. Come daily to play and get daily Coins. Convert Your coins and transfer money to your banks.Play Quiz of your choice and collect coins.Spin wheel and watch ads to collect more coins. Come daily to this app and get Free coins. Invite your friends to this app and get coins when they play Quiz.Using the app collect coins. After collecting coins(minimum=50,000) withdraw money According to coins collected in your wallet. You are able to withdraw your money by google pay.")
                .enableDarkMode(false)
                .addGroup("Connect with us")
                .addEmail("rishavchanda0@gmail.com","Email")
                .addWebsite("https://f9lzboyhvq9wnsrpqdo0aq-on.drv.tw/www.rishavchandaportfolio.ml/")
                .addFacebook("rishav.chanda.165")
                .addTwitter("RishavChanda")
                .addPlayStore(getContext().getPackageName())
                .addGitHub("rishavchanda")
                .addInstagram("rishav50")
                .addItem(versionElement)
                .create();
    }

}
