package gopdu.pdu.vesion2.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import gopdu.pdu.vesion2.Common;
import gopdu.pdu.vesion2.IOnBackPressed;
import gopdu.pdu.vesion2.R;
import gopdu.pdu.vesion2.databinding.ActivityCustomerUseMainBinding;
import gopdu.pdu.vesion2.fragment.CustomerMap_Fragment;

public class CustomerUseMainActivity extends AppCompatActivity {

    private ActivityCustomerUseMainBinding binding;
    private int back =1;
    private Fragment customerFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(CustomerUseMainActivity.this, R.layout.activity_customer_use_main);
        init();
        setUpMenuOnClick();
    }

    private void init() {
        customerFragment = new CustomerMap_Fragment();
        loadFragment(customerFragment);
    }

    private void setUpMenuOnClick() {
        binding.navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.navigation_gifts:
                        if(back != 1){
                            back =1;
                            loadFragment(customerFragment);
                        }
                        return true;
                    case R.id.navigation_cart:
                        if(back != 2){
//                            fragment = new HistoryCustomerFragment();
//                            loadFragment(fragment);
                            back =2;
                        }

                        return true;
                    case R.id.navigation_profile:
                        if(back != 3){
//                            fragment = new AboutFragment();
//                            loadFragment(fragment);
                            back =3;
                        }
                        return true;
                }

                return false;
            }
        });
    }
    public void loadFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment,"MyFragment");
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getSupportFragmentManager().findFragmentByTag("MyFragment") != null)
            getSupportFragmentManager().findFragmentByTag("MyFragment").setRetainInstance(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getSupportFragmentManager().findFragmentByTag("MyFragment") != null)
            getSupportFragmentManager().findFragmentByTag("MyFragment").getRetainInstance();
    }

    @Override
    public void onBackPressed() {
        if(back==1){
            if (!(customerFragment instanceof IOnBackPressed) || !((IOnBackPressed) customerFragment).onBackPressed()) {
                super.onBackPressed();
            }
        }
    }
}
