package gopdu.pdu.vesion2.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import gopdu.pdu.vesion2.Common;
import gopdu.pdu.vesion2.R;
import gopdu.pdu.vesion2.databinding.ActivityLoginBinding;
import gopdu.pdu.vesion2.object.Customer;
import gopdu.pdu.vesion2.service.APIService;
import gopdu.pdu.vesion2.service.DataService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private DataService dataService;
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
                String phone = Common.formatPhoneNumber(binding.etPhone.getText().toString().trim());
                if(phone.equals("")){
                    Common.ShowToastShort(getString(R.string.errorPhone));
                }else {
                    Map<String, String> map = new HashMap<>();
                    map.put("driverOrCustomer","Customer");
                    map.put("numberphone",phone);
                    retrofit2.Call<String> checkExits = dataService.checkExits(map);
                    checkExits.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if(response.body() != null){
                                if(response.body().equals("true")){
                                    Customer customer = new Customer();
                                    customer.setNumberphone(Common.formatPhoneNumber(binding.etPhone.getText().toString()));
                                    Intent intent = new Intent(LoginActivity.this, ComfirmOtpActivity.class);
                                    intent.putExtra("Customer",customer);
                                    startActivity(intent);
                                }else {
                                    Common.ShowToastShort(getString(R.string.notExitsAccount));
                                }
                            }else {
                                Common.ShowToastShort(getString(R.string.checkConnect));
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Common.ShowToastShort(getString(R.string.checkConnect));
                        }
                    });
                }
            }
        });
    }

    private void init() {
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
}
