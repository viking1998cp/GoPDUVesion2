package gopdu.pdu.vesion2.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MainViewpager extends FragmentPagerAdapter {
    private ArrayList<Fragment> arrayListFragments = new ArrayList<>();
    private ArrayList<String> arrayListTitle = new ArrayList<>();

    public MainViewpager(FragmentManager fm) {
        super(fm);
    }
    public void addfragment(Fragment fragment, String title){
        arrayListFragments.add(fragment);
        arrayListTitle.add(title);
    }
    @Override
    public Fragment getItem(int i) {
        return arrayListFragments.get(i);
    }

    @Override
    public int getCount() {
        return arrayListFragments.size();
    }
    public void resetFragment(){
        for(int i=0;i<arrayListFragments.size();i++){
            arrayListFragments.remove(i);
        }

        for(int i=0;i<arrayListTitle.size();i++){
            arrayListTitle.remove(i);
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return arrayListTitle.get(position);
    }

}
