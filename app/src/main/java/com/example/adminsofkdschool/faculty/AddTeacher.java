package com.example.adminsofkdschool.faculty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.adminsofkdschool.Dashboard;
import com.example.adminsofkdschool.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddTeacher extends AppCompatActivity {

    private ImageView addTeacherImage;
    private EditText addTeacherName,addTeacherPost, addTeacherEmail,addTeacherPhoneNumber,addTeacherShift;
    private Spinner addTeacherCategory;
    private Button addTeacherBtn;
    private final int request=1;
    private Bitmap bitmap= null;
    private String category;
    private String name,post,email,phoneNumber,shift,downloadUrl="";

    private ProgressDialog pd;
    private StorageReference storageReference;
    private DatabaseReference reference,dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);

        pd= new ProgressDialog(this);
        reference= FirebaseDatabase.getInstance().getReference().child("Teacher");
        storageReference= FirebaseStorage.getInstance().getReference();

        addTeacherImage= findViewById(R.id.addTeacherImage);
        addTeacherName= findViewById(R.id.addTeacherName);
        addTeacherPost= findViewById(R.id.addTeacherPost);
        addTeacherEmail= findViewById(R.id.addTeacherEmail);
        addTeacherPhoneNumber= findViewById(R.id.addPhoneNumber);
        addTeacherShift= findViewById(R.id.addTeacherShift);
        addTeacherCategory= findViewById(R.id.add_teacher_Category);
        addTeacherBtn= findViewById(R.id.addTeacherBtn);


        String[] items = new String[]{"Select Category","Commerce","Science","Arts"};
        addTeacherCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,items));


        addTeacherCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category= addTeacherCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        addTeacherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addTeacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValiDation();
            }
        });

    }



    private void checkValiDation() {
        name= addTeacherName.getText().toString();
        email= addTeacherEmail.getText().toString();
        post= addTeacherPost.getText().toString();
        shift= addTeacherShift.getText().toString();
        phoneNumber= addTeacherPhoneNumber.getText().toString();

        if (name.isEmpty()){
            addTeacherName.setError("Empty");
            addTeacherName.requestFocus();
        }
        else if (post.isEmpty()){
            addTeacherPost.setError("Empty");
            addTeacherPost.requestFocus();
        }
        else if (email.isEmpty()){
            addTeacherEmail.setError("Empty");
            addTeacherEmail.requestFocus();
        }
        else if (phoneNumber.isEmpty()){
            addTeacherPhoneNumber.setError("Empty");
            addTeacherPhoneNumber.requestFocus();
        }
        else if (shift.isEmpty()){
            addTeacherShift.setError("Empty");
            addTeacherShift.requestFocus();
        }
        else if (category.equals("Select Category")){
            Toast.makeText(this, "Please,Provide teacher category", Toast.LENGTH_SHORT).show();
        }
        else if (bitmap==null){
            pd.setMessage("Uploading...");
            pd.show();
            insertData();
        }
        else {
            pd.setMessage("Uploading...");
            pd.show();
            uplodeImage();
        }

    }

    private void uplodeImage() {

        ByteArrayOutputStream baos= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50,baos);
        byte[] finalImage= baos.toByteArray();
        final StorageReference filePath;
        filePath= storageReference.child("Teachers").child(finalImage+"jpg");
        final UploadTask uploadTask= filePath.putBytes(finalImage);
        uploadTask.addOnCompleteListener(AddTeacher.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()){
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl=String.valueOf(uri);
                                    insertData();
                                }
                            });
                        }
                    });
                    startActivity(new Intent(AddTeacher.this, Dashboard.class));
                }else {
                    pd.dismiss();
                    Toast.makeText(AddTeacher.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void insertData() {
        dbRef= reference.child(category);
        final String uniqueKey= dbRef.push().getKey();

       TeacherData teacherData= new TeacherData(name,post,email,phoneNumber,shift,category,downloadUrl,uniqueKey);

        dbRef.child(uniqueKey).setValue(teacherData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
                Toast.makeText(AddTeacher.this, "Teacher Added", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                pd.dismiss();
                Toast.makeText(AddTeacher.this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void openGallery() {
        Intent pickImage= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage,request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==request && resultCode==RESULT_OK){
            Uri uri= data.getData();
            try {
                bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            addTeacherImage.setImageBitmap(bitmap);
        }
    }



}