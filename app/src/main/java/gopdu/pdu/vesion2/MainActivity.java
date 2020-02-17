package gopdu.pdu.vesion2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import gopdu.pdu.vesion2.Activity.RegisterActivity;
import gopdu.pdu.vesion2.Adapter.BannerAdapter;
import gopdu.pdu.vesion2.Object.Banner;
import gopdu.pdu.vesion2.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int currentPage = 0;
    private Timer timer;
    private final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    private final long PERIOD_MS = 1500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

        setupBanner();
        setupOnClick();
        autoBanner();
    }

    private void autoBanner() {

        binding.viewPagerVoucher.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
                binding.viewPagerVoucher.setCurrentItem(currentPage++, true);
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
    }

    private ArrayList<Banner> banners = new ArrayList<>();
    private void setupBanner() {

        banners.add(new Banner(0, "http://gopdu.000webhostapp.com/Banner/ic_banner_1.jpg"));
        banners.add(new Banner(0, "http://gopdu.000webhostapp.com/Banner/ic_banner_2.png"));
        banners.add(new Banner(0,"http://gopdu.000webhostapp.com/Banner/ic_banner_3.png"));
        BannerAdapter bannerAdapter = new BannerAdapter(banners);
        binding.viewPagerVoucher.setAdapter(bannerAdapter);
        binding.indicatorVoucher.setViewPager(binding.viewPagerVoucher);
    }
}
