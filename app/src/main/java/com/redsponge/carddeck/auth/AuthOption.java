package com.redsponge.carddeck.auth;

import androidx.fragment.app.Fragment;

public class AuthOption {

    private Fragment fragment;
    private String switchButtonText;
    private String switchTitle;

    public AuthOption(Fragment fragment, String switchButtonText, String switchTitle) {
        this.fragment = fragment;
        this.switchButtonText = switchButtonText;
        this.switchTitle = switchTitle;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public String getSwitchButtonText() {
        return switchButtonText;
    }

    public void setSwitchButtonText(String switchButtonText) {
        this.switchButtonText = switchButtonText;
    }

    public String getSwitchTitle() {
        return switchTitle;
    }

    public void setSwitchTitle(String switchTitle) {
        this.switchTitle = switchTitle;
    }
}
