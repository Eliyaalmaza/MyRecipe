// FavoriteRecipesFragment.java
package com.example.myrecipe;

public class FavoriteRecipesFragment extends BaseRecipesFragment {
    @Override
    protected boolean shouldInclude(Recipe recipe) {
        // Only recipes marked favorite
        return recipe.isFavorite();
    }
}
