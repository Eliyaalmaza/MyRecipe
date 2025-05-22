// MyRecipesFragment.java
package com.example.myrecipe;

public class MyRecipesFragment extends BaseRecipesFragment {
    @Override
    protected boolean shouldInclude(Recipe recipe) {
        // Only recipes whose all ingredients are found
        return recipe.isCanBeDone();
    }
}
