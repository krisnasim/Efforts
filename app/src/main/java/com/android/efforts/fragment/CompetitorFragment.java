package com.android.efforts.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.efforts.R;
import com.android.efforts.activity.HomeActivity;
import com.android.efforts.customclass.ProtoBufRequest;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;
import moe.tsun.nx.api.NxFormProto;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

public class CompetitorFragment extends Fragment implements Response.Listener<NxFormProto.QrDatum>, Response.ErrorListener {

    @BindView(R.id.info_competitor_input)
    EditText info_competitor_input;
    @BindView(R.id.competitor_picture)
    ImageView competitor_picture;
    @BindView(R.id.info_store_name_input) EditText info_store_name_input;
    @BindView(R.id.info_brand_name_input) EditText info_brand_name_input;
    @BindView(R.id.info_program_name_input) EditText info_program_name_input;
    @BindView(R.id.start_date_competitor_input) EditText start_date_competitor_input;
    @BindView(R.id.end_date_competitor_input) EditText end_date_competitor_input;
    @BindView(R.id.submit_competitor_btn)
    Button submit_competitor_btn;

    private byte[] imageByte;
    private File actualImage;
    private File compressedImage;
    private Calendar myCalendar;
    private String mCurrentPhotoPath;
    private int imageWidth;
    private int imageHeight;
    private String image_url;
    private String token;
    private Bitmap testPhoto;
    private boolean imageUploaded = false;
    private int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private DatePickerDialog.OnDateSetListener startDateListener;
    private DatePickerDialog.OnDateSetListener endDateListener;
    //private EditText currentDateInput;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPref;

    @OnClick(R.id.submit_competitor_btn)
    public void submitIssue() {
        String compInput = info_competitor_input.getText().toString();
        String storeName = info_store_name_input.getText().toString();
        String storeBrand = info_brand_name_input.getText().toString();
        String progName = info_program_name_input.getText().toString();
        String startDate = start_date_competitor_input.getText().toString();
        String endDate = end_date_competitor_input.getText().toString();

        if (progName.matches("")) {
            Toast.makeText(getActivity(), "Anda belum memasukan nama program!", Toast.LENGTH_SHORT).show();
        } else if(compInput.matches("")) {
            Toast.makeText(getActivity(), "Anda belum memasukan detil program!", Toast.LENGTH_SHORT).show();
        }
//        else if(imageByte == null) {
//            Toast.makeText(getActivity(), "Anda belum mamasukan foto program!", Toast.LENGTH_SHORT).show();
//        }
        else if(storeName.matches("")) {
            Toast.makeText(getActivity(), "Anda belum memasukan nama toko!", Toast.LENGTH_SHORT).show();
        } else if(storeBrand.matches("")) {
            Toast.makeText(getActivity(), "Anda belum memasukan nama merk produk!", Toast.LENGTH_SHORT).show();
        } else if(startDate.matches("")) {
            Toast.makeText(getActivity(), "Anda belum memasukan tanggal mulai program!", Toast.LENGTH_SHORT).show();
        } else if(endDate.matches("")) {
            Toast.makeText(getActivity(), "Anda belum memasukan tanggal akhir program!", Toast.LENGTH_SHORT).show();
        } else if(!imageUploaded) {
            Toast.makeText(getActivity(), "Foto belum berhasil diunggah. Mohon tunggu hingga foto selesai diunggah", Toast.LENGTH_SHORT).show();
        } else {
//            final SharedPreferences sharedPref = getActivity().getSharedPreferences("userCred", Context.MODE_PRIVATE);
//            token  = sharedPref.getString("access_token", "NoToken");
            Log.d("SharedPref", sharedPref.getString("access_token", "NoToken"));
            String token = sharedPref.getString("access_token", "empty token");

            submit_competitor_btn.setEnabled(false);
            progressDialog = new ProgressDialog(getActivity(), R.style.CustomDialog);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Mohon tunggu...");
            progressDialog.show();

            //String url = getString(R.string.create_issue);
            String url = "https://form.nx.tsun.moe/r/api/v1/forms/3211147815576659998/data";

            //set headers
            Map<String, String> headers = new HashMap<>();
            headers.put("Charset", "UTF-8");
            headers.put("Content-Type", "application/x-protobuf");
            headers.put("Accept", "application/x-protobuf,application/json");
            headers.put("Authorization", "Bearer "+token);

            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("title", "this is competitor report");
                jsonObj.put("description", compInput);
                jsonObj.put("store_name", storeName);
                jsonObj.put("brand_name", storeBrand);
                jsonObj.put("program_name", progName);
                jsonObj.put("campaign_start", startDate);
                jsonObj.put("campaign_end", endDate);
                JSONObject photo = new JSONObject();
                photo.put("path", image_url);
                photo.put("contentType", "image/jpg");
                photo.put("name", "K-ON_is_love");
                photo.put("description", "kawaii");
                jsonObj.put("photo", photo);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            NxFormProto.OpDatum testDatum = null;
            try {
                testDatum = NxFormProto.OpDatum.newBuilder().setFormId(3211147815576659998L).setContent(jsonObj.toString(2)).build();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            ProtoBufRequest<NxFormProto.OpDatum, NxFormProto.QrDatum> testReq = new ProtoBufRequest<>(Request.Method.POST, url, testDatum, NxFormProto.QrDatum.class, headers, this, this);
            requestQueue.add(testReq);
//            VolleyMultipartRequest newReq = new VolleyMultipartRequest(url, headers, new Response.Listener<NetworkResponse>() {
//                @Override
//                public void onResponse(NetworkResponse response) {
//                    String resultResponse = new String(response.data);
//                    try {
//                        progressDialog.dismiss();
//                        submit_competitor_btn.setEnabled(true);
//                        JSONObject result = new JSONObject(resultResponse);
//                        Log.d("JSONResult", "result: " + result.toString(2));
//                        Toast.makeText(getActivity(), "Pencatatan kompetitor berhasil!", Toast.LENGTH_SHORT).show();
//                        //change fragment
//                        //change fragment
//                        String level = sharedPref.getString("level", "NoLevel");
//                        if(level.equals("sales_representative") || level.equals("area_manager") || level.equals("promoter") || level.equals("admin")) {
//                            //HomeActivity act = (HomeActivity) getActivity();
//                            //act.changeFragment(new HomeFragment());
//                        } else if(level.equals("merchandiser")) {
//                            //HomeMDSActivity act = (HomeMDSActivity) getActivity();
//                            //act.changeFragment(new HomeMDSFragment());
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, this) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("remark", info_competitor_input.getText().toString());
//                    params.put("store_name", info_store_name_input.getText().toString());
//                    params.put("brand_name", info_brand_name_input.getText().toString());
//                    params.put("program_name", info_program_name_input.getText().toString());
//                    params.put("campaign_start", start_date_competitor_input.getText().toString());
//                    params.put("campaign_end", end_date_competitor_input.getText().toString());
//                    params.put("store_id", sharedPref.getString("store_id", "NoID"));
//
//                    Log.d("parameters", info_competitor_input.getText().toString());
//                    Log.d("parameters", info_store_name_input.getText().toString());
//                    Log.d("parameters", info_brand_name_input.getText().toString());
//                    Log.d("parameters", info_program_name_input.getText().toString());
//                    Log.d("parameters", start_date_competitor_input.getText().toString());
//                    Log.d("parameters", end_date_competitor_input.getText().toString());
//                    Log.d("parameters", sharedPref.getString("store_id", "NoID"));
//                    return params;
//                }
//
//                @Override
//                protected Map<String, DataPart> getByteData() throws AuthFailureError {
//                    Map<String, DataPart> params = new HashMap<>();
//                    params.put("photo_name", new DataPart("comp.jpg", imageByte, "image/jpeg"));
//
//                    return params;
//                }
//            };
//            VolleySingleton.getInstance(getContext()).addToRequestQueue(newReq);
        }
    }

    @OnClick(R.id.pick_image_competitor_button)
    public void pickImageCompetitor() {
//        if(isStoragePermissionGranted()){
//            doPickImage();
//        }

        //testing the camera to launch
        dispatchTakePictureIntent();
    }

    @OnClick(R.id.start_date_competitor_input)
    public void pickStartDate() {
        new DatePickerDialog(getActivity(), startDateListener,
                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        updateDateLabel(start_date_competitor_input);
    }

    @OnClick(R.id.end_date_competitor_input)
    public void pickEndDate() {
        new DatePickerDialog(getActivity(), endDateListener,
                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        updateDateLabel(end_date_competitor_input);
    }

    public CompetitorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create calendar
        myCalendar = Calendar.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_competitor, container, false);
        ButterKnife.bind(this, view);
        sharedPref = getActivity().getSharedPreferences("userCred", Context.MODE_PRIVATE);
        token  = sharedPref.getString("access_token", "NoToken");
        //currentDateInput = (EditText) view.findViewById(R.id.currentDateCompInput);

        //setting up editText for Date not editable
        //currentDateInput.setFocusable(false);
        //currentDateInput.setClickable(true);
        start_date_competitor_input.setFocusable(false);
        start_date_competitor_input.setClickable(false);

        end_date_competitor_input.setFocusable(false);
        end_date_competitor_input.setClickable(false);

        competitor_picture.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageWidth = competitor_picture.getWidth();
                imageHeight = competitor_picture.getHeight(); //height is ready
                Log.d("imageWidth", String.valueOf(imageWidth));
                Log.d("imageHeight", String.valueOf(imageHeight));
                competitor_picture.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

//        currentDateInput.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new DatePickerDialog(getActivity(), dateListener,
//                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//                updateDateLabel();
//            }
//        });

        //set listener for date
        initiateDateListeners();
        //set title
        getActivity().setTitle(R.string.title_competitor_fragment);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.d("image picker","Permission: "+permissions[0]+ "was "+grantResults[0]);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        //new method for image
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data == null) {
                showError("Failed to open picture!");
                return;
            }
            try {
                actualImage = FileUtil.from(getContext(), data.getData());
                competitor_picture.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
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
//            competitor_picture.setImageBitmap(imageBitmap);
            setPic();
        }
    }

    @Override
    public void onResponse(NxFormProto.QrDatum response) {
        String bodyResult = "";
        progressDialog.dismiss();
        try {
            Log.d("onResponse", response.toString());
            //bodyResult = JWTUtils.decoded(String.valueOf(response));

            Toast.makeText(getActivity(), "Laporan kompetitor berhasil!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            startActivity(intent);
            getActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progressDialog.dismiss();
        Toast.makeText(getActivity(), "Belum berhasil mengirimkan data ke server!", Toast.LENGTH_SHORT).show();
        submit_competitor_btn.setEnabled(true);
        Log.d("ErrorResponse", "Error response: "+error.getMessage());
        Log.d("ErrorResponse", "Error response: "+error.getLocalizedMessage());
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
                    imageUploaded = true;
                    //bodyResult = JWTUtils.decoded(String.valueOf(response));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, this);

        requestQueue.add(testReq);
    }

    private void initiateDateListeners() {
        startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel(start_date_competitor_input);
            }
        };

        endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel(end_date_competitor_input);
            }
        };
    }

    private void updateDateLabel(EditText obj) {
        //String myFormat = "dd/MM/yy"; //In which you need put here
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        obj.setText(sdf.format(myCalendar.getTime()));
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

    public void doPickImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
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

    private void compressImage() {
        if (actualImage == null) {
            showError("Please choose an image!");
        } else {
            // Compress image using RxJava in background thread
            Compressor.getDefault(getContext())
                    .compressToFileAsObservable(actualImage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<File>() {
                        @Override
                        public void call(File file) {
                            compressedImage = file;
                            Log.d("Compressor", "Compressed image save in " + compressedImage.getPath());
                            Log.d("Compressor", String.format("Size : %s", getReadableFileSize(compressedImage.length())));
                            //get the file converted to image byte array
                            if(compressedImage != null) {
                                //imageByte = convertFileToByteArray(compressedImage);
                                //no compression, so I pass instead still the raw image
                                imageByte = convertFileToByteArray(actualImage);
                            }
                            //setCompressedImage();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            showError(throwable.getMessage());
                        }
                    });
        }
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
        competitor_picture.setImageBitmap(bitmap);

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

        //upload the image
        uploadImageNX(testPhoto);
    }
}
