package com.example.assignment4;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddItemActivity extends AppCompatActivity {

    private EditText nameEditText, quantityEditText, priceEditText;
    private Button addButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        databaseReference = FirebaseDatabase.getInstance().getReference("shopping_list");

        nameEditText = findViewById(R.id.name);
        quantityEditText = findViewById(R.id.quantity);
        priceEditText = findViewById(R.id.price);
        addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(v -> addItem());
    }

    private void addItem() {
        String name = nameEditText.getText().toString().trim();
        int quantity = Integer.parseInt(quantityEditText.getText().toString().trim());
        double price = Double.parseDouble(priceEditText.getText().toString().trim());

        if (TextUtils.isEmpty(name) || quantity <= 0 || price <= 0) {
            Toast.makeText(AddItemActivity.this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = databaseReference.push().getKey();
        Item item = new Item(name, quantity, price);

        if (id != null) {
            databaseReference.child(id).setValue(item).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(AddItemActivity.this, "Item added", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddItemActivity.this, "Failed to add item: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
