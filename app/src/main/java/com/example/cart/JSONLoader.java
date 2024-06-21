package com.example.cart;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JSONLoader {

    public List<Product> loadJSONFromAsset(Context context, String fileName) {
        List<Product> productList = new ArrayList<>();
        String json = null;

        try {
            // Leggi il file JSON
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

            // Analizza il JSON
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                int id = obj.getInt("id");
                String name = obj.getString("name");
                double price = obj.getDouble("price");

                Product product = new Product(name, price);
                product.setId(id);
                productList.add(product);
            }

        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
        }

        return productList;
    }
}
