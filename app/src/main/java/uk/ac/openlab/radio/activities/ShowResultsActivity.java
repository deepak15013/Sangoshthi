package uk.ac.openlab.radio.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.drawables.ChecklistItemView;

public class ShowResultsActivity extends AppCompatActivity {

    private ChecklistItemView toolbarItemView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_results);

        toolbarItemView = (ChecklistItemView) findViewById(R.id.toolbar_item);
        assert toolbarItemView != null;
        toolbarItemView.setTitle("Quiz Results");
        toolbarItemView.hideCheckbox(true);
        toolbarItemView.setIcon(R.drawable.ic_person);

        String resultsJson = getIntent().getStringExtra("MESSAGE");
        Log.v("dks","resultsJSON: "+resultsJson);



    }
}
