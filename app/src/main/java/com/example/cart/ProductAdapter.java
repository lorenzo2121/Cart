package com.example.cart;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.Executors;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private ProductDAO productDAO;

    public ProductAdapter(Context context, List<Product> productList) {
        this.productList = productList;
        AppDatabase db = AppDatabase.getInstance(context);
        this.productDAO = db.productDAO();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText("Price: $" + product.getPrice());
        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Executors.newSingleThreadExecutor().execute(() -> {

                    Product existingProduct = productDAO.getProductById(product.getId());

                    if (existingProduct != null) {
                        // Prodotto già presente: aggiorna la quantità
                        existingProduct.setQuantity(existingProduct.getQuantity() + 1);
                        productDAO.update(existingProduct);  // Aggiorna nel database
                        Log.d("DATABASE", "Quantità del prodotto aggiornata nel DB: " + product.getName());
                    } else {
                        // Prodotto non presente: inserisci nel carrello
                        product.setQuantity(1);  // Imposta la quantità a 1 (prima aggiunta)
                        productDAO.insert(product);  // Inserisci nel database
                        Log.d("DATABASE", "Prodotto aggiunto al DB: " + product.getName());
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        public TextView productName, productPrice, productQuantity;
        public Button addToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            addToCart = itemView.findViewById(R.id.addToCart);
        }
    }
}
