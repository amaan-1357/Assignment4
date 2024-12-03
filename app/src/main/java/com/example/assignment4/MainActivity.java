package com.example.assignment4;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Item, ItemViewHolder> adapter;
    private DatabaseReference itemsReference;
    private FloatingActionButton addItemButton;
    private String userId;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the user ID from the Intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Initialize Firebase Database reference
        itemsReference = FirebaseDatabase.getInstance().getReference("shopping_list").child(userId);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize FAB
        addItemButton = findViewById(R.id.addItemButton);
        addItemButton.setOnClickListener(v -> {
            // Start AddItemActivity for result
            Intent addItemIntent = new Intent(MainActivity.this, AddItemActivity.class);
            addItemIntent.putExtra("USER_ID", userId);
            startActivityForResult.launch(addItemIntent);
        });

        // Initialize Logout Button
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        });

        // Set up FirebaseRecyclerAdapter
        FirebaseRecyclerOptions<Item> options = new FirebaseRecyclerOptions.Builder<Item>()
                .setQuery(itemsReference, Item.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Item, ItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Item model) {
                holder.bind(model, getRef(position).getKey());
            }

            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_view, parent, false);
                return new ItemViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Ensure that the adapter starts listening for changes in the database
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Ensure that the adapter stops listening for changes in the database
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    private final ActivityResultCallback<ActivityResult> resultCallback = result -> {
        if (result.getResultCode() == RESULT_OK) {
            // Notify the adapter of data changes (re-fetch data)
            adapter.notifyDataSetChanged();
            Toast.makeText(MainActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
        }
    };

    private final ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), resultCallback);

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQuantity, itemPrice;
        Button deleteButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(Item item, String itemId) {
            itemName.setText(item.getName());
            itemQuantity.setText(String.valueOf(item.getQuantity()));
            itemPrice.setText(String.valueOf(item.getPrice()));

            deleteButton.setOnClickListener(v -> {
                // Delete the item from Firebase
                DatabaseReference itemRef = FirebaseDatabase.getInstance()
                        .getReference("shopping_list")
                        .child(itemId);
                itemRef.removeValue();
            });
        }
    }
}
