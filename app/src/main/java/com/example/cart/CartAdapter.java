package com.example.cart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Product> products = new ArrayList<>();
    private Context context;
    private TextView textViewTotal; // Riferimento alla TextView del totale

    public CartAdapter(Context context, TextView textViewTotal) {
        this.context = context;
        this.textViewTotal = textViewTotal;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_cart, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = products.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText("Price: $" + product.getPrice());
        holder.productQuantity.setText("Quantity: " + product.getQuantity());
        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product.setQuantity(product.getQuantity() + 1);
                notifyDataSetChanged(); // Aggiorna la RecyclerView quando la quantità cambia
                updateProductInDatabase(product);
                updateTotal(); // Aggiorna il totale dopo la modifica della quantità
            }
        });
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (product.getQuantity() > 0) {
                    product.setQuantity(product.getQuantity() - 1);
                    notifyItemChanged(position); // Notifica solo l'elemento modificato
                    updateProductInDatabase(product);
                    updateTotal(); // Aggiorna il totale dopo la modifica della quantità
                }

                if (product.getQuantity() == 0) {
                    removeProductFromDatabase(product);
                    products.remove(product);
                    notifyDataSetChanged(); // Aggiorna la RecyclerView dopo la rimozione
                    updateTotal(); // Aggiorna il totale dopo la rimozione del prodotto
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
        updateTotal(); // Aggiorna il totale quando si impostano i prodotti
    }

    public void updateTotal() {
        double total = calculateTotal();
        textViewTotal.setText("Total: €" + String.format("%.2f", total));
    }

    private double calculateTotal() {
        double total = 0;
        for (Product product : products) {
            total += product.getPrice() * product.getQuantity();
        }
        return total;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        public TextView productName, productPrice, productQuantity;
        public Button addButton, removeButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            addButton = itemView.findViewById(R.id.buttonPlus);
            removeButton = itemView.findViewById(R.id.buttonMinus);
        }
    }

    // Metodi per l'aggiornamento e la rimozione dal database
    private void updateProductInDatabase(Product product) {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(context).productDAO().update(product);
            Log.d("DATABASE", "Prodotto aggiornato nel DB: " + product.getName());
        });
    }

    private void removeProductFromDatabase(Product product) {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(context).productDAO().delete(product);
            Log.d("DATABASE", "Prodotto rimosso dal DB: " + product.getName());
        });
    }
}

