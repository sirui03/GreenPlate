package com.example.androidgreenplate.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import com.example.androidgreenplate.R;
import com.example.androidgreenplate.views.ShoppingListActivity;

import androidx.annotation.NonNull;
import java.util.List;



public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.IngredientViewHolder> {

    private  List<Ingredient> ingredientList;

    private OnItemClickListener onItemClickListener;

    public ShoppingListAdapter(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;

    }

    public interface OnItemClickListener {
        void onItemClick(Ingredient ingredient);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        final Ingredient ingredient = ingredientList.get(position);
        holder.checkBox.setChecked(false);
        holder.nameTextView.setText(ingredient.getName());
        holder.quantityTextView.setText(String.valueOf(ingredient.getQuantity()));


        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(ingredient);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private TextView nameTextView;
        private TextView quantityTextView;

        public CheckBox getCheckBox() {
            return checkBox;
        }
        public TextView getNameTextView() {
            return nameTextView;
        }

        public TextView getQuantityTextView() {
            return quantityTextView;
        }

        public IngredientViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            nameTextView = itemView.findViewById(R.id.textview_ingredient_name);
            quantityTextView = itemView.findViewById(R.id.textview_ingredient_quantity);
        }
    }
}
