package com.example.adminsofkdschool;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class UploadPdf extends AppCompatActivity {

    private Spinner pdfCategory;
    private EditText pdfTitle; // filetitle
    private CardView selectPdf;// imagebrowse

    private Button uploadPdf; //imageupload
    private TextView pdfTxtView;

    private Uri pdfDataUri; //filetitle

    private final int request=1;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressDialog pd;
    private FirebaseStorage storage;
    FirebaseDatabase database;
    String downloadUrl="" ;
    private String pdfName, title, url;

    private String category;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);

        pdfTitle=findViewById(R.id.pdfTitle);
        selectPdf= findViewById(R.id.addPdf);
        pdfCategory= findViewById(R.id.pdf_Category);
        uploadPdf= findViewById(R.id.uploadPdfBtn);
        pdfTxtView=findViewById(R.id.pdfTxtView);

        pd= new ProgressDialog(this);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("pdf");
        storageReference= FirebaseStorage.getInstance().getReference();

        String[] items = new String[]{"Select Category","Class Three","Class Four","Class Five","Class Six","Class Seven","Class Eight","Class Nine & Ten","Other PDFs"};
        pdfCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,items));

        pdfCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category= pdfCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        selectPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(UploadPdf.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    // openGallery();
                    pickPdf();
                }else
                    ActivityCompat.requestPermissions(UploadPdf.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);

            }
        });



        uploadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title=pdfTitle.getText().toString();
                if (title.isEmpty()){
                    pdfTitle.setError("Empty");
                    pdfTitle.requestFocus();
                }
                else if (pdfDataUri==null){
                    Toast.makeText(UploadPdf.this, "Please, Upload PDF", Toast.LENGTH_SHORT).show();
                }else if (category.equals("Select Category")){
                    Toast.makeText(UploadPdf.this, "Please, select the PDF Category", Toast.LENGTH_SHORT).show();
                }else{
                    pd.setTitle("Please wait...");
                    pd.setMessage("Uploading pdf...");
                    pd.show();
                    UploadPDF();
                }
            }
        });

    }

    private void UploadPDF() {

        StorageReference reference= storageReference.child("pdf/"+pdfName+"-"+System.currentTimeMillis()+".pdf");
        reference.putFile(pdfDataUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri uri= uriTask.getResult();
                        uploadData(String.valueOf(uri));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadPdf.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {
                float percent=(100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                pd.setMessage("Uploaded :"+(int)percent+"%");
            }
        });
    }


    private void uploadData(String valueOf) {
        databaseReference= databaseReference.child(category);
        final String uniquKey= databaseReference.child("pdf").push().getKey();

        HashMap data= new HashMap();
        data.put("pdfTitle",title);
        data.put("pdfUrl",valueOf);

        //String uniquKey= databaseReference.child("pdf").push().getKey();

        //databaseReference.child("pdf").child(uniquKey).setValue(downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
        databaseReference.child(uniquKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                pd.dismiss();
                Toast.makeText(UploadPdf.this, "PDF uploaded Successfully", Toast.LENGTH_SHORT).show();
                pdfTitle.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadPdf.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickPdf();
        }else
        {
            Toast.makeText(this, "Please, provide permission", Toast.LENGTH_SHORT).show();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void pickPdf() {
        Intent intent= new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select pdf file") ,86);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (requestCode==86 && resultCode==RESULT_OK && data!=null  && data.getData() !=null){
            pdfDataUri=data.getData(); //return the uri of selected file...
            //pdfTxtView.setText("Selected File: "+data.getData().getLastPathSegment());

            if (pdfDataUri.toString().startsWith("content://")){
                Cursor cursor = null;
                try {
                    cursor=UploadPdf.this.getContentResolver().query(pdfDataUri,null,null,null,null);

                    if (cursor!=null && cursor.moveToFirst()){
                        pdfName=cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (pdfDataUri.toString().startsWith("file://")){
                pdfName =  new File(pdfDataUri.toString()).getName();
            }
            pdfTxtView.setText(pdfName);

        }else{
            Toast.makeText(this, "Please select a file", Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}



