package gopdu.pdu.vesion2.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.compat.AutocompleteFilter;
import com.google.android.libraries.places.compat.AutocompletePrediction;
import com.google.android.libraries.places.compat.AutocompletePredictionBuffer;
import com.google.android.libraries.places.compat.AutocompletePredictionBufferResponse;
import com.google.android.libraries.places.compat.GeoDataClient;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.PlaceBuffer;
import com.google.android.libraries.places.compat.PlaceBufferResponse;
import com.google.android.libraries.places.compat.Places;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import gopdu.pdu.vesion2.Common;
import gopdu.pdu.vesion2.GoPDUApplication;
import gopdu.pdu.vesion2.R;
import gopdu.pdu.vesion2.databinding.ItemLocationBinding;
import gopdu.pdu.vesion2.object.Location;

public class ItemLocationAdapter extends RecyclerView.Adapter<ItemLocationAdapter.ItemRowHolder> implements Filterable {

    private static final String TAG = "ItemLocationAdapter";
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);
    ArrayList<Location> mResultList;

    private GoogleApiClient mGoogleApiClient;

    private LatLngBounds mBounds;

    private LatLng pickupLng;

    private int layout;

    private AutocompleteFilter mPlaceFilter;

    private OnItemClickedListener onItemClickedListener;


    public ItemLocationAdapter(GoogleApiClient googleApiClient,
                               LatLngBounds bounds, AutocompleteFilter filter, LatLng pickupLng) {

        this.mGoogleApiClient = googleApiClient;
        this.mBounds = bounds;
        this.mPlaceFilter = filter;
        this.pickupLng = pickupLng;

    }

    /*
    Clear List items
     */
    public void clearList() {
        if (mResultList != null && mResultList.size() > 0) {
            mResultList.clear();
        }
    }


    /**
     * Sets the bounds for all subsequent queries.
     */
    public void setBounds(LatLngBounds bounds) {
        mBounds = bounds;
    }

    public LatLng getPickupLng() {
        return pickupLng;
    }

    public void setPickupLng(LatLng pickupLng) {
        this.pickupLng = pickupLng;
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    clearList();
                    mResultList = getAutocomplete(constraint);
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList;
                        results.count = mResultList.size();
                        Log.d("BBB", "performFilteringT: " + results.count);
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    //notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    private ArrayList resultList;
    private ArrayList<Location> getAutocomplete(CharSequence constraint) {
        if (mGoogleApiClient.isConnected()) {
            final GeoDataClient mGeoDataClient;
            mGeoDataClient = Places.getGeoDataClient(GoPDUApplication.getInstance(), null);
            // Submit the query to the autocomplete API and retrieve a PendingResult that will
            // contain the results when the query completes.

            final Task<AutocompletePredictionBufferResponse> results =
                    mGeoDataClient.getAutocompletePredictions(constraint.toString(),
                            mBounds, mPlaceFilter);


            // This method should have been called off the main UI thread. Block and wait for at most 60s
            // for a result from the API.

            // Confirm that the query completed successfully, otherwise return null
            resultList = new ArrayList<>();
            results.addOnSuccessListener(new OnSuccessListener<AutocompletePredictionBufferResponse>() {
                @Override
                public void onSuccess(final AutocompletePredictionBufferResponse autocompletePredictions) {
                    Iterator<AutocompletePrediction> iterator = results.getResult().iterator();
                    while (iterator.hasNext()) {
                        AutocompletePrediction prediction = iterator.next();

                        LatLng destination = Common.getLocationFromAddress(prediction.getSecondaryText(STYLE_BOLD).toString());
                        float distance = Common.getDistance(pickupLng, destination)/1000;
                        if(distance<=90.0){
                            final Location location = new Location();
                            location.setNameLocation(prediction.getPrimaryText(STYLE_BOLD).toString());
                            location.setNameDetailLocation(prediction.getSecondaryText(STYLE_BOLD).toString());
                            location.setId(prediction.getPlaceId());

                            //get latitude , longtitude  from places address
                            location.setLat(destination.latitude);
                            location.setLogt(destination.longitude);
                            resultList.add(location);
                        }else {
                            continue;
                        }
                        // Get the details of this prediction and copy it into a new PlaceAutocomplete object.
                    }
                    notifyDataSetChanged();
                    Log.d("BBB", "getAutocomplete: " + resultList.size());

                }
            });


            return resultList;
        }
        Log.e("BBB", "Google API client is not connected for autocomplete query.");
        return null;
    }

    @NonNull
    @Override
    public ItemLocationAdapter.ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLocationBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_location, parent, false);
        return new ItemLocationAdapter.ItemRowHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRowHolder holder, int position) {

        Location location = getItem(position);
        holder.binding.tvName.setText(location.getNameLocation());
        holder.binding.tvNameDetail.setText(location.getNameDetailLocation());
        float distance = Common.getDistance(pickupLng, new LatLng(location.getLat(), location.getLogt()))/1000;
        holder.binding.tvDistance.setText(GoPDUApplication.getInstance().getString(R.string.distance, distance));
    }


    @Override
    public int getItemCount() {
        if (mResultList != null)
            return mResultList.size();

        else
            return 0;
    }

    public Location getItem(int position) {
        return mResultList.get(position);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemLocationBinding binding;

        private ItemRowHolder(ItemLocationBinding view) {
            super(view.getRoot());
            binding = view;
            view.getRoot().setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onItemClickedListener.onItemClick(getAdapterPosition(), v);
        }


    }

    /**
     * Holder for Places Geo Data Autocomplete API results.
     */

    public interface OnItemClickedListener {
        void onItemClick(int postion, View v);
    }


    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }
}
