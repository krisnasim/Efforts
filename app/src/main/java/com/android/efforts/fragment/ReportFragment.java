package com.android.efforts.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.efforts.R;
import com.android.efforts.activity.HomeActivity;
import com.android.efforts.customclass.CustomJSONObjectRequest;
import com.android.efforts.customclass.JWTUtils;
import com.android.efforts.customclass.ProtoBufRequest;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.FileUtil;
import moe.tsun.nx.api.NxAssetProto;
import moe.tsun.nx.api.NxCommonProto;
import moe.tsun.nx.api.NxFormProto;

import static android.app.Activity.RESULT_OK;

public class ReportFragment extends Fragment implements Response.ErrorListener, Response.Listener<NxFormProto.QrDatum> {

    @BindView(R.id.report_picture) ImageView report_picture;
    @BindView(R.id.report_title_input) EditText report_title_input;
    @BindView(R.id.report_content_input) EditText report_content_input;

    private int imageWidth;
    private int imageHeight;
    private byte[] imageByte;
    private String image_url;
    private String token = "";
    private Bitmap testPhoto;
    private String mCurrentPhotoPath;
    private boolean imageUploaded = false;
    private ProgressDialog progressDialog;
    private int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    public ReportFragment() {
        // Required empty public constructor
    }

    @OnClick(R.id.take_picture_report)
    public void clickImage() {
        dispatchTakePictureIntent();
    }

    @OnClick(R.id.submit_competitor_btn)
    public void sendData() {
        if(!imageUploaded) {
            Toast.makeText(getContext(), "Foto belum berhasil diunggah. Mohon tunggu hingga foto selesai diunggah", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog = new ProgressDialog(getActivity(), R.style.CustomDialog);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Mohon tunggu...");
            progressDialog.show();

            try {
                loginWithOAuthNX("email", "pdd");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("userCred", Context.MODE_PRIVATE);
        //Log.d("sharedPref", sharedPref.getString("jwt", "NoToken"));
        String fullName = sharedPref.getString("full_name", "John Did");
        String email = sharedPref.getString("email", "nomail@reply.com");
        token = sharedPref.getString("access_token", "noToken");

        getActivity().setTitle("Report");
        ButterKnife.bind(this, view);

        report_picture.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageWidth = report_picture.getWidth();
                imageHeight = report_picture.getHeight(); //height is ready
                Log.d("imageWidth", String.valueOf(imageWidth));
                Log.d("imageHeight", String.valueOf(imageHeight));
                report_picture.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

//        try {
//            loginWithOAuthNX("email", "pwd");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        return view;
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
    public void onErrorResponse(VolleyError error) {
        try {
            Log.d("errorResponse", String.valueOf(error));
            Log.d("errorHeader", String.valueOf(error.networkResponse.headers));
            //Log.d("errorResponse", String.valueOf(error.networkResponse.data));
            Log.d("errorResponseData", new String(error.networkResponse.data));
            NxCommonProto.SpringError err = NxCommonProto.SpringError.parseFrom(error.networkResponse.data);
//            Log.d("errorResponse", err.getErrorMap().toString());
            //onLoginFailed();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("errorResponse", "FUCK THIS SHIT!!!!");
        }
    }

    @Override
    public void onResponse(NxFormProto.QrDatum response) {
        String bodyResult = "";
        try {
            Log.d("onResponse", response.toString());
            progressDialog.dismiss();
            //bodyResult = JWTUtils.decoded(String.valueOf(response));

            Toast.makeText(getActivity(), "Laporan berhasil!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            startActivity(intent);
            getActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        //new method for image
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
//            if (data == null) {
//                showError("Failed to open picture!");
//                return;
//            }
//            try {
//                actualImage = FileUtil.from(getContext(), data.getData());
//                competitor_picture.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
//                Log.d("Original file size: ",String.format("Size : %s", getReadableFileSize(actualImage.length())));
//                compressImage();
//            } catch (IOException e) {
//                showError("Failed to read picture data!");
//                e.printStackTrace();
//            }
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
//            competitor_picture.setImageBitmap(imageBitmap);
            setPic();
        }
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
                        "com.android.efforts.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
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

    private void showError(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    private String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private void setPic() {
        // Get the dimensions of the View
        //int targetW = visiblity_picture.getWidth();
        //int targetH = visiblity_picture.getHeight();
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
        testPhoto = BitmapFactory.decodeFile(mCurrentPhotoPath);
        report_picture.setImageBitmap(bitmap);

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

        Toast.makeText(getContext(), "Mengunggah foto. Mohon tunggu...", Toast.LENGTH_LONG).show();
        //upload the image
        uploadImageNX(testPhoto);
    }

    private void uploadImageNX(Bitmap bm) {
        String url = "https://form.nx.tsun.moe/r/api/v1/assets/3211146650761816089";
        Log.d("NXServer", "Entering NX Asset Platform...");
        Log.d("NXServer", url);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Charset", "UTF-8");
        headers.put("Content-Type", "image/jpg");
        headers.put("Accept", "application/x-protobuf,application/json");
        headers.put("Authorization", "Bearer "+token);
        Log.d("token", token);

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        ProtoBufRequest<NxFormProto.OpDatum, NxFormProto.QrAttachment> testReq = new ProtoBufRequest<>(Request.Method.POST, url, bm, NxFormProto.QrAttachment.class, headers, new Response.Listener<NxFormProto.QrAttachment>() {
            @Override
            public void onResponse(NxFormProto.QrAttachment response) {
                String bodyResult = "";
                try {
                    Log.d("onResponse", response.toString());
                    Log.d("code", response.getCode());
                    image_url = "https://form.nx.tsun.moe/r/api/v1/assets/"+response.getCode();
                    //bodyResult = JWTUtils.decoded(String.valueOf(response));
                    imageUploaded = true;
                    Toast.makeText(getContext(), "Foto telah berhasil diunggah", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, this);

        requestQueue.add(testReq);
    }

    private void loginWithOAuthNX(String email, String pwd) throws JSONException, UnsupportedEncodingException {
        //String url = "http://192.168.100.60:8180/r/api/v1/data";
        String url = "https://form.nx.tsun.moe/r/api/v1/forms/3211146650761816089/data";

        //convert both clientID and clientSecret into Base64
        String clientID = "07fbb8e4-8caa-4b91-a7f6-1db581164c9f";
        String clientSecret = "qV60eBr0qGw3tbSvJ2kl86AH";
        String combinedClient = clientID+":"+clientSecret;
        byte[] byteArray = combinedClient.getBytes("UTF-8");
        //convert bytes to Base64
        String base64Result = Base64.encodeToString(byteArray, Base64.DEFAULT);

        Log.d("NXServer", "Entering NX Platform...");
        Log.d("NXServer", url);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Charset", "UTF-8");
        headers.put("Content-Type", "application/x-protobuf");
        headers.put("Accept", "application/x-protobuf,application/json");
        headers.put("Authorization", "Bearer "+token);
        Log.d("token", token);

        //JSONObject insideJsonObj = new JSONObject();
        JSONObject jsonObj = new JSONObject();
//        jsonObj.put("username", email);
//        jsonObj.put("password_digest", pwd);
        jsonObj.put("title", report_title_input.getText().toString());
        jsonObj.put("description", report_content_input.getText().toString());
        jsonObj.put("report_type", "TIDAK NORMAL");
        JSONObject photo = new JSONObject();
        photo.put("path", image_url);
        photo.put("contentType", "image/jpg");
        photo.put("name", "Efforts is love");
        photo.put("description", "POWAA");
        jsonObj.put("photo", photo);

        //JSONObject jsonObj = new JSONObject();
        //jsonObj.put("auth", insideJsonObj);

        //CUSTOM SAMPLE FOR NXFORMPROTOBUF
        //NxFormProto.OpForm testForm = NxFormProto.OpForm.newBuilder()
        NxFormProto.OpDatum testDatum = NxFormProto.OpDatum.newBuilder().setFormId(3211146650761816089L).setContent(jsonObj.toString(2)).build();
        byte[] data = testDatum.toByteArray();
        //NxFormProto.OpTemplate testTemplate = NxFormProto.OpTemplate.newBuilder()

        HashMap<String, String> params = new HashMap<>();
//        params.put("title", "this is title report");
//        params.put("description", "this is description");
//        params.put("report_type", "authorization_report");
//        params.put("photo_url", "photo_url");
//        params.put("content", data);

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        //CustomJSONObjectRequest customJSONReq = new CustomJSONObjectRequest(Request.Method.POST, url, jsonObj, params, headers, this, this);
        ProtoBufRequest<NxFormProto.OpDatum, NxFormProto.QrDatum> testReq = new ProtoBufRequest<>(Request.Method.POST, url, testDatum, NxFormProto.QrDatum.class, headers, this, this);

//            try {
//                //Map<String, String> testH = jsObjRequest.getHeaders();
//                //Log.d("headers",testH.get("Content-Type"));
//                //Log.d("headers", String.valueOf(customJSONReq.getHeaders()));
//                Log.d("content", jsonObj.toString(2));
//            } catch (AuthFailureError authFailureError) {
//                authFailureError.printStackTrace();
//            }

            requestQueue.add(testReq);
    }
}
