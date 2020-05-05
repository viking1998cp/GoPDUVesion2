package gopdu.pdu.vesion2.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import gopdu.pdu.vesion2.Common;
import gopdu.pdu.vesion2.object.Customer;
import gopdu.pdu.vesion2.R;
import gopdu.pdu.vesion2.object.ServerResponse;
import gopdu.pdu.vesion2.repository.CheckExitsAccountRepository;
import gopdu.pdu.vesion2.service.APIService;
import gopdu.pdu.vesion2.service.DataService;
import gopdu.pdu.vesion2.databinding.ActivityRegisterBinding;
import gopdu.pdu.vesion2.viewmodel.CheckExitsViewModel;
import gopdu.pdu.vesion2.viewmodel.ListServiceViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private CheckExitsViewModel checkExitsViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(RegisterActivity.this, R.layout.activity_register);
        init();
        ActionToolbar();
        setUpOnClick();
    }

    private void init() {
        checkExitsViewModel = ViewModelProviders.of(this).get(CheckExitsViewModel.class);
    }

    private void setUpOnClick() {
        final Date dateNow = Calendar.getInstance().getTime();
        final DatePickerDialog dateDialog = new DatePickerDialog(binding.getRoot().getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        binding.etbirthDate.setText(dayOfMonth+"/"+(month+1)+"/"+year);

                    }
                },
                dateNow.getYear()+1900,
                dateNow.getMonth(),
                dateNow.getDate());

        binding.etbirthDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    dateDialog.show();
                }
            }
        });

        binding.etbirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog.show();
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                register();
            }
        });

    }

    private void register() {
        boolean check = true;
        if(binding.etfirstName.getText().toString().trim().equals("")) {
            check = false;
            binding.etfirstName.setError(getString(R.string.errorFirstName));
        }
        if(binding.etLastname.getText().toString().trim().equals("")){
            check = false;
            binding.etLastname.setError(getString(R.string.errorLastName));
        }
        if(binding.etbirthDate.getText().toString().equals("")){
            check = false;
            binding.etbirthDate.setError(getString(R.string.errorDate));
        }
        if(binding.etEmail.getText().toString().trim().equals("")){
            check = false;
            binding.etEmail.setError(getString(R.string.errorEmail));
        } else if(!Common.checkEmail(binding.etEmail.getText().toString().trim())){
            check = false;
            binding.etEmail.setError(getString(R.string.errorEmailFormat));
        }
        if(binding.etPhone.getText().toString().trim().equals("")){
            check = false;
            binding.etPhone.setError(getString(R.string.errorPhone));
        }
        if(check == true){

            Map<String, String> params = new HashMap<>();
            params.put("driverOrCustomer", "Customer");
            params.put("numberphone",Common.formatPhoneNumber(binding.etPhone.getText().toString()));
            checkExitsViewModel.CheckExitsAccount(params).observe(this, new Observer<ServerResponse>() {
                @Override
                public void onChanged(ServerResponse serverResponse) {
                    if(serverResponse != null){
                        if(!serverResponse.getSuccess()){
                            Customer customer = new Customer();
                            customer.setName(binding.etfirstName.getText() +" "+binding.etLastname.getText());
                            customer.setBirthdate(binding.etbirthDate.getText().toString());
                            customer.setEmail(binding.etEmail.getText().toString());
                            customer.setNumberphone(Common.formatPhoneNumber(binding.etPhone.getText().toString()));
                            Intent intent = new Intent(RegisterActivity.this, ComfirmOtpActivity.class);
                            intent.putExtra("Customer",customer);
                            intent.putExtra("from","Register");
                            startActivity(intent);
                        }else {
                            Common.ShowToastShort(getString(R.string.exitsAccount));
                        }
                    }
                }
            });

        }
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
