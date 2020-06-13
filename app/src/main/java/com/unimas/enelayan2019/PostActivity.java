package com.unimas.enelayan2019;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {
    private BottomNavigationView botNav;
    private FloatingActionButton fab;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Dialog popAddPost;
    private EditText postTitle, postDescription;
    private ImageView postImage, addPostButton;
    private CircleImageView userImage;
    private ProgressBar postProgress;
    private Uri ImageURI;
    private TextView selectImageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        initPopup();
        selectPostImage();

        fab = (FloatingActionButton) findViewById(R.id.addPost);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popAddPost.show();
            }
        });

        botNav = findViewById(R.id.bottomNav);
        botNav.setSelectedItemId(R.id.post);
        botNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.post:
                        return true;

                    case R.id.cart:
                        startActivity(new Intent(getApplicationContext(), CartActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.account:
                        startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    private void selectPostImage() {
        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 22){
                    checkAndRequestForPermissions();
                }
                else {
                    openGallery();
                }
            }
        });
    }

    private void initPopup(){
        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.add_post_layout);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        postTitle = popAddPost.findViewById(R.id.postTitle);
        selectImageText = popAddPost.findViewById(R.id.selectImageText);
        postDescription = popAddPost.findViewById(R.id.postDetails);
        userImage = popAddPost.findViewById(R.id.userPics);
        postImage = popAddPost. findViewById(R.id.postImage);
        addPostButton = popAddPost.findViewById(R.id.doneButton);
        postProgress = popAddPost.findViewById(R.id.progressBar);

        try {
            Glide.with(PostActivity.this).load(user.getPhotoUrl()).into(userImage);
        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Glide error: "+e, Toast.LENGTH_SHORT).show();
        }


        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPostButton.setVisibility(View.INVISIBLE);
                postProgress.setVisibility(View.VISIBLE);


            }
        });

    }

    private void checkAndRequestForPermissions(){
        if (ContextCompat.checkSelfPermission(PostActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(PostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(getApplicationContext(), "Please accept the permission to access your storage", Toast.LENGTH_SHORT).show();
            }else {
                ActivityCompat.requestPermissions(PostActivity.this,
                        new String[] { Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
                openGallery();
            }
        }else {
            openGallery();
        }
    }

    private void openGallery(){
        CropImage.activity().start(PostActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK){
                ImageURI = result.getUri();
                postImage.setImageURI(ImageURI);
                selectImageText.setVisibility(View.INVISIBLE);
            }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception e = result.getError();
                Toast.makeText(PostActivity.this, "Possible error occured is " +e, Toast.LENGTH_SHORT).show();
            }

        }
    }
}