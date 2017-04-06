package com.android.efforts.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.efforts.R;
import com.android.efforts.activity.BarcodeActivity;
import com.android.efforts.activity.HomeActivity;
import com.android.efforts.customclass.VolleyMultipartRequest;
import com.android.efforts.customclass.VolleySingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

public class SelloutFragment extends Fragment implements Response.ErrorListener {

    @BindView(R.id.scan_result)
    TextView scanResult;
    @BindView(R.id.invoice_preview)
    ImageView invoicePreview;
    @BindView(R.id.sellout_submit_button)
    Button sellout_submit_button;

    private byte[] imageByte;
    private int imageWidth;
    private int imageHeight;
    private File actualImage;
    private File compressedImage;
    private String mCurrentPhotoPath;
    private Bitmap imageBitmap;
    private String scanResultText;
    private int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private ProgressDialog progressDialog;

    @OnClick(R.id.sellout_submit_button)
    public void submitSellout() {
        //scanResultText = "DQB8J52";
        if(scanResultText == null) {
            //cannot proceed if there are no barcode result
            Toast.makeText(getActivity(), "Anda belum memasukan hasil pindaian penjualan barang!", Toast.LENGTH_SHORT).show();
        } else {
            if(imageByte == null) {
                //cannot proceed if no image byte exists
                Toast.makeText(getActivity(), "Anda belum memasukan hasil foto penjualan barang!", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog = new ProgressDialog(getActivity(), R.style.CustomDialog);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Mohon tunggu...");
                progressDialog.show();

                final SharedPreferences sharedPref = getActivity().getSharedPreferences("userCred", Context.MODE_PRIVATE);
                Log.d("SharedPref", sharedPref.getString("jwt", "NoToken"));
                Log.d("SharedPref", sharedPref.getString("store_id", "NoID"));
                String token = sharedPref.getString("jwt", "empty token");
                String url = getString(R.string.create_sellout);
                //set headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "multipart/form-data");
                headers.put("Authorization", token);

                VolleyMultipartRequest newVMReq = new VolleyMultipartRequest(url, headers, new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String resultResponse = new String(response.data);
                        progressDialog.dismiss();
                        try {
                            JSONObject result = new JSONObject(resultResponse);
                            Log.d("JSONResult", "result: "+result.toString(2));
                            Toast.makeText(getActivity(), "Pencatatan penjualan berhasil!", Toast.LENGTH_SHORT).show();
                            //change fragment
                            HomeActivity act = (HomeActivity) getActivity();
                            act.changeFragment(new HomeFragment());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, this)
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        //params.put("service_tag", "DQB8J52");
                        params.put("service_tag", scanResult.getText().toString());
                        String store_id = sharedPref.getString("store_id", "NoID");
                        Log.d("checkParams", store_id);
                        params.put("store_id", store_id);
                        return params;
                    }

                    @Override
                    protected Map<String, DataPart> getByteData() throws AuthFailureError {
                        Map<String, DataPart> params = new HashMap<>();
                        if(imageByte != null) {
                            params.put("proof", new DataPart("proof.jpg", imageByte, "image/jpeg"));
                        }
                        return params;
                    }
                };
                //send the request
                VolleySingleton.getInstance(getContext()).addToRequestQueue(newVMReq);
            }
        }
    }

    public SelloutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sellout, container, false);
        ButterKnife.bind(this, view);

        invoicePreview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageWidth = invoicePreview.getWidth();
                imageHeight = invoicePreview.getHeight(); //height is ready
                Log.d("imageWidth", String.valueOf(imageWidth));
                Log.d("imageHeight", String.valueOf(imageHeight));
                invoicePreview.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        //set title
        getActivity().setTitle(R.string.title_sellout_fragment);
        return view;
    }

    @OnClick(R.id.scan_button)
    public void scanButton() {
        /*IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setCaptureActivity(BarcodeActivity.class);
        integrator.addExtra("PROMPT_MESSAGE", "");
        integrator.initiateScan();*/

        Intent intent = new Intent(getActivity(), BarcodeActivity.class);  //new Intent("BarcodeActivity");
        //Intent x = new Intent(getActivity(), BarcodeActivity.class);
        //intent.putExtra("SCAN_FORMATS", "QR_CODE_MODE");
        startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);
    }

    @OnClick(R.id.pick_image_button)
    public void pickImage(){
//        if(isStoragePermissionGranted()){
//            doPickImage();
//        }else{
//            //Toast.makeText(getActivity(), "Please give permission for image picker", Toast.LENGTH_LONG).show();
//        }

        //testing the camera to launch
        dispatchTakePictureIntent();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("test1", "result2");
        IntentResult sr = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, data);

        if (sr != null) {
            if(sr.getContents() != null) {
                Log.d("result in fragment", sr.getContents());
                //Toast.makeText(getActivity(), "scanResult : " + sr.getContents(), Toast.LENGTH_LONG).show();
                scanResultText = sr.getContents();
                scanResult.setText(sr.getContents());
            }
        }
        //new method for image
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data == null) {
                showError("Failed to open picture!");
                return;
            }
            try {
                actualImage = FileUtil.from(getContext(), data.getData());
                invoicePreview.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
                Log.d("Original file size: ",String.format("Size : %s", getReadableFileSize(actualImage.length())));
                compressImage();
            } catch (IOException e) {
                showError("Failed to read picture data!");
                e.printStackTrace();
            }
        } else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            try {
//                String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), imageBitmap, "Title", null);
//                Uri uriLink = Uri.parse(path);
//                actualImage = FileUtil.from(getContext(), uriLink);
//                compressImage();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            invoicePreview.setImageBitmap(imageBitmap);
            setPic();
        }
    }

    private void showError(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                //add file to gallery
                galleryAddPic();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("PhotoFile", "Error creating file!");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private void doPickImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void compressImage() {
        if (actualImage == null) {
            showError("Please choose an image!");
        } else {

            // Compress image in main thread
            //compressedImage = Compressor.getDefault(this).compressToFile(actualImage);
            //setCompressedImage();

            // Compress image to bitmap in main thread
            /*compressedImageView.setImageBitmap(Compressor.getDefault(this).compressToBitmap(actualImage));*/

            // Compress image using RxJava in background thread
            Compressor.getDefault(getContext())
                    .compressToFileAsObservable(actualImage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<File>() {
                        @Override
                        public void call(File file) {
                            compressedImage = file;
                            setCompressedImage();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            showError(throwable.getMessage());
                        }
                    });
        }
    }

    private void setCompressedImage() {
//        compressedImageView.setImageBitmap(BitmapFactory.decodeFile(compressedImage.getAbsolutePath()));
//        compressedSizeTextView.setText(String.format("Size : %s", getReadableFileSize(compressedImage.length())));

        //Toast.makeText(getActivity(), "Compressed image save in " + compressedImage.getPath(), Toast.LENGTH_LONG).show();
        Log.d("Compressor", "Compressed image save in " + compressedImage.getPath());
        Log.d("Compressor", String.format("Size : %s", getReadableFileSize(compressedImage.length())));

        //get the file converted to image byte array
        if(compressedImage != null) {
            //imageByte = convertFileToByteArray(compressedImage);
            //no compression, so I pass instead still the raw image
            imageByte = convertFileToByteArray(actualImage);
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d("image picker","Permission is granted");
                return true;
            } else {

                Log.d("image picker","Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.d("image picker","Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.d("image picker","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
            //doPickImage();
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progressDialog.dismiss();
        Log.d("ErrorResponse", "Error response: "+error.getMessage());

        Toast.makeText(getActivity(), "Barang belum masuk inventaris toko, mohon cek kembali", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(getActivity(), HomeSRActivity.class);
//        startActivity(intent);
//        getActivity().finish();
    }

//    @Override
//    public void onResponse(JSONObject response) {
//        try {
//            Log.d("Response", "JSON Response: "+response.toString(2));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        Toast.makeText(getActivity(), "Pelaporan penjualan sudah masuk!", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(getActivity(), HomeSRActivity.class);
//        startActivity(intent);
//        getActivity().finish();
//    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private byte[] getByteImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();

        return imageBytes;
    }

    private byte[] convertFileToByteArray(File file) {
        byte[] b = new byte[(int) file.length()];
        //try to convert the file to byte array
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(b);
            for (int i = 0; i < b.length; i++) {
                System.out.print((char)b[i]);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
            e.printStackTrace();
        }
        catch (IOException e1) {
            System.out.println("Error Reading The File.");
            e1.printStackTrace();
        }

        return b;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageWidth;
        int targetH = imageHeight;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        File file = new File(mCurrentPhotoPath);
        Log.d("Not Compressor", String.format("Size : %s", getReadableFileSize(file.length())));
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        Log.d("targetW", String.valueOf(targetW));
        Log.d("targetH", String.valueOf(targetH));

        Log.d("photoW", String.valueOf(photoW));
        Log.d("photoH", String.valueOf(photoH));

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        //bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        invoicePreview.setImageBitmap(bitmap);

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            imageByte = new byte[(int) file.length()];
            fis.read(imageByte); //read file into bytes[]
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
