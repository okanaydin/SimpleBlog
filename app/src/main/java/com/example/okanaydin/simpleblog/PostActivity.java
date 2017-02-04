package com.example.okanaydin.simpleblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private ImageButton mselectImage;
    private EditText mPostTitle, mPostDesc;
    private Button mSubmitBtn;

    private Uri mimageUri = null ;

    private static final int GALLERY_REQUEST = 1;

    private StorageReference mStogare;


    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mStogare = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");


        mselectImage=(ImageButton)findViewById(R.id.imageSelect);

        mPostTitle=(EditText)findViewById(R.id.titleField);
        mPostDesc=(EditText)findViewById(R.id.descField);
        mSubmitBtn=(Button)findViewById(R.id.submitBtn);

        mProgress = new ProgressDialog(this);

        mselectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

            }
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startPosting();

            }
        });


    }

    private void startPosting() {

        mProgress.setMessage("Posting to Blog ...");
        mProgress.show();

            final String title_val = mPostTitle.getText().toString().trim();
            final String desc_val= mPostDesc.getText().toString().trim();

            if (!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && mimageUri != null){

                StorageReference filePath = mStogare.child("Blog Images").child(mimageUri.getLastPathSegment());

                filePath.putFile(mimageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        DatabaseReference newPost = mDatabase.push();

                        newPost.child("title").setValue(title_val);
                        newPost.child("desc").setValue(desc_val);
                        newPost.child("image").setValue(downloadUrl.toString());

                        mProgress.dismiss();

                    }
                });
            }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            mimageUri = data.getData();

            mselectImage.setImageURI(mimageUri );

        }
    }
}
