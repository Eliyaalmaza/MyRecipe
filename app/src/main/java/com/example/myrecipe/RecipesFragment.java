package com.example.myrecipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class RecipesFragment extends Fragment {
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);
        ViewPager2 pager = view.findViewById(R.id.pager);
        TabLayout tabs = view.findViewById(R.id.tab_layout);
        pager.setAdapter(new RecipesPagerAdapter(this));
        new TabLayoutMediator(tabs, pager,
                (tab, pos) -> {
                    if (pos == 0) tab.setText("My Recipes");
                    else if (pos == 1) tab.setText("All Recipes");
                    else tab.setText("Favorites");
                }
        ).attach();
        return view;
    }
}
