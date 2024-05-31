package com.doublehammerstudio.agriunoapp.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.doublehammerstudio.agriunoapp.Fragments.DashboardFragment;
import com.doublehammerstudio.agriunoapp.Fragments.DataReadingFragment;

public class MainFragmentAdapter extends FragmentStateAdapter {
    public MainFragmentAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DashboardFragment();
            case 1:
                return new DataReadingFragment();
            default:
                return new DashboardFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Number of fragments in your bottom navigation
    }
}