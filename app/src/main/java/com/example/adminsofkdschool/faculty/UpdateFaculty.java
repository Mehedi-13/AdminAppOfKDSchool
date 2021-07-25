package com.example.adminsofkdschool.faculty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.adminsofkdschool.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class UpdateFaculty extends AppCompatActivity {

    private FloatingActionButton fab;
    private RecyclerView cDepartment,sDepartment,aDepartment;
    private LinearLayout cNoData,sNoData,aNoData;
    private List<TeacherData> list1,list2,list3;
    private DatabaseReference reference,dbRef;
    private TeacherAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_faculty);

        cDepartment=findViewById(R.id.cDepartment);
        sDepartment=findViewById(R.id.sDepartment);
        aDepartment=findViewById(R.id.aDepartment);

        cNoData=findViewById(R.id.cNoData);
        sNoData=findViewById(R.id.sNoData);
        aNoData=findViewById(R.id.aNoData);

        reference= FirebaseDatabase.getInstance().getReference().child("Teacher");

        cDepartment();
        sDepartment();
        aDepartment();

        fab= findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateFaculty.this,AddTeacher.class));
            }
        });
    }

    private void cDepartment() {
        dbRef= reference.child("Commerce");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list2= new ArrayList<>();
                if (!snapshot.exists()){
                    cNoData.setVisibility(View.VISIBLE);
                    cDepartment.setVisibility(View.GONE);
                }else {
                    cNoData.setVisibility(View.GONE);
                    cDepartment.setVisibility(View. VISIBLE);

                    for (DataSnapshot snapshot1 :snapshot.getChildren()){
                        TeacherData data= snapshot1.getValue(TeacherData.class);
                        list2.add(data);
                    }
                    cDepartment.setHasFixedSize(true);
                    cDepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));
                    adapter= new TeacherAdapter(list2,UpdateFaculty.this,"Commerce");
                    adapter.notifyDataSetChanged();
                    cDepartment.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(UpdateFaculty.this,error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sDepartment() {
        dbRef= reference.child("Science");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list1= new ArrayList<>();
                if (!snapshot.exists()){
                    sNoData.setVisibility(View.VISIBLE);
                    sDepartment.setVisibility(View.GONE);
                }else {
                    sNoData.setVisibility(View.GONE);
                    sDepartment.setVisibility(View. VISIBLE);

                    for (DataSnapshot snapshot1 :snapshot.getChildren()){
                        TeacherData data= snapshot1.getValue(TeacherData.class);
                        list1.add(data);
                    }
                    sDepartment.setHasFixedSize(true);
                    sDepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));
                    adapter= new TeacherAdapter(list1,UpdateFaculty.this,"Science");
                    adapter.notifyDataSetChanged();

                    sDepartment.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(UpdateFaculty.this,error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void aDepartment() {
        dbRef= reference.child("Arts");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list3= new ArrayList<>();
                if (!snapshot.exists()){
                    aNoData.setVisibility(View.VISIBLE);
                    aDepartment.setVisibility(View.GONE);
                }else {
                    aNoData.setVisibility(View.GONE);
                    aDepartment.setVisibility(View. VISIBLE);

                    for (DataSnapshot snapshot1 :snapshot.getChildren()){
                        TeacherData data= snapshot1.getValue(TeacherData.class);
                        list3.add(data);
                    }
                    aDepartment.setHasFixedSize(true);
                    aDepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculty.this));
                    adapter= new TeacherAdapter(list3,UpdateFaculty.this,"Arts");
                    adapter.notifyDataSetChanged();

                    aDepartment.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(UpdateFaculty.this,error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}