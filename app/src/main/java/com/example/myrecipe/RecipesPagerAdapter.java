package com.example.myrecipe;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class RecipesPagerAdapter extends FragmentStateAdapter {
    public RecipesPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new MyRecipesFragment();
            case 2: return new FavoriteRecipesFragment();
            default: return new AllRecipesFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
