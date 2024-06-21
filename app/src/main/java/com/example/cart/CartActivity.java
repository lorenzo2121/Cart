package com.example.cart;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.Executors;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private AppDatabase appDatabase;
    public TextView totale;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        totale = findViewById(R.id.textViewTotal);
        recyclerView = findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CartAdapter(this,totale);
        recyclerView.setAdapter(adapter);

        // Inizializza il database Room
        appDatabase = AppDatabase.getInstance(this);

        // Carica i dati dal database e aggiorna la RecyclerView
        loadCartProducts();
    }

    private void loadCartProducts() {
        // Esegui la query per ottenere tutti i prodotti nel carrello
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<Product> products = appDatabase.productDAO().getAllProducts();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setProducts(products);
                    }
                });
            }
        });
    }
}
