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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class UpdateTeacherActivity extends AppCompatActivity {

    private ImageView updateTeacherImage;
    private EditText updateTeacherName,updateTeacherEmail,updateTeacherPost,updateTeacherPhoneNumber,updateTeacherShift;
    private Button updateTeacherBtn,deleteTeacherBtn;
    private StorageReference storageReference;
    private DatabaseReference reference;
    private String name,email,post,image,shift,phoneNumber;
    private final int request=1;
    private Bitmap bitmap=null;
    private String downloadUrl,uniqueKey,category;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_teacher);

        reference= FirebaseDatabase.getInstance().getReference().child("Teacher");
        storageReference= FirebaseStorage.getInstance().getReference();

        pd= new ProgressDialog(this);

         uniqueKey= getIntent().getStringExtra("key");

         category= getIntent().getStringExtra("category");

        name=getIntent().getStringExtra("name");
        email=getIntent().getStringExtra("email");
        post=getIntent().getStringExtra("post");
        image=getIntent().getStringExtra("image");
        shift=getIntent().getStringExtra("shift");
        phoneNumber=getIntent().getStringExtra("phoneNumber");

        updateTeacherImage =findViewById(R.id.updateTeacherImage);
        updateTeacherName =findViewById(R.id.updateTeacherName);
        updateTeacherEmail =findViewById(R.id.updateTeacherEmail);
        updateTeacherPost =findViewById(R.id.updateTeacherPost);
        updateTeacherPhoneNumber =findViewById(R.id.updateTeacherPhoneNumber);
        updateTeacherShift =findViewById(R.id.updateTeacherShift);
        updateTeacherBtn =findViewById(R.id.updateTeacherBtn);
        deleteTeacherBtn =findViewById(R.id.deleteTeacherBtn);

        try {
            Picasso.get().load(image).into(updateTeacherImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateTeacherName.setText(name);
        updateTeacherPost.setText(post);
        updateTeacherEmail.setText(email);
        updateTeacherPhoneNumber.setText(phoneNumber);
        updateTeacherShift.setText(shift);

        updateTeacherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        updateTeacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name= updateTeacherName.getText().toString();
                post= updateTeacherPost.getText().toString();
                email= updateTeacherEmail.getText().toString();
                phoneNumber= updateTeacherPhoneNumber.getText().toString();
                shift= updateTeacherShift.getText().toString();
                checkValidation();
            }
        });
        
        deleteTeacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Deleting...");
                pd.show();
                deleteData();
            }
        });

    }

    private void deleteData() {
        reference.child(category).child(uniqueKey).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        Toast.makeText(UpdateTeacherActivity.this, "Teacher Deleted successfully", Toast.LENGTH_SHORT).show();
                        Intent intent= new Intent(UpdateTeacherActivity.this,UpdateFaculty.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(UpdateTeacherActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkValidation() {
        if (name.isEmpty()){
            updateTeacherName.setError("Empty");
            updateTeacherName.requestFocus();
        } else if (post.isEmpty()){
            updateTeacherPost.setError("Empty");
            updateTeacherPost.requestFocus();
        }else if (email.isEmpty()){
            updateTeacherEmail.setError("Empty");
            updateTeacherEmail.requestFocus();
        }else if (phoneNumber.isEmpty()){
            updateTeacherPhoneNumber.setError("Empty");
            updateTeacherPhoneNumber.requestFocus();
        }else if (shift.isEmpty()){
            updateTeacherShift.setError("Empty");
            updateTeacherShift.requestFocus();
        }else if (bitmap==null){
            updateData(image);
        }else {
            uplodeImage();
        }
    }

    private void updateData(String s) {
        pd.setMessage("Updating...");
        pd.show();
        HashMap map= new HashMap();
        map.put("name",name);
        map.put("post",post);
        map.put("phoneNumber",phoneNumber);
        map.put("email",email);
        map.put("shift",shift);
        map.put("image",s);



        reference.child(category).child(uniqueKey).updateChildren(map).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(UpdateTeacherActivity.this, "Teacher Updated successfully", Toast.LENGTH_SHORT).show();
                Intent intent= new Intent(UpdateTeacherActivity.this,UpdateFaculty.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(UpdateTeacherActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uplodeImage() {

        ByteArrayOutputStream baos= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50,baos);
        byte[] finalImage= baos.toByteArray();
        final StorageReference filePath;
        filePath= storageReference.child("Teachers").child(finalImage+"jpg");
        final UploadTask uploadTask= filePath.putBytes(finalImage);
        uploadTask.addOnCompleteListener(UpdateTeacherActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                                    updateData(downloadUrl);
                                }
                            });
                        }
                    });
                    startActivity(new Intent(UpdateTeacherActivity.this, Dashboard.class));
                }else {
                    pd.dismiss();
                    Toast.makeText(UpdateTeacherActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
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
            updateTeacherImage.setImageBitmap(bitmap);
        }
    }


}