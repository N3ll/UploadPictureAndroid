package l1nt.mandatory01;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Communication communication;
    String token = "token";
    SharedPreferences prefs = null;
    Context context;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    ImageView imageView;
    String imgName = "imageName";
    Bitmap imageBitmap;
    EditText caseId;
    EditText desc;
    OkCancelInputDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);

        caseId = (EditText) findViewById(R.id.caseId);
        desc = (EditText) findViewById(R.id.desc);
        imageView = (ImageView) findViewById(R.id.imageButton1);

        imageView.setImageResource(R.drawable.take);
        caseId.setText("");
        desc.setText("");

        communication = new Communication(this);
        communication.setupSSLCertificate();
        prefs = getSharedPreferences("l1nt.mandatory01", MODE_PRIVATE);
        context = this;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (prefs.getBoolean("firstrun", true)) {
            System.out.println("First time");
            this.createUser();
        }
    }

    public void createUser() {
        dialog = new OkCancelInputDialog(this, "Create user", "Choose a username") {
            @Override
            public void clickOk() {
                Toast toast = Toast.makeText(context, "Creating user...please wait", Toast.LENGTH_LONG);
                toast.show();
                //Communication coms = new Communication(context);
                communication.CreateUser(getUserInput(), token += System.currentTimeMillis());
                super.clickOk();
                prefs.edit().putBoolean("firstrun", false).commit();
            }
        };
        dialog.show();
    }


    public void takePicture(View view) {

        imageView = (ImageView) findViewById(R.id.imageButton1);
        dispatchTakePictureIntent();
    }

    public void uploadPicture(View view) {

        String checkCase = caseId.getText().toString();
        String checkDesc = desc.getText().toString();

        if (checkCase.matches("") || checkDesc.matches("") || imgName == "imageName") {
            Toast.makeText(this, "You are missing either a field or a picture", Toast.LENGTH_SHORT).show();
            return;
        } else {
            imgName += System.currentTimeMillis() + "";
            communication.uploadPicture(imageBitmap, token, checkCase, checkDesc, imgName);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            if (mCurrentPhotoPath != null) {
                //decoding the file into a bitmap
                imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                //setting the bitmap on the image file.
                imageView.setImageBitmap(imageBitmap);
                imgName += System.currentTimeMillis() + "";
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "l1nt.mandatory01.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    List<ResolveInfo> resolvedIntentActivities = context.getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                        String packageName = resolvedIntentInfo.activityInfo.packageName;
                        context.grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //ALWAYS CALL THE SUPER METHOD
        super.onSaveInstanceState(outState);
        /* Here we put code now to save the state */
        outState.putString("path", mCurrentPhotoPath);
        outState.putString("caseId", caseId.getText().toString());
        outState.putString("desc", desc.getText().toString());
        outState.putString("imgName", this.imgName);

        if (dialog != null && dialog.getUserInput() != "") {
            outState.putString("username", dialog.getUserInput());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        caseId.setText(savedState.getString("caseId"));
        desc.setText(savedState.getString("desc"));

        ImageButton imgBtn = (ImageButton) findViewById(R.id.imageButton1);
        if (savedState.getString("path") != null) {
            this.mCurrentPhotoPath = savedState.getString("path");
            this.imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            //setting the bitmap on the image file.
            imgBtn.setImageBitmap(imageBitmap);
            this.imgName = savedState.getString("imgName");
        } else {
            imgBtn.setImageResource(R.drawable.take);
        }

        if (savedState.getString("username") != null || savedState.getString("username")!="") {
            this.dialog.setUserInput(savedState.getString("username"));
        }
    }
}
