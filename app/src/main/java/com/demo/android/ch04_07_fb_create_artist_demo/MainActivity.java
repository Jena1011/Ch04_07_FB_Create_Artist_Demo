package com.demo.android.ch04_07_fb_create_artist_demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Toast;

import com.demo.android.ch04_07_fb_create_artist_demo.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding myUI;
    private DatabaseReference db_ArtistRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myUI = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(myUI.getRoot());
        myUI.btAddAtrist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArtist();
            }
        });
        myUI.tvStatus.setMovementMethod(new ScrollingMovementMethod());
        myUI.tvDbStatus.setMovementMethod(new ScrollingMovementMethod());
        myUI.btFindAtrist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findArtist();
            }
        });
        myUI.btUpdateAtrist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateArtist();
            }
        });
        myUI.btDeleteAtrist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAtrist();
            }
        });
        db_ArtistRef = FirebaseDatabase.getInstance().getReference("artists");

    }

    private void deleteAtrist() {
        String key = myUI.etIdStatus.getText().toString().trim();
        db_ArtistRef.child(key).removeValue();
    }

    private void updateArtist() {
        String key = myUI.etIdStatus.getText().toString().trim();
        String name = myUI.etName.getText().toString().trim();
        String genre = myUI.spGenres.getSelectedItem().toString();
        HashMap<String,Object> new_data = new HashMap<>();
        new_data.put("artistId",key);
        new_data.put("artistName",name);
        new_data.put("artistGenre",genre);
        db_ArtistRef.child(key).updateChildren(new_data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        db_ArtistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myUI.tvDbStatus.setText("");
                String tmp = "";
                if(snapshot.exists()){
                    for(DataSnapshot ds:snapshot.getChildren()){
                        Artist ar = ds.getValue(Artist.class);
                        tmp += ar.artistName + "\n" + ar.artistGenre +"\n" + ar.artistId + "\n\n";
                    }
                }
                myUI.tvDbStatus.setText(tmp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void findArtist() {
        Query query = db_ArtistRef.orderByChild("artistName").equalTo(myUI.etName.getText().toString().trim());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tmp = "";
                String tmpId = "";
                if(snapshot.exists()){
                    for(DataSnapshot ds:snapshot.getChildren()){
                        Artist ar = ds.getValue(Artist.class);
                        tmp += ar.artistName + "\n" + ar.artistGenre +"\n" + ar.artistId + "\n\n";
                        tmpId += ar.artistId + "\n";
                    }
                }
                myUI.tvStatus.setText(tmp);
                myUI.etIdStatus.setText(tmpId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addArtist() {
        String name = myUI.etName.getText().toString().trim();
        String genre = myUI.spGenres.getSelectedItem().toString();
        if(!TextUtils.isEmpty(name)){
            String id = db_ArtistRef.push().getKey();
            Artist artist = new Artist(id,name,genre);
            db_ArtistRef.child(id).setValue(artist);
            Toast.makeText(this, "Artist name:"+name, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "You should enter a name", Toast.LENGTH_SHORT).show();
        }
    }
}