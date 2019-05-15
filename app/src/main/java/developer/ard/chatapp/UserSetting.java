package developer.ard.chatapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class UserSetting extends AppCompatActivity {

    private static final int GALLERY_PICK = 1;

    // Storage Firebase
    private StorageReference mImageStorage;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

  String m_Text = "";

    private ProgressDialog mProgressDialog;

    TextView mNama,mStatus,mEmail;
    ImageButton editnama,editstatus,editfoto;
    private CircleImageView mDisplayImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editfoto = findViewById(R.id.editfoto);
        editnama = findViewById(R.id.editnama);
        editstatus = findViewById(R.id.editstatus);

        mNama =findViewById(R.id.namaTxt);
        mStatus = findViewById(R.id.statusTxt);
        mEmail = findViewById(R.id.emailTxt);
        mDisplayImage =  findViewById(R.id.imageprofile);



        mImageStorage = FirebaseStorage.getInstance().getReference();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);


        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("nama") && dataSnapshot.hasChild("email") && dataSnapshot.hasChild("image") && dataSnapshot.hasChild("status") && dataSnapshot.hasChild("thumb_image")) {
                    String email = dataSnapshot.child("email").getValue().toString();
                    String dname = dataSnapshot.child("nama").getValue().toString();
                    final String image = dataSnapshot.child("image").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                    mNama.setText(dname);
                    mStatus.setText(status);
                    mEmail.setText(email);

                    if (!image.equals("default")) {

                        Picasso.with(UserSetting.this).load(image).placeholder(R.drawable.usera).into(mDisplayImage);

                        Picasso.with(UserSetting.this).load(image).placeholder(R.drawable.usera).into(mDisplayImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("tes","sukses");
                            }

                            @Override
                            public void onError() {
                                Log.d("tes","error");
                                Picasso.with(UserSetting.this).load(image).placeholder(R.drawable.usera).into(mDisplayImage);

                            }
                        });

                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        editfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });

        editstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserSetting.this);
                builder.setTitle("Ganti Status");

                final EditText input = new EditText(UserSetting.this);
                input.setText(mNama.getText().toString());


                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        mProgressDialog = new ProgressDialog(UserSetting.this);
                        mProgressDialog.setTitle("Saving Changes");
                        mProgressDialog.setMessage("Please wait while we save the changes");
                        mProgressDialog.show();
                        mUserDatabase.child("status").setValue(m_Text).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    mProgressDialog.dismiss();

                                } else {

                                    Toast.makeText(getApplicationContext(), "Gagal Mengganti Status", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            }
        });

        editnama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(UserSetting.this);
                builder.setTitle("Ganti Nama");

                final EditText input = new EditText(UserSetting.this);
                input.setText(mNama.getText().toString());


                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        mProgressDialog = new ProgressDialog(UserSetting.this);
                        mProgressDialog.setTitle("Saving Changes");
                        mProgressDialog.setMessage("Please wait while we save the changes");
                        mProgressDialog.show();
                        mUserDatabase.child("nama").setValue(m_Text).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    mProgressDialog.dismiss();

                                } else {

                                    Toast.makeText(getApplicationContext(), "Gagal Mengganti Nama", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);

            //Toast.makeText(SettingsActivity.this, imageUri, Toast.LENGTH_LONG).show();

        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {


                mProgressDialog = new ProgressDialog(UserSetting.this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Please wait....");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();


                Uri resultUri = result.getUri();

                File thumb_filePath = new File(resultUri.getPath());

                String current_user_id = mCurrentUser.getUid();


                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_filePath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();


                final StorageReference filepath = mImageStorage.child("profile_image").child(current_user_id + ".jpg");
                final StorageReference thumb_filepath = mImageStorage.child("profile_image").child("thumbs").child(current_user_id + ".jpg");




                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {





                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {



                                    if (thumb_task.isSuccessful()) {

                                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String download_url = uri.toString();
                                                Map update_hashMap = new HashMap();
                                                update_hashMap.put("image", ""+download_url);
                                                mUserDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {

                                                        }

                                                    }
                                                });
                                            }
                                        });

                                        thumb_filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                              String thumb_downloadUrl = uri.toString();
                                                Map update_hashMap = new HashMap();
                                                update_hashMap.put("thumb_image", ""+thumb_downloadUrl);
                                                mUserDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {

                                                            mProgressDialog.dismiss();
                                                            Toast.makeText(UserSetting.this, "Berhasil Mengganti Foto.", Toast.LENGTH_LONG).show();

                                                        }

                                                    }
                                                });
                                            }
                                        });







                                    } else {

                                        Toast.makeText(UserSetting.this, "Gagal mengganti foto", Toast.LENGTH_LONG).show();
                                        mProgressDialog.dismiss();

                                    }


                                }
                            });


                        } else {

                            Toast.makeText(UserSetting.this, "Error in uploading.", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();

                        }

                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }

}
