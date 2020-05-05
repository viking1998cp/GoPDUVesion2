package gopdu.pdu.vesion2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


import gopdu.pdu.vesion2.Common;
import gopdu.pdu.vesion2.GoPDUApplication;
import gopdu.pdu.vesion2.R;
import gopdu.pdu.vesion2.databinding.ItemHistoryBinding;
import gopdu.pdu.vesion2.databinding.ItemLoadingBinding;
import gopdu.pdu.vesion2.object.History;

public class ItemHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private OnItemClickedListener onItemClickedListener;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private ArrayList<History> arrayList;

    public ItemHistoryAdapter(ArrayList<History> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            ItemHistoryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_history, parent, false);
            return new ItemHistoryAdapter.ItemRowHolder(binding);
        } else {
            ItemLoadingBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_loading, parent, false);
            return new ItemHistoryAdapter.LoadingViewHolder(binding);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ItemRowHolder) {
            populateItemRows((ItemRowHolder) viewHolder, position);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList == null ? 0 : arrayList.size();
    }



    @Override
    public int getItemViewType(int position) {
        return arrayList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public History getItem(int position){
        return arrayList.get(position);
    }

    private class ItemRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemHistoryBinding binding;
        private ItemRowHolder(ItemHistoryBinding view) {
            super(view.getRoot());
            binding = view;
            view.getRoot().setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            onItemClickedListener.onItemClick(getAdapterPosition(), v);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ItemLoadingBinding binding;

        public LoadingViewHolder(@NonNull ItemLoadingBinding view) {
            super(view.getRoot());
            binding = view;
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

    }

    private void populateItemRows(ItemRowHolder viewHolder, int position) {
        History history = arrayList.get(position);
        viewHolder.binding.tvDateTime.setText(history.getTime());
        viewHolder.binding.tvPickUpName.setText(history.getPickupName());
        LatLng ơickupLng = new LatLng(history.getPickupLat(), history.getPickupLogt());
        LatLng DestinationLng = new LatLng(history.getDestinationLat(), history.getDestinationLogt());
        viewHolder.binding.tvDistance.setText(GoPDUApplication.getInstance().getString(R.string.distance, Common.getDistance(ơickupLng,DestinationLng)/1000));
        viewHolder.binding.tvDestinationName.setText(history.getDestinationName());
        if(history.getStatus().equals(GoPDUApplication.getInstance().getString(R.string.param_StatusSuccess))){
            viewHolder.binding.tvState.setBackground(GoPDUApplication.getInstance().getResources().getDrawable(R.drawable.background_button_no_boder_blue));
            viewHolder.binding.tvState.setText(GoPDUApplication.getInstance().getString(R.string.Successs));
        }else {
            viewHolder.binding.tvState.setBackground(GoPDUApplication.getInstance().getResources().getDrawable(R.drawable.background_button_no_boder_gray));
            viewHolder.binding.tvState.setText(GoPDUApplication.getInstance().getString(R.string.Cancel));
        }

    }

    public interface OnItemClickedListener {
        void onItemClick(int postion, View v);
    }


    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }
}
