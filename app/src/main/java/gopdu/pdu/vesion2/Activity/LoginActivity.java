package gopdu.pdu.vesion2.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

import gopdu.pdu.vesion2.Common;
import gopdu.pdu.vesion2.GoPDUApplication;
import gopdu.pdu.vesion2.MainActivity;
import gopdu.pdu.vesion2.R;
import gopdu.pdu.vesion2.databinding.ActivityLoginBinding;
import gopdu.pdu.vesion2.object.Customer;
import gopdu.pdu.vesion2.object.ServerResponse;
import gopdu.pdu.vesion2.viewmodel.CheckExitsViewModel;


public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private CheckExitsViewModel checkExitsViewModel;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(LoginActivity.this, R.layout.activity_login);
        init();
        ActionToolbar();
        setOnClick();
    }

    private void setOnClick() {

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Customer customer = new Customer();
                customer.setNumberphone(Common.formatPhoneNumber(binding.etPhone.getText().toString()));
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login() {
        String phone = Common.formatPhoneNumber(binding.etPhone.getText().toString().trim());
        if(phone.equals("")){
            Common.ShowToastShort(getString(R.string.errorPhone));
        }else {
            Map<String, String> map = new HashMap<>();
            map.put("driverOrCustomer","Customer");
            map.put("numberphone",Common.formatPhoneNumber(phone));
            checkExitsViewModel.CheckExitsAccount(map).observe(this, new Observer<ServerResponse>() {
                @Override
                public void onChanged(ServerResponse serverResponse) {
                    if(serverResponse != null){
                        if(serverResponse.getSuccess()){
                            Customer customer = new Customer();
                            customer.setNumberphone(Common.formatPhoneNumber(binding.etPhone.getText().toString()));
                            Intent intent = new Intent(LoginActivity.this, ComfirmOtpActivity.class);
                            intent.putExtra("Customer",customer);
                            intent.putExtra("from","Login");
                            startActivity(intent);
                        }else {
                            Common.ShowToastShort(serverResponse.getMessenger());
                        }
                    }else {
                        Common.ShowToastShort(getString(R.string.checkConnect));
                    }
                }
            });
        }
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        mStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                ProgressDialog progress = new ProgressDialog(LoginActivity.this);
                progress.setMessage(getString(R.string.waiting));
                progress.show();
                String user = firebaseAuth.getUid();
                if(user != null){
                    Intent intent = new Intent(LoginActivity.this, CustomerUseMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                progress.dismiss();
            }
        };
        checkExitsViewModel = ViewModelProviders.of(this).get(CheckExitsViewModel.class);
    }

    private void ActionToolbar() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mStateListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mStateListener);
    }
}
