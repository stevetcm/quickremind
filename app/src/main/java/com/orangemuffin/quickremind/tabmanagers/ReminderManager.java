package com.orangemuffin.quickremind.tabmanagers;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.orangemuffin.quickremind.R;
import com.orangemuffin.quickremind.fragments.FragmentUpcoming;
import com.orangemuffin.quickremind.fragments.FragmentEnded;

import java.util.ArrayList;
import java.util.List;

/* Created by OrangeMuffin on 6/20/2017 */
public class ReminderManager extends Fragment {
    private static TabLayout tabLayout;
    private static ViewPager viewPager;
    private static ViewPagerAdapter viewPagerAdapter;

    private boolean switchPage = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_manager_layout, null);

        Bundle data = getArguments();
        switchPage = data.getBoolean("switchPage");

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(viewPagerAdapter.getTabView(i));
        }

        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new FragmentUpcoming(), "Upcoming");
        viewPagerAdapter.addFragment(new FragmentEnded(), "Ended");
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(2);

        if (switchPage) { viewPager.setCurrentItem(1, false); }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private int[] imageResId = { R.drawable.ic_notif_on_white, R.drawable.ic_notif_off_white};

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public View getTabView(int position) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.tab_element_layout, null);
            TextView tv = (TextView) v.findViewById(R.id.tabText);
            tv.setText(mFragmentTitleList.get(position));
            ImageView img = (ImageView) v.findViewById(R.id.tabIcon);
            img.setImageResource(imageResId[position]);
            return v;
        }
    }
}
