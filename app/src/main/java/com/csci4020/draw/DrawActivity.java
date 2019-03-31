package com.csci4020.draw;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class DrawActivity extends Activity implements RadioGroup.OnCheckedChangeListener
{
	PaintArea paintArea;

    private final static int REQUEST_PHOTO = 100;

    File publicFile = null;
    String fileLocation = null;
    FileOutputStream publicFos;

    AlertDialog stickerAlert;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_draw);

		// Identify the paint area
		paintArea = findViewById(R.id.paintArea);
		paintArea.setDrawingCacheEnabled(true);

		setUpButtonClickListeners();
	}

	private void setUpButtonClickListeners()
	{

		findViewById(R.id.open_file_button).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
                openImage();
			}
		});

		findViewById(R.id.save_file_Button).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
                saveImage();
			}
		});

		findViewById(R.id.send_message_button).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
                emailImage();
			}
		});

		findViewById(R.id.undo_button).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
                if (paintArea != null ){
                    paintArea.undo();
                }
			}
		});

        stickerAlert = setupStickerDialog();

		findViewById(R.id.constraintLayout_current_color).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{

			}
		});

		findViewById(R.id.new_page_button).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
                if (paintArea != null){
                    paintArea.clear();
                }
			}
		});


		findViewById(R.id.color_picker_button).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{

			}
		});

		findViewById(R.id.new_page_button).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{

			}
		});

		// set listeners to Radio Group
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
		radioGroup.setOnCheckedChangeListener(this);

		findViewById(R.id.radioButton_sticker).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
                paintArea.setCurrentTool(PaintArea.STICKER_FEATURE);
			}
		});

		findViewById(R.id.radioButton_frame).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
                paintArea.toggleDrawingFrame();
			}
		});
	}

	/**
	 * Determine which radioButton feature is checked by the user
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId)
	{
        PaintArea paintArea = findViewById(R.id.paintArea);

        switch (checkedId)
		{
			case R.id.radioButton_brush:
				paintArea.setCurrentTool(TOOLS.BRUSH);
				Log.i("TOOL", "SELECTED BRUSH");
				break;
			case R.id.radioButton_line:
				paintArea.setCurrentTool(TOOLS.LINE);
				break;
			case R.id.radioButton_square:
				paintArea.setCurrentTool(TOOLS.RECTANGLE);
				break;
		}
	}

    private void openImage() {
        Intent choosePictureIntent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(choosePictureIntent, REQUEST_PHOTO);
    }

    private void saveImage(){

        this.fileLocation = MediaStore.Images.Media.insertImage(getContentResolver(),paintArea.getDrawingCache(),"title.png","desc123");

        Log.i("File Location", "this.fileLocation = " + this.fileLocation);

        Toast.makeText(getApplicationContext(), "Saved to Gallery", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetWorldReadable")
    public void emailImage(){
        saveImagePublic();
        if (this.publicFile != null) {
            this.publicFile.setReadable(true, false);
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(this.publicFile));
            startActivity(Intent.createChooser(emailIntent, "Select app to send this image."));
        }
    }

    public void saveImagePublic(){
        File publicDir = getPublicAlbumStorageDirectory("Emailed Photos");
        String filename = "myfile.png";
        this.publicFile = new File(publicDir,filename);
        try {
            this.publicFos = new FileOutputStream(publicFile);
            Bitmap bitmapFromView = paintArea.getDrawingCache();
            bitmapFromView.compress(Bitmap.CompressFormat.PNG, 100, this.publicFos);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public File getPublicAlbumStorageDirectory(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("StorageDirectory", "Directory not created");
        }
        return file;
    }


    // MARK: - Dealing with stickers
}
