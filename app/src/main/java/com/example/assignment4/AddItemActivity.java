package com.example.assignment4;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddItemActivity extends AppCompatActivity {

    private EditText itemNameEditText, itemQuantityEditText, itemPriceEditText;
    private Button addItemButton;
    private DatabaseReference itemsReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Get the user ID from the Intent
        userId = getIntent().getStringExtra("USER_ID");

        // Initialize Firebase Database reference
        itemsReference = FirebaseDatabase.getInstance().getReference("shopping_list").child(userId);

        // Initialize views
        itemNameEditText = findViewById(R.id.name);
        itemQuantityEditText = findViewById(R.id.quantity);
        itemPriceEditText = findViewById(R.id.price);
        addItemButton = findViewById(R.id.addButton);

        // Set up the "Add Item" button click listener
        addItemButton.setOnClickListener(v -> addItem());
    }

    private void addItem() {
        String itemName = itemNameEditText.getText().toString().trim();
        String itemQuantityStr = itemQuantityEditText.getText().toString().trim();
        String itemPriceStr = itemPriceEditText.getText().toString().trim();

        if (TextUtils.isEmpty(itemName) || TextUtils.isEmpty(itemQuantityStr) || TextUtils.isEmpty(itemPriceStr)) {
            Toast.makeText(AddItemActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int itemQuantity = Integer.parseInt(itemQuantityStr);
            double itemPrice = Double.parseDouble(itemPriceStr);

            // Create a new item object (Item class should be defined separately)
            Item newItem = new Item(userId, itemName, itemQuantity, itemPrice);

            // Push the item to Firebase
            String itemId = itemsReference.push().getKey();  // Get a unique key for the item
            if (itemId != null) {
                itemsReference.child(itemId).setValue(newItem)  // Save item to Firebase Realtime Database
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddItemActivity.this, "Item added successfully!", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);  // Notify MainActivity that the data was added
                                finish();  // Close the activity
                            } else {
                                Toast.makeText(AddItemActivity.this, "Failed to add item: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } catch (NumberFormatException e) {
            Toast.makeText(AddItemActivity.this, "Invalid quantity or price", Toast.LENGTH_SHORT).show();
        }
    }
}
