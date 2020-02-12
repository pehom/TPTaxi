package com.android.pehominc.tptaxi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PassengerActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference passengersDatabaseReference;
    private Button signOutButton;
    private TextView passengerTitleTextView;
    private String passengerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);
        passengerTitleTextView = findViewById(R.id.passengerTitleTextView);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        passengersDatabaseReference = database.getReference().child("passengers");

        if (auth.getCurrentUser().getUid() != null) {
          database.getReference().child("passengers").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Passenger passenger = dataSnapshot.getValue(Passenger.class);
                    passengerName = passenger.getName();
                    passengerTitleTextView.setText("Passenger " + passengerName);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }

        signOutButton = findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(PassengerActivity.this, ChooseModeActivity.class));
            }
        });
    }
}
