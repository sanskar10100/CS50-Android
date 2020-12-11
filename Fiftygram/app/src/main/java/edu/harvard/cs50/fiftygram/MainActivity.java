package edu.harvard.cs50.fiftygram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.FileDescriptor;
import java.io.IOException;

import jp.wasabeef.glide.transformations.gpu.ContrastFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.InvertFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.PixelationFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SepiaFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SketchFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.ToonFilterTransformation;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
	private ImageView imageView;
	private Bitmap original;
	private Bitmap toSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
		imageView = findViewById(R.id.image_view);
	}

	/**
	 * Applies the filter to the selected image
	 *
	 * @param filter the filter to be applied. Passed as a transformation object
	 */
	public void apply(Transformation<Bitmap> filter) {
		if (original != null) {
			Glide
					.with(this)
					.asBitmap()
					.load(original)
					.apply(RequestOptions.bitmapTransform(filter))
					.into(new CustomTarget<Bitmap>() {
						@Override
						public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
							imageView.setImageBitmap(resource);
							toSave = resource;
						}

						@Override
						public void onLoadCleared(@Nullable Drawable placeholder) {
							// PASS
						}
					});
		}
	}

	public void applySepia(View view) {
		apply(new SepiaFilterTransformation());
	}

	public void applyToon(View view) {
		apply(new ToonFilterTransformation());
	}

	public void applySketch(View view) {
		apply(new SketchFilterTransformation());
	}

	public void applyPixelation(View view) {
		apply(new PixelationFilterTransformation());
	}

	public void applyInvert(View view) {
		apply(new InvertFilterTransformation());
	}

	public void applyContrast(View view) {
		apply(new ContrastFilterTransformation());
	}

	public void choosePhoto(View view) {
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.setType("image/*");
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK && data != null) {
			try {
				Uri uri = data.getData();
				ParcelFileDescriptor parcelFileDescriptor =
						getContentResolver().openFileDescriptor(uri, "r");
				FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
				original = BitmapFactory.decodeFileDescriptor(fileDescriptor);
				parcelFileDescriptor.close();
				imageView.setImageBitmap(original);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	public void savePhoto(View view) {
		if (toSave != null) {
			ContentResolver contentResolver = getContentResolver();
			MediaStore.Images.Media.insertImage(contentResolver, toSave, "Filtered image", "Filtered image");
			Toast.makeText(getApplicationContext(), "The image has been saved!", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "Select an image and apply a filter first!", Toast.LENGTH_SHORT).show();
		}
	}
}
