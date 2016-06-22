package uk.ac.openlab.radio.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.drawables.ChecklistItemView;

public class ShowResultsActivity extends AppCompatActivity {

    private ChecklistItemView toolbarItemView;

    private Spinner spnNumOfOptions, spnCorrectoption;
    private Button btnCreateResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_results);

        toolbarItemView = (ChecklistItemView) findViewById(R.id.toolbar_item);
        assert toolbarItemView != null;
        toolbarItemView.setTitle("Quiz Results");
        toolbarItemView.hideCheckbox(true);
        toolbarItemView.setIcon(R.drawable.ic_person);

        spnNumOfOptions = (Spinner) findViewById(R.id.spnNumOfOptions);
        spnCorrectoption = (Spinner) findViewById(R.id.spnCorrectOption);
        btnCreateResults = (Button) findViewById(R.id.btn_create_results);

        final String resultsJson = getIntent().getStringExtra("MESSAGE");
        Log.v("dks","resultsJSON: "+resultsJson);

        btnCreateResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String numOfOptionString = String.valueOf(spnNumOfOptions.getSelectedItem().toString());
                Log.v("dks","numOfOption: "+numOfOptionString);
                String correctOptionString = String.valueOf(spnCorrectoption.getSelectedItem().toString());
                Log.v("dks","correctOption: "+correctOptionString);

                int numOfOption, correctOption;
                if(numOfOptionString != null && correctOptionString != null) {
                    numOfOption = Integer.parseInt(numOfOptionString);
                    correctOption = Integer.parseInt(correctOptionString);
                    Log.v("dks","numOfOption: "+numOfOption+"corect: "+correctOption);

                    ArrayList<Integer> values = new ArrayList<>();
                    try {
                        JSONObject jsonObject = new JSONObject(resultsJson);
                        for(int i=1;i<=numOfOption;i++) {
                            int value = Integer.parseInt(jsonObject.optString(String.valueOf(i)));
                            values.add(value);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    BarChart quizChart = (BarChart) findViewById(R.id.chart_quiz_results);

                    assert quizChart != null;
                    quizChart.setVisibility(View.VISIBLE);

                    ArrayList<BarEntry> entries = new ArrayList<>();

                    for(int i=0; i<numOfOption;i++) {
                        entries.add(new BarEntry(values.get(i),i));
                    }

                    BarDataSet dataSet = new BarDataSet(entries, "Num of persons");
                    dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

                    ArrayList<String> labels = new ArrayList<>();
                    for(int i=0;i<numOfOption;i++) {
                        labels.add(String.valueOf(i));
                    }

                    BarData data = new BarData(labels, dataSet);

                    quizChart.setData(data);
                    quizChart.setDescription("Quiz Results");
                    YAxis leftAxis = quizChart.getAxisLeft();
                    YAxis rightAxis = quizChart.getAxisRight();

                    leftAxis.setAxisMinValue(0);
                    rightAxis.setAxisMinValue(0);
                    quizChart.notifyDataSetChanged();
                    quizChart.invalidate();

                }
                else {
                    Toast.makeText(ShowResultsActivity.this, "Please enter number of option and the correct option", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
