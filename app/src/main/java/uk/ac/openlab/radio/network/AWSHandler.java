package uk.ac.openlab.radio.network;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;

import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.activities.MainActivity;

/**
 * Created by deepaksood619 on 12/7/16.
 */
public class AWSHandler {

    private static AWSHandler sharedInstance;
    public static AWSHandler shared() {
        if(sharedInstance == null) {
            sharedInstance = new AWSHandler();
        }
        return sharedInstance;
    }

    private Context context;

    private static final String BUCKET_NAME = "sehatkivaani";

    private TransferUtility transferUtility;

    private String fileName;

    public void init(Context context) {
        this.context = context;

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                this.context,
                "us-east-1:abbd2d99-3eca-446c-9a37-0e36d2f883d5", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        // Create an S3 client
        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);

        // Set the region of your S3 bucket
        s3.setRegion(Region.getRegion(Regions.US_EAST_1));

        transferUtility = new TransferUtility(s3, this.context);
    }

    public void storeAwsFile(File fileToUpload, String fileKey) {

        fileName = fileKey;

        TransferObserver observer = transferUtility.upload(
                BUCKET_NAME,      //The bucket to upload to
                fileKey,     //The key for the uploaded object
                fileToUpload         //The file where the data to upload exists
        );

        transferObserverListener(observer);
    }

    public void transferObserverListener(TransferObserver transferObserver){

        transferObserver.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.e("statechange", state+"");
                if(state.toString().equalsIgnoreCase("COMPLETED")) {
                    if(MainActivity.uploadTrailer) {
                        FreeSwitchApi.shared().trailerUpload(new IMessageListener() {
                            @Override
                            public void success() {
                                Toast.makeText(context, R.string.toast_file_upload_success, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void fail() {
                                Toast.makeText(context, R.string.toast_file_upload_failed, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void error() {

                            }

                            @Override
                            public void message(String message) {

                            }
                        }, fileName);
                    }
                    else {
                        FreeSwitchApi.shared().contentUpload(new IMessageListener() {
                            @Override
                            public void success() {
                                Toast.makeText(context, R.string.toast_file_upload_success, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void fail() {
                                Toast.makeText(context, R.string.toast_file_upload_failed, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void error() {

                            }

                            @Override
                            public void message(String message) {

                            }
                        }, fileName);
                    }
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                try{
                    int percentage = (int) (bytesCurrent/bytesTotal * 100);
                    Log.e("percentage",percentage +"");
                } catch (ArithmeticException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("error","error");
            }

        });
    }

}
