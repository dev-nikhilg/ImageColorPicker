package com.codinginjava.www.imagecolorpicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = MainActivity.class.getSimpleName();
    private final int PICK_PHOTO = 100;

    private LinearLayout linearLayout;
    private ImageView imageView;
    private Toolbar toolbar;
    private GridView gridView;
    private TextView noImageSelectedText;
    private FloatingActionButton fab;

    private GridAdapter gridAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialise layout
        initLayout();

        // set click listeners
        fab.setOnClickListener(this);
    }

    private void initLayout() {
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        imageView = (ImageView)findViewById(R.id.imageView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        gridView = (GridView) findViewById(R.id.gridView);
        noImageSelectedText = (TextView) findViewById(R.id.noImageSelectedText);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Intent pickPhoto = new Intent(Intent.ACTION_PICK);
                pickPhoto.setType("image/*");
                startActivityForResult(pickPhoto, PICK_PHOTO);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_PHOTO:
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "Picked a photo.");
                    Uri selectedImage = data.getData();
                    createPalette(selectedImage);
                }
                break;
        }
    }

    public void createPalette(Uri imageUri) {
        // Load image into imageview using picasso
        Picasso.with(this).load(imageUri).into(imageView);

        // hide the noimageselectedtextview
        noImageSelectedText.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);

        // Do this async on activity
        try {
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    HashMap<String, Integer> hashMap = processPalette(palette);
                    Object[] entries = hashMap.entrySet().toArray();
                    gridAdapter = new GridAdapter(getApplicationContext(), entries);
                    gridView.setAdapter(gridAdapter);
                }
            });
        } catch (Exception ex) {
            Log.e("MainActivity", "error in creating palette");
        }
    }

    private HashMap<String,Integer> processPalette (Palette p) {
        HashMap<String, Integer> map = new HashMap<>();

        if (p.getVibrantSwatch() != null)
            map.put("Vibrant", p.getVibrantSwatch().getRgb());
        if (p.getDarkVibrantSwatch() != null)
            map.put("DarkVibrant", p.getDarkVibrantSwatch().getRgb());
        if (p.getLightVibrantSwatch() != null)
            map.put("LightVibrant", p.getLightVibrantSwatch().getRgb());

        if (p.getMutedSwatch() != null)
            map.put("Muted", p.getMutedSwatch().getRgb());
        if (p.getDarkMutedSwatch() != null)
            map.put("DarkMuted", p.getDarkMutedSwatch().getRgb());
        if (p.getLightMutedSwatch() != null)
            map.put("LightMuted", p.getLightMutedSwatch().getRgb());

        return map;
    }
}
