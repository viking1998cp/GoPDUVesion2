package gopdu.pdu.vesion2.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import gopdu.pdu.vesion2.Common;
import gopdu.pdu.vesion2.object.Customer;
import gopdu.pdu.vesion2.R;
import gopdu.pdu.vesion2.service.APIService;
import gopdu.pdu.vesion2.service.DataService;
import gopdu.pdu.vesion2.databinding.ActivityComfirmOtpBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComfirmOtpActivity extends AppCompatActivity {

    private ActivityComfirmOtpBinding binding;
    private Customer customer;
    private FirebaseAuth mAuth;
    private String VerifyCationId;
    private DataService dataService;
    private int timer = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ComfirmOtpActivity.this, R.layout.activity_comfirm_otp);
        init();
        ActionToolbar();
        setOnClick();
//        SendVerifyCode(customer.getNumberphone());
        setTimerOTP();
    }

    private void setTimerOTP() {
        timer = 60;
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer--;
                binding.tvTimer.setText(getString(R.string.timerotp,(timer)));
                //here you can have your logic to set text to edittext
                Log.d("BBB", "onTick: "+timer);
            }

            public void onFinish() {
                binding.tvTimer.setText(getString(R.string.sendagainotp));
            }

        }.start();
    }

    private void setOnClick() {

        binding.btnAcvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.otpComfirm.length()==6){
                    if(Common.checkConnect()){
                        verifyCode(binding.otpComfirm.getText().toString());
                    }else {
                        Common.ShowToastShort(getString(R.string.checkConnect));
                    }

                }else {
                    Common.ShowToastShort(getString(R.string.otpEmpty));
                }
            }
        });

        binding.tvTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timer ==0){
                    SendVerifyCode(customer.getNumberphone());
                    setTimerOTP();
                }
            }
        });
    }

    private void init() {

        mAuth = FirebaseAuth.getInstance();
        if(getIntent().getSerializableExtra("Customer") != null) {
            customer = (Customer) getIntent().getSerializableExtra("Customer");
        }
        binding.tvHelper.setText(getString(R.string.helperActivityOTP, customer.getNumberphone()));
        dataService = APIService.getService();

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

    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerifyCationId,code); //Kiểm tra mã code được
        SigInWithCredential(credential);
    }
    private void SigInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    insertAccount();
                    Intent intent = new Intent(ComfirmOtpActivity.this,CustomerUseMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }else {
                    Common.ShowToastShort(getString(R.string.otpError));
                }
            }
        });
    }

    private void insertAccount() {

        Map<String, String> param = new HashMap<>();
        param.put("id", mAuth.getUid());
        param.put("name", customer.getName());
        param.put("numberphone", customer.getNumberphone());
        param.put("email", customer.getEmail());
        param.put("birthDate", customer.getBirthDate());
        retrofit2.Call<String> insertAccount = dataService.registerAccount(param);

        insertAccount.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response != null){
                    if(response.body().equals("Register success")){
                        Common.ShowToastShort(getString(R.string.registerSuccess));
//                        Intent intent = new Intent(ComfirmOtpActivity.this, CustomerUseMainActivity.class);
                    }
                }else {
                    Common.ShowToastShort(getString(R.string.checkConnect));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void SendVerifyCode(String PhoneNumber){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(PhoneNumber
                ,60
                , TimeUnit.SECONDS
                , TaskExecutors.MAIN_THREAD
                ,mcallback);

    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mcallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            VerifyCationId =s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.d("BBB", "onVerificationFailed: "+e.toString());
        }
    };
}
