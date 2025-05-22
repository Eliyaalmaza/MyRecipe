package com.example.myrecipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class IngredientsFragment extends Fragment {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ingredients, container, false);
        viewPager = root.findViewById(R.id.ingredients_view_pager);
        tabLayout = root.findViewById(R.id.ingredients_tab_layout);

        // Set adapter to ViewPager2
        viewPager.setAdapter(new IngredientsPagerAdapter(this));

        // Attach Tabs with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("My Fridge");
                            break;
                        case 1:
                            tab.setText("Manage Fridge");
                            break;
                        case 2:
                            tab.setText("Favorite Items");
                            break;
                    }
                }).attach();

        return root;
    }

    private static class IngredientsPagerAdapter extends FragmentStateAdapter {
        public IngredientsPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new MyFridgeFragment();
                case 1:
                    return new ManageFridgeFragment();
                case 2:
                    return new FavoriteItemsFragment();
                default:
                    return new Fragment(); // fallback empty fragment
            }
        }

        @Override
        public int getItemCount() {
            return 3; // three tabs
        }
    }
}
   