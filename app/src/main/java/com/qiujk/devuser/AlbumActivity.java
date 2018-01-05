package com.qiujk.devuser;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

public class AlbumActivity extends Activity {
    private final String tag ="AlbumActivity";
    public static final int TAKE_VIDEO = 0;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    public static final int CHOOSE_VIDEO = 3;
    private Button mTakeVideo;
    private Button mTakePhoto;
    private Button mChoosePhoto;
    private Button mChooseVideo;
    //private ImageView imageView;
    private VideoView videoView;
    private LinearLayout linerLayout;
    private Uri imageUri;
    private Uri vedioUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        for (int count = 0; count < 10; count++) {
            Log.i(tag,"value:"+count);
        }
        linerLayout = (LinearLayout)findViewById(R.id.albumliner);
        mTakeVideo = (Button)findViewById(R.id.btn_take_video);
        mTakePhoto = (Button)findViewById(R.id.btn_take_photo);
        mChoosePhoto = (Button)findViewById(R.id.choose_from_album);
        mChooseVideo = (Button)findViewById(R.id.choose_from_video);
        //imageView = (ImageView)findViewById(R.id.iv_picture);
        videoView = (VideoView)findViewById(R.id.videoViewPlayer);
        videoView.setVisibility(View.GONE);
        mTakeVideo.setOnClickListener(click);
        mTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建file对象，用于存储拍照后的图片；
                File outputImage = new File(getExternalCacheDir(),"testimage.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(tag,e.getMessage().toString());
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(AlbumActivity.this,
                            "com.qiujk.devuser.fileprovider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                //启动相机程序
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });
        mChooseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                if (ContextCompat.checkSelfPermission(AlbumActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AlbumActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {
                    openAlbumAndVideo(CHOOSE_VIDEO);
                }
                }catch (Exception ex){
                    Log.e(tag,"setOnClickListener error:"+ex.getMessage());
                }
            }
        });
        mChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AlbumActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AlbumActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbumAndVideo(CHOOSE_PHOTO);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbumAndVideo(CHOOSE_PHOTO);
                } else {
                    Toast.makeText(this, "you denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbumAndVideo(CHOOSE_VIDEO);
                } else {
                    Toast.makeText(this, "you denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private View.OnClickListener click = new View.OnClickListener() {
     @Override
        public void onClick(View v) {
         try {
             Intent intent = new Intent();
             intent.setAction("android.media.action.VIDEO_CAPTURE");
             intent.addCategory("android.intent.category.DEFAULT");
             File file = new File(getExternalCacheDir(),"testvideo.3gp");
             if (file.exists()) {
                 file.delete();
             }
             vedioUri = Uri.fromFile(file);
             intent.putExtra(MediaStore.EXTRA_OUTPUT, vedioUri);
             startActivityForResult(intent, TAKE_VIDEO);
         }
         catch (Exception ex){
             Log.e(tag,"mTakeVideo: "+ex.getMessage().toString());
         }
        }
     };

    //打开相册
    private void openAlbumAndVideo(int type) {

        if(type==CHOOSE_PHOTO) {
            Intent intent1 = new Intent();
            intent1.setType("image/*");
            intent1.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent1, CHOOSE_PHOTO);
        }else if(type==CHOOSE_VIDEO){

            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, CHOOSE_VIDEO);

//            Intent intent2 = new Intent("android.intent.action.GET_CONTENT");
//            intent2.setType("video/*");
//            intent2.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(intent2, CHOOSE_VIDEO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_VIDEO:
                if (resultCode == RESULT_OK)
                {
                    try {
                        if (vedioUri!=null){
                            Toast.makeText(this, vedioUri.getPath(), Toast.LENGTH_SHORT).show();
//                            imageView.setVisibility(View.INVISIBLE);
                            videoView.setVisibility(View.VISIBLE);
                            String videoString = vedioUri.getPath();
                            Uri uri = Uri.parse(videoString);
                            videoView.setMediaController(new MediaController(this));
                            videoView.setVideoURI(uri);
                            videoView.requestFocus();
                            videoView.start();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
//                        imageView.setVisibility(View.VISIBLE);
                        videoView.setVisibility(View.GONE);
//                        if (FileExists(imageUri.getPath()))
//                            Glide.with(this).load(imageUri.getPath()).into(imageView);
                        Bitmap bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        linerLayout.setBackground(new BitmapDrawable(null, bm));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //imageView.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= 19) {  //4.4及以上的系统使用这个方法处理图片；
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);  //4.4及以下的系统使用这个方法处理图片
                    }
                }
                break;
            case CHOOSE_VIDEO:
                if (resultCode == RESULT_OK) {
                    //imageView.setVisibility(View.INVISIBLE);
                    videoView.setVisibility(View.VISIBLE);
                    Uri uri = data.getData();
                    videoView.setMediaController(new MediaController(this));
                    videoView.setVideoURI(uri);
                    videoView.requestFocus();
                    videoView.start();
                }
                break;
            default:
                break;
        }
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getPath(uri, null,CHOOSE_PHOTO);
        displayImage(imagePath);
    }


    private String getPath(Uri uri, String selection, int type) {
        String path = null;
        //通过Uri和selection来获取真实的路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                if (type==CHOOSE_PHOTO) {
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                }else if (type==CHOOSE_VIDEO){
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                }
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            //Glide.with(this).load(imagePath).into(imageView);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            linerLayout.setBackground(new BitmapDrawable(null, bitmap));
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 4.4及以上的系统使用这个方法处理图片
     *
     * @param data
     */
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果document类型的Uri,则通过document来处理
            String docID = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docID.split(":")[1];     //解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;

                imagePath = getPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection,CHOOSE_PHOTO);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/piblic_downloads"), Long.valueOf(docID));

                imagePath = getPath(contentUri, null,CHOOSE_PHOTO);

            }

        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，则使用普通方式使用
            imagePath = getPath(uri, null,CHOOSE_PHOTO);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的uri，直接获取路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }
    //判断文件是否存在
    public boolean FileExists(String strFile)
    {
        try
        {
            File f=new File(strFile);
            if(!f.exists())
            {
                return false;
            }

        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }
}
