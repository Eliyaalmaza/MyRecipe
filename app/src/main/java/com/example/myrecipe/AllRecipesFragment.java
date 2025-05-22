// AllRecipesFragment.java
package com.example.myrecipe;

public class AllRecipesFragment extends BaseRecipesFragment {
    @Override
    protected boolean shouldInclude(Recipe recipe) {
        // Show every recipe
        return true;
    }
}
