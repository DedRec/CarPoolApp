package com.example.drivercarpool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddTripActivity extends AppCompatActivity {
    int lastId = 0;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference reference;
    Button addBtn;
    EditText sourceEditText, destinationEditText, carPlateEditText, passengersText;
    Spinner timeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        reference = FirebaseDatabase.getInstance().getReference("trips");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        timeSpinner = findViewById(R.id.time_spinner);
        sourceEditText = findViewById(R.id.source_input);
        destinationEditText = findViewById(R.id.destination_input);
        carPlateEditText = findViewById(R.id.car_plate_input);
        passengersText = findViewById(R.id.passenger_input);
        addBtn = findViewById(R.id.add_btn);

        reference.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot trip : snapshot.getChildren()){
                    String lastTripId = trip.getKey();
                    lastId = Integer.parseInt(lastTripId.replaceAll("\\D+", ""));
                }
                lastId = lastId > 0 ? lastId : 0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = String.valueOf(timeSpinner.getSelectedItem());
                String source = String.valueOf(sourceEditText.getText());
                String destination = String.valueOf(destinationEditText.getText());
                String carPlate = String.valueOf(carPlateEditText.getText());
                String passengers = String.valueOf(passengersText.getText());

                if(TextUtils.isEmpty(source)){
                    Toast.makeText(getApplicationContext(),"Enter source",Toast.LENGTH_SHORT).show();
                    sourceEditText.requestFocus();
                    return;
                } else if(TextUtils.isEmpty(destination)){
                    Toast.makeText(getApplicationContext(),"Enter destination",Toast.LENGTH_SHORT).show();
                    destinationEditText.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(carPlate)) {
                    Toast.makeText(getApplicationContext(),"Enter Car Plate Number",Toast.LENGTH_SHORT).show();
                    carPlateEditText.requestFocus();
                    return;
                }else if (TextUtils.isEmpty(passengers)) {
                    Toast.makeText(getApplicationContext(),"Enter Passengers Number",Toast.LENGTH_SHORT).show();
                    passengersText.requestFocus();
                    return;
                }else if (!(source.toLowerCase().contains("gate3")|source.toLowerCase().contains("gate4"))&!(destination.toLowerCase().contains("gate3")|destination.toLowerCase().contains("gate4"))) {
                    Toast.makeText(getApplicationContext(),"Destination must be Gate3/4 or Source must be Gate3/4", Toast.LENGTH_SHORT).show();
                    return;
                }
                String tripid = "trip"+(++lastId);
                HelperTrip trip = new HelperTrip(destination,source,time,carPlate,user.getUid(),tripid,passengers);
                reference.child(tripid).setValue(trip);

                Toast.makeText(getApplicationContext(), "Trip added successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddTripActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}