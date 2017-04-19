package com.android.efforts.activity;

import android.accounts.AccountAuthenticatorActivity;
import android.os.Bundle;

import com.android.efforts.R;

public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);
    }
}
