package gopdu.pdu.vesion2.adapter;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import gopdu.pdu.vesion2.object.Banner;
import gopdu.pdu.vesion2.R;
import gopdu.pdu.vesion2.databinding.ItemBannerBinding;

public class BannerAdapter extends PagerAdapter {
    private ArrayList<Banner> banners;
    private ItemClickListener itemClickListener;

    public BannerAdapter(ArrayList<Banner> banners) {
        this.banners = banners;
    }

    @Override
    public int getCount() {
        return banners.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ItemBannerBinding binding = DataBindingUtil.inflate(LayoutInflater.from(container.getContext()), R.layout.item_banner, container, false);
        binding.getRoot().setTag(position);

        Banner banner = banners.get(position);

        Picasso.Builder builder = new Picasso.Builder(binding.getRoot().getContext());
                builder.listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Log.d("BBB", "onImageLoadFailed: "+exception.getMessage());
                    }
                })
                .build();
        Picasso picasso = builder.build();

        try {
            Picasso.setSingletonInstance(picasso);
        } catch (IllegalStateException ignored) {
            // Picasso instance was already set
            // cannot set it after Picasso.with(Context) was already in use
        }
        picasso.with(binding.getRoot().getContext()).load(banner.getUrlImv().trim())

                .into(binding.imvBanner);

        container.addView(binding.getRoot());

        return binding.getRoot();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
    public interface ItemClickListener {
        public void onItemClick(int position);
    }

}
