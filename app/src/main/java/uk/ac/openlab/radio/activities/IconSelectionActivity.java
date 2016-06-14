/*
 * Copyright (c) 2016. Kyle Montague
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.ac.openlab.radio.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import uk.ac.openlab.radio.GlobalUtils;
import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.adapters.IconListAdapter;
import uk.ac.openlab.radio.adapters.RHDTopicIcons;
import uk.ac.openlab.radio.datatypes.Topic;

public class IconSelectionActivity extends AppCompatActivity {

    public static final int CAMERA_IMAGE_CAPTURE = R.integer.camera_image_capture;
    GridView gridView;
    Button finished;

    public static final int REQUEST_CODE = 9968;
    public static final String EXTRA_ICON = "EXTRA.ICON";

    private int selectedIndex=-1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(GlobalUtils.appTheme());
        setContentView(R.layout.activity_icon_selection);


        if (savedInstanceState != null)
            fileName = savedInstanceState.getString("image-file");

        gridView = (GridView)findViewById(R.id.grid);
        finished = (Button)findViewById(R.id.button_finish);

        IconListAdapter adapter = RHDTopicIcons.showTopics(this, false);
        gridView.setAdapter(adapter);
    }


    @Override
    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putString("image-file", fileName);
    }



    String fileName;
    private void takePhoto() {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("my_app", ".jpg");
            fileName = tempFile.getAbsolutePath();
            Uri uri = Uri.fromFile(tempFile);
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(i, CAMERA_IMAGE_CAPTURE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_IMAGE_CAPTURE){
            if(resultCode == RESULT_OK){
                //add this two the grid view after the take photo button
                //show as the selected icon

                if(new File(fileName).exists()) {
                    Toast.makeText(this, "Returned a photo: " + fileName, Toast.LENGTH_SHORT).show();
                }

            }else{
                //no image - how should we handle this. do nothing?
            }
        }
    }

    private void done(){
        if(selectedIndex != -1){
            Topic item = (Topic)gridView.getSelectedItem();
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_ICON,item);
            setResult(Activity.RESULT_OK,resultIntent);
        }else{
            setResult(Activity.RESULT_CANCELED);
        }
        finish();
    }
}
