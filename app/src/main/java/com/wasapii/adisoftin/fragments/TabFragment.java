package com.wasapii.adisoftin.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.wasapii.adisoftin.R;
import com.wasapii.adisoftin.customview.BadgeView;
import com.wasapii.adisoftin.customview.NonSwipeableViewPager;

/**
 * Created by Ratan on 7/27/2015.
 */
public class TabFragment extends Fragment {

    public static TabLayout tabLayout;
    public static NonSwipeableViewPager viewPager;
    public static int int_items = 5 ;
    Context ctx;
    private static BadgeView badge;
    View notificationView;
    TabHost tabHost;


   // private int icons[] = {R.drawable.notification_seletor,R.drawable.chat_seletor,R.drawable.home_seletor,R.drawable.location_selector,R.drawable.profile_selector};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
            final View x =  inflater.inflate(R.layout.tab_layout,null);

            tabLayout = (TabLayout) x.findViewById(R.id.tabs);
            viewPager = (NonSwipeableViewPager) x.findViewById(R.id.viewpager);

        /**
         *Set an Apater for the View Pager
         */
        ctx = getActivity();
        badge = new BadgeView(ctx);
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        LayoutInflater inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView tvNewNotifications = (TextView) x.findViewById(R.id.new_notifications);
        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

        tabLayout.post(new Runnable() {

            @Override
            public void run() {

                tabLayout.setupWithViewPager(viewPager);

                    for (int i = 0; i < tabLayout.getTabCount(); i++) {

                        TabLayout.Tab tab = tabLayout.getTabAt(i);

                        if (tab != null) {

                            switch (i){
                                case 0:
                                    tab.setCustomView(R.layout.notification_tab_item);
                                    break;
                                case 1:
                                    tab.setCustomView(R.layout.chat_tab_item);
                                    break;
                                case  2 :
                                    tab.setCustomView(R.layout.home_tab_item);
                                    break;
                                case  3 :
                                    tab.setCustomView(R.layout.location_tab_item);
                                    break;
                                case  4 :
                                    tab.setCustomView(R.layout.profile_tab_item);
                                    break;
                            }
                        }
//                        tabLayout.getTabAt(i).setIcon(icons[i]);
                    }

                viewPager.setCurrentItem(2,false);  //set home fragment as default

                tabLayout.getTabAt(2).select(); // set icon selected of home tab
              }
        });


        return x;

    }




    public static void setCurrentPage(){
        viewPager.setCurrentItem(4,false);  //set home fragment as default
        tabLayout.getTabAt(4).select(); // set icon selected of home tab
    }



    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position)
        {
          switch (position){
              case 0 : return new NotificationFragment();
              case 1 : return new ChatFragment();
              case 2 : return new PeopleFragment();
              case 3 : return new MapFragment();
              case 4 :return  new ProfileFragment();

          }
        return null;
        }

        @Override
        public int getCount() {
            return int_items;
        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "";//"Primary";
                case 1 :
                    return "";//"Social";
                case 2 :
                    return "";//"Updates";
                case 3 :
                    return "";
                case 4 :
                    return "";
            }
                return null;
        }
    }

}
/*
LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View chatTab = inflater.inflate(R.layout.chat_tab, null);

        tvNewNotifications = (TextView) chatTab.findViewById(R.id.new_notifications);

        intent = new Intent().setClass(MainTab.this, Chat.class);
        tabSpec = tabHost
                .newTabSpec("chat")
                .setIndicator(chatTab)
                .setContent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
 */