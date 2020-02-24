package gopdu.pdu.vesion2.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

import gopdu.pdu.vesion2.R;
import gopdu.pdu.vesion2.databinding.ItemServiceBinding;
import gopdu.pdu.vesion2.object.Service;

public class ItemServiceAdapter  extends PagerAdapter {

    private ArrayList<Service> services;
    private ItemClickListener itemClickListener;

    public ItemServiceAdapter(ArrayList<Service> services) {
        this.services = services;
    }

    @Override
    public int getCount() {
        return services.size();
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ItemServiceBinding binding = DataBindingUtil.inflate(LayoutInflater.from(container.getContext()), R.layout.item_service, container, false);
        binding.getRoot().setTag(position);

        return binding.getRoot();
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }
    public interface ItemClickListener {
        public void onItemClick(int position);
    }
    @Override
    public float getPageWidth(int position) {
        return(0.8f);
    }
}
