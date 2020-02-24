package gopdu.pdu.vesion2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import gopdu.pdu.vesion2.activity.CustomerUseMainActivity;
import gopdu.pdu.vesion2.activity.LoginActivity;
import gopdu.pdu.vesion2.activity.RegisterActivity;
import gopdu.pdu.vesion2.adapter.BannerAdapter;
import gopdu.pdu.vesion2.object.Banner;
import gopdu.pdu.vesion2.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int currentPage = 0;
    private Timer timer;
    private final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    private final long PERIOD_MS = 1500;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

        init();
        setupBanner();
        setupOnClick();
        autoBanner();
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        mStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                ProgressDialog progress = new ProgressDialog(MainActivity.this);
                progress.setMessage(getString(R.string.waiting));
                progress.show();
                String user = firebaseAuth.getUid();
                if(user != null){
                    Intent intent = new Intent(MainActivity.this, CustomerUseMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                progress.dismiss();
            }
        };
    }

    private void autoBanner() {

        binding.viewPagerService.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == banners.size()) {
                    currentPage = 0;
                }
                binding.viewPagerService.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
    }

    private void setupOnClick() {

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private ArrayList<Banner> banners = new ArrayList<>();
    private void setupBanner() {

        banners.add(new Banner(0, "http://gopdu.000webhostapp.com/Banner/ic_banner_1.jpg"));
        banners.add(new Banner(0, "http://gopdu.000webhostapp.com/Banner/ic_banner_2.png"));
        banners.add(new Banner(0,"http://gopdu.000webhostapp.com/Banner/ic_banner_3.png"));
        BannerAdapter bannerAdapter = new BannerAdapter(banners);
        binding.viewPagerService.setAdapter(bannerAdapter);
        binding.indicatorVoucher.setViewPager(binding.viewPagerService);
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
