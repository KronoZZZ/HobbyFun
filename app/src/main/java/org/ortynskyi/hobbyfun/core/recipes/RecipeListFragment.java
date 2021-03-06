package org.ortynskyi.hobbyfun.core.recipes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.ortynskyi.hobbyfun.R;
import org.ortynskyi.hobbyfun.base.BaseFragment;
import org.ortynskyi.hobbyfun.base.OnItemClickListener;
import org.ortynskyi.hobbyfun.core.recipes.adapter.RecipeAdapter;
import org.ortynskyi.hobbyfun.core.recipes.domain.dto.Recipe;
import org.ortynskyi.hobbyfun.core.recipes.presentation.RecipePresenter;
import org.ortynskyi.hobbyfun.core.recipes.presentation.RecipePresenterImpl;
import org.ortynskyi.hobbyfun.core.recipes.presentation.RecipeView;
import org.ortynskyi.hobbyfun.network.RestApi;
import org.ortynskyi.hobbyfun.views.EndlessOnScrollListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class RecipeListFragment extends BaseFragment implements RecipeView, OnItemClickListener {

    public static final String EXTRA_RECIPE_ID = "EXTRA_RECIPE_ID";

    private static final String PIZZA = "pizza";

    @BindView(R.id.recipesRecycle) RecyclerView recipesRecycle;

    private RecipePresenter presenter;

    private EndlessOnScrollListener scrollListener;
    private RecipeAdapter adapter;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new RecipePresenterImpl();
        presenter.attachView(this);
    }

    @NonNull
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        ButterKnife.bind(this, view);
        initAdapter();
        presenter.fetchRecipes(PIZZA, RestApi.DEFAULT_PAGE);
        return view;
    }

    @Override
    public void loadRecipes(@NonNull final List<Recipe> recipes) {
        adapter.setRecipes(recipes);
    }

    @Override
    public void onItemClick(final int position) {
        final Recipe recipe = adapter.getRecipe(position);
        final Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
        intent.putExtra(EXTRA_RECIPE_ID, recipe.getRecipeId());
        startActivity(intent);
    }

    private void initAdapter() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new RecipeAdapter(this);
        recipesRecycle.setLayoutManager(layoutManager);
        recipesRecycle.setAdapter(adapter);
        recipesRecycle.addOnScrollListener(scrollListener = new EndlessOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(final int currentPage) {
                presenter.fetchRecipes(PIZZA, currentPage);
            }
        });
    }
}
