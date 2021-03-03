package com.redsponge.gltest.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.redsponge.gltest.R;
import com.redsponge.gltest.list.RoomListActivity;

public class AuthActivity extends FragmentActivity {

    private ViewPager2 vpContent;
    private AuthPagerAdapter pagerAdapter;

    private Button btnOtherOption;
    private TextView tvOtherOptionTitle;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        auth = FirebaseAuth.getInstance();

        vpContent = findViewById(R.id.vpContent);

        pagerAdapter = new AuthPagerAdapter(this);
        vpContent.setAdapter(pagerAdapter);
        vpContent.setUserInputEnabled(false);

        vpContent.setOffscreenPageLimit(1);

        btnOtherOption = findViewById(R.id.btnOtherOption);
        tvOtherOptionTitle = findViewById(R.id.tvOtherOptionTitle);
    }


    public void switchAuthOption(View view) {
        int nextItem = (vpContent.getCurrentItem() + 1) % pagerAdapter.getItemCount();
        btnOtherOption.setText(pagerAdapter.getAuthOption(nextItem).getSwitchButtonText());
        tvOtherOptionTitle.setText(pagerAdapter.getAuthOption(nextItem).getSwitchTitle());

        vpContent.setCurrentItem(nextItem, true);
    }

    public void tryLogIn() {
        // TODO: Access auth and check if the user exists
        FirebaseUser user = auth.getCurrentUser();
        if(user == null) return;

        Intent intent = new Intent(this, RoomListActivity.class);
        finish();
        startActivity(intent);
    }
}
