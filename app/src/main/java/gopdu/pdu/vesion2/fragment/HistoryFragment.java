package gopdu.pdu.vesion2.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;

import gopdu.pdu.vesion2.Common;
import gopdu.pdu.vesion2.GoPDUApplication;
import gopdu.pdu.vesion2.R;
import gopdu.pdu.vesion2.activity.HistoryDetailActivityActivity;
import gopdu.pdu.vesion2.adapter.ItemHistoryAdapter;
import gopdu.pdu.vesion2.databinding.FragmentHistoryBinding;
import gopdu.pdu.vesion2.network.HistoryResponse;
import gopdu.pdu.vesion2.object.History;
import gopdu.pdu.vesion2.presenter.PresenterHistoryFragment;
import gopdu.pdu.vesion2.view.ViewHistoryFragmentListener;
import gopdu.pdu.vesion2.viewmodel.GetHistoryViewModel;


public class HistoryFragment extends Fragment implements ViewHistoryFragmentListener {

    private FragmentHistoryBinding binding;
    private String userid;
    private ArrayList<History> histories;
    private ItemHistoryAdapter historyAdapter;
    private boolean isLoading = false;
    private boolean limitData = false;

    private PresenterHistoryFragment presenter;
    private ProgressDialog progressDialog;

    private String status;

    private int page =1;

    private GetHistoryViewModel getHistoryModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false);

        init();
        GetData(page);
        setUpOnClick();
        initScrollListener();
        return binding.getRoot();
    }

    private void setUpOnClick() {


        historyAdapter.setOnItemClickedListener(new ItemHistoryAdapter.OnItemClickedListener() {
            @Override
            public void onItemClick(int postion, View v) {
                Intent intent = new Intent(getActivity(), HistoryDetailActivityActivity.class);
                intent.putExtra(getString(R.string.paramID), historyAdapter.getItem(postion).getId());
                startActivity(intent);
            }
        });
    }

    private void GetData(int page) {
        progressDialog.show();
        HashMap param = new HashMap<>();
        param.put(getString(R.string.paramID), userid);
        param.put(getString(R.string.paramPage), page);
        param.put(getString(R.string.paramStatus), status);

        getHistoryModel.getHistoryResponseLiveData(param).observe(this, new Observer<HistoryResponse>() {
            @Override
            public void onChanged(HistoryResponse historyResponse) {
                presenter.reciverGetHistory(historyResponse);
            }
        });;
    }


    private void init() {

        userid = FirebaseAuth.getInstance().getUid();
        status = GoPDUApplication.getInstance().getString(R.string.param_StatusSuccess);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(GoPDUApplication.getInstance().getString(R.string.waiting));
        progressDialog.setCancelable(false);

        presenter = new PresenterHistoryFragment(this);
        getHistoryModel = ViewModelProviders.of(this).get(GetHistoryViewModel.class);
        histories = new ArrayList<>();
        historyAdapter = new ItemHistoryAdapter(histories);
        binding.rclHistory.setAdapter(historyAdapter);


    }

    private void initScrollListener() {
        binding.rclHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading && !limitData  ) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == histories.size() - 1) {
                        //bottom of list!
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    Runnable my_runnable = new Runnable() {
        @Override
        public void run() {
            histories.remove(histories.size() - 1);
            int scrollPosition = histories.size();
            historyAdapter.notifyItemRemoved(scrollPosition);

            GetData(++page);

            historyAdapter.notifyDataSetChanged();
            isLoading = false;
        }
    };

    public Handler handler = new Handler(); // use 'new Handler(Looper.getMainLooper());' if you want this handler to control something in the UI
    // to start the handler
    public void startHandler() {
        handler.postDelayed(my_runnable, 2000);
    }

    // to stop the handler
    public void stopHandler() {
        handler.removeCallbacks(my_runnable);
    }

    // to reset the handler
    public void restart() {
        handler.removeCallbacks(my_runnable);
        handler.postDelayed(my_runnable, 2000);
    }

    private void loadMore() {
        histories.add(null);
        historyAdapter.notifyItemInserted(histories.size() - 1);

        startHandler();
    }



    @Override
    public void getHistorySuccess(ArrayList<History> data) {
        histories.addAll(data);
        historyAdapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }

    @Override
    public void getHistoryFaild() {
        progressDialog.dismiss();
        Common.ShowToastShort(getString(R.string.checkConnect));
    }

    @Override
    public void getHistoryStatusSuccess(String status) {
        this.status = status;
    }

    @Override
    public void getHistoryStatusCancel(String status) {
        this.status = status;
    }

    @Override
    public void reciverAllDataHistory() {
        limitData = true;
        Common.ShowToastShort(GoPDUApplication.getInstance().getString(R.string.reciverAllData));
        progressDialog.dismiss();
    }
}
