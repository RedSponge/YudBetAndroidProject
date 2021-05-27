package com.redsponge.carddeck.auth;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AuthPagerAdapter extends FragmentStateAdapter {

    private final AuthOption[] options;

    public AuthPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        options = new AuthOption[] {
                new AuthOption(new AuthLoginFragment(), "Register", "Don't have an account?"),
                new AuthOption(new AuthRegisterFragment(), "Login", "Already have an account?")
        };
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return options[position].getFragment();
    }

    public AuthOption getAuthOption(int position) {
        return options[position];
    }

    @Override
    public int getItemCount() {
        return options.length;
    }
}
