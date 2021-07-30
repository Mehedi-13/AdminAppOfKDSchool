package com.example.adminsofkdschool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.adminsofkdschool.faculty.UpdateFaculty;
import com.example.adminsofkdschool.notice.DeleteNoticeActivity;

public class Dashboard extends AppCompatActivity implements View.OnClickListener {

    private CardView  uploadeTheNotice,addGalleryImage,addEbook,faculty,deleteNotice,logout;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sharedPreferences =this.getSharedPreferences("login",MODE_PRIVATE);
        editor= sharedPreferences.edit();

        if (sharedPreferences.getString("isLogin","false").equals("false")){
            openLogin();
        }

        uploadeTheNotice= findViewById(R.id.addNotice);
        uploadeTheNotice.setOnClickListener(this);

        addGalleryImage= findViewById(R.id.addGalleryImage);
        addGalleryImage.setOnClickListener(this);

        addEbook=findViewById(R.id.addEbook);
        addEbook.setOnClickListener(this);

        faculty=findViewById(R.id.faculty);
        faculty.setOnClickListener(this);

        deleteNotice=findViewById(R.id.deleteNotice);
        deleteNotice.setOnClickListener(this);

        logout=findViewById(R.id.logOut);
        logout.setOnClickListener(this);

    }

    private void openLogin() {
        startActivity(new Intent(Dashboard.this,LoginActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {

        Intent intent;
        switch (v.getId()){
            case R.id.addNotice:
                 intent= new Intent(Dashboard.this,UploadNotice.class);
                startActivity(intent);
                break;

            case R.id.addGalleryImage:
                 intent= new Intent(Dashboard.this,UploadingImage.class);
                startActivity(intent);
                break;

            case R.id.addEbook:
                intent= new Intent(Dashboard.this,UploadPdf.class);
                startActivity(intent);
                break;

            case R.id.faculty:
                intent= new Intent(Dashboard.this, UpdateFaculty.class);
                startActivity(intent);
                break;

            case R.id.deleteNotice:
                intent= new Intent(Dashboard.this, DeleteNoticeActivity.class);
                startActivity(intent);
                break;

            case R.id.logOut:
                editor.putString("isLogin","false");
                editor.commit();
                openLogin();
                break;

        }

    }

}