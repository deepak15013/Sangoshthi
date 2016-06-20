package uk.ac.openlab.radio.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.drawables.ChecklistItemView;

public class SpreadTheWordActivity extends AppCompatActivity {

    private ChecklistItemView toolbarItemView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spread_the_word);

        toolbarItemView = (ChecklistItemView) findViewById(R.id.toolbar_item);
        assert toolbarItemView != null;
        toolbarItemView.setTitle("Spread The Word");
        toolbarItemView.hideCheckbox(true);


    }
}
