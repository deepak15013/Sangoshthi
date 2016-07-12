package uk.ac.openlab.radio.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import uk.ac.openlab.radio.GlobalUtils;
import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.drawables.ChecklistItemView;
import uk.ac.openlab.radio.network.AWSHandler;
import uk.ac.openlab.radio.utilities.FileBrowserActivity;

public class UploadFilesActivity extends AppCompatActivity {

    private final int REQUEST_CODE_PICK_FILE = 2;

    private ChecklistItemView toolbarItemView;

    private Button btnBrowse;
    private TextView tvFileLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_files);

        toolbarItemView = (ChecklistItemView) findViewById(R.id.toolbar_item);
        assert toolbarItemView != null;
        toolbarItemView.setTitle(getString(R.string.create_trailer_title));
        toolbarItemView.hideCheckbox(true);
        toolbarItemView.setIcon(R.drawable.ic_person);

        btnBrowse = (Button) findViewById(R.id.btn_browse);
        tvFileLocation = (TextView) findViewById(R.id.tv_file_location);

        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fileExploreIntent = new Intent(FileBrowserActivity.INTENT_ACTION_SELECT_FILE,
                        null,
                        getApplicationContext(),
                        FileBrowserActivity.class
                );

                startActivityForResult(
                        fileExploreIntent,
                        REQUEST_CODE_PICK_FILE
                );
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_PICK_FILE) {
            if(resultCode == RESULT_OK) {
                String newFile = data.getStringExtra(
                        FileBrowserActivity.returnFileParameter);

                tvFileLocation.setText(newFile);

                File fileToUpload = new File(newFile);
                if(fileToUpload.exists()) {
                    String fileKey = GlobalUtils.shared().getStudioId()+"/trailer/"+fileToUpload.getName();

                    AWSHandler.shared().storeAwsFile(fileToUpload, fileKey);
                }
                else {
                    Log.v("dks","file not found");
                }


                Toast.makeText(
                        this,
                        "Received FILE path from file browser:\n"+newFile,
                        Toast.LENGTH_LONG).show();

            } else {//if(resultCode == this.RESULT_OK) {
                Toast.makeText(
                        this,
                        "Received NO result from file browser",
                        Toast.LENGTH_LONG).show();
            }//END } else {//if(resultCode == this.RESULT_OK) {
        }//if (requestCode == REQUEST_CODE_PICK_FILE) {

        super.onActivityResult(requestCode, resultCode, data);
    }
}
