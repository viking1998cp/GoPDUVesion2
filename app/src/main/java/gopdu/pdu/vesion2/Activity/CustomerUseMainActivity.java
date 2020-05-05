package gopdu.pdu.vesion2.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import gopdu.pdu.vesion2.IOnBackPressed;
import gopdu.pdu.vesion2.R;
import gopdu.pdu.vesion2.databinding.ActivityCustomerUseMainBinding;
import gopdu.pdu.vesion2.fragment.CustomerMap_Fragment;
import gopdu.pdu.vesion2.fragment.HistoryFragment;

public class CustomerUseMainActivity extends AppCompatActivity {

    private ActivityCustomerUseMainBinding binding;
    private int back =1;
    private Fragment customerFragment;
    private Fragment historyFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(CustomerUseMainActivity.this, R.layout.activity_customer_use_main);
        init();
        setUpMenuOnClick();
    }

    private void init() {
        customerFragment = new CustomerMap_Fragment();
        historyFragment = new HistoryFragment();
        loadFragment(customerFragment, R.id.navigation_book);

    }

    private void setUpMenuOnClick() {
        binding.navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_book:
                        if(back != 1){
                            back =1;
                            loadFragment(customerFragment, R.id.navigation_book);
                        }
                        return true;
                    case R.id.navigation_history:
                        if(back != 2){
                            loadFragment(historyFragment, R.id.navigation_history);
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
    public void loadFragment(Fragment fragment, int id) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (fragment.isAdded()) {
            fragmentTransaction.show(fragment);
        } else {
            fragmentTransaction.addToBackStack(id + "stack_item");
            fragmentTransaction.replace(R.id.frame_container, fragment);

        }
        fragmentTransaction.commit();
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
