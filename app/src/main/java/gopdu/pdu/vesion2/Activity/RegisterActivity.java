package gopdu.pdu.vesion2.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import gopdu.pdu.vesion2.Common;
import gopdu.pdu.vesion2.Object.Customer;
import gopdu.pdu.vesion2.R;
import gopdu.pdu.vesion2.Service.APIService;
import gopdu.pdu.vesion2.Service.DataService;
import gopdu.pdu.vesion2.databinding.ActivityRegisterBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private DataService dataService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(RegisterActivity.this, R.layout.activity_register);

        init();
        ActionToolbar();
        setUpOnClick();
    }

    private void init() {
        dataService = APIService.getService();
    }

    private void setUpOnClick() {
        final Date dateNow = Calendar.getInstance().getTime();
        final DatePickerDialog dateDialog = new DatePickerDialog(binding.getRoot().getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        binding.btnDateOfBirth.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },
                dateNow.getYear()+1900,
                dateNow.getMonth(),
                dateNow.getDate());
        Log.d("BBB", "setUpOnClick: "+dateNow.getYear());
        binding.btnDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog.show();
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = true;
                if(binding.etfirstName.getText().toString().trim().equals("")) {
                    check = false;
                    binding.etfirstName.setError(getString(R.string.errorFirstName));
                }
                if(binding.etLastname.getText().toString().trim().equals("")){
                    check = false;
                    binding.etLastname.setError(getString(R.string.errorLastName));
                }
                if(binding.btnDateOfBirth.getText().toString().equals(getString(R.string.pickADate))){
                    check = false;
                    Common.ShowToastShort(getString(R.string.errorDate));
                }
                if(binding.etEmail.getText().toString().trim().equals("")){
                    check = false;
                    binding.etEmail.setError(getString(R.string.errorEmail));
                }
                if(binding.etPhone.getText().toString().trim().equals("")){
                    check = false;
                    binding.etPhone.setError(getString(R.string.errorPhone));
                }
                if(check == true){
                    Map<String, String> params = new HashMap<>();
                    params.put("driverOrCustomer", "Customer");
                    params.put("numberphone",Common.editPhoneNumber(binding.etPhone.getText().toString()));
                    retrofit2.Call<String> checkExits = dataService.checkExits(params);
                    checkExits.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if(response != null){
                                if(response.body().equals("false")){
                                    Customer customer = new Customer();
                                    customer.setName(binding.etfirstName.getText() +" "+binding.etLastname.getText());
                                    customer.setBirthDate(binding.btnDateOfBirth.getText().toString());
                                    customer.setEmail(binding.etEmail.getText().toString());
                                    customer.setNumberphone(Common.editPhoneNumber(binding.etPhone.getText().toString()));
                                    Intent intent = new Intent(RegisterActivity.this, ComfirmOtpActivity.class);
                                    intent.putExtra("Customer",customer);
                                    startActivity(intent);
                                }else {
                                    Common.ShowToastShort(getString(R.string.exitsAccount));
                                }
                                Log.d("BBB", "onResponse: "+response.body());
                            }else {
                                Common.ShowToastShort(getString(R.string.checkConnect));
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.d("BBB", "onFailure: "+t.getMessage());
                        }
                    });
                }

            }
        });

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
