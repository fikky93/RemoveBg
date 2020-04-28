package org.vq.removebg.UI;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;

import org.vq.removebg.R;
import org.vq.removebg.Services.ApiInterface;
import org.vq.removebg.helper.ServiceGenerator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button remove, download;
    private ImageButton imgBtn;
    private Uri fileUri;
    private ImageView imgResuly;

    private Bitmap bitmap;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StartAppSDK.init(this, "203588271", true);
        StartAppAd.disableSplash();

        setContentView(R.layout.activity_main);

        remove = (Button)findViewById(R.id.btnRemove);
        download = (Button)findViewById(R.id.btnDownload);
        imgBtn = (ImageButton)findViewById(R.id.btnPickImage);
        imgResuly = (ImageView)findViewById(R.id.imgResult);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Enteni");

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void selectImage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Pick Image");
        builder.setMessage("Select image source");
        builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1);
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            return;
        }
        if (requestCode == 1) {
            if (data != null) {
                fileUri = data.getData();
                bitmap = (Bitmap) data.getExtras().get("data");
                imgBtn.setImageBitmap(bitmap);
            }
        } else if (requestCode == 2) {
            if (data != null) {
                fileUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imgBtn.setImageBitmap(bitmap);
            }
        }
    }

    private void doUpload() {
        File file = createTempFile(bitmap);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image_file", file.getName(), reqFile);

        ApiInterface service = ServiceGenerator.createService(ApiInterface.class, "pMmheRSwDziLRR8Gzw4C5aWc ");
        Call<ResponseBody> call = service.doAction(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        byte[] data = response.body().bytes();
                        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                    imgResuly.setImageBitmap(bitmap);
                    download.setEnabled(true);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Error Upload", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error Connection", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private File createTempFile(Bitmap bitmap) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                , System.currentTimeMillis() + "_image.png");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitmapdata = bos.toByteArray();
        //write the bytes in file

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public void handleDownload(View view) {
        bitmap = ((BitmapDrawable) imgResuly.getDrawable()).getBitmap();
        File path = Environment.getExternalStorageDirectory();
        File dir = new File(path + "/PasCode/");
        dir.mkdir();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        String imagename = "RemoveBg_" + currentDateandTime + ".PNG";
        File file = new File(dir, imagename);
        OutputStream out;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Toast.makeText(MainActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleRemove(View view) {
        if (bitmap != null){
            progressDialog.show();
            doUpload();
        }else {
            Toast.makeText(this, "Select Image", Toast.LENGTH_SHORT).show();
        }
    }
}
