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
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;

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
        toolbarItemView.setTitle(getString(R.string.show_results_title));
        toolbarItemView.setIcon(R.drawable.ic_person);

        spnNumOfOptions = (Spinner) findViewById(R.id.spnNumOfOptions);
        spnCorrectoption = (Spinner) findViewById(R.id.spnCorrectOption);
        btnCreateResults = (Button) findViewById(R.id.btn_create_results);

        final String resultsJson = getIntent().getStringExtra("MESSAGE");
        final String quizId = getIntent().getStringExtra("QUIZ_ID");

        btnCreateResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String numOfOptionString = String.valueOf(spnNumOfOptions.getSelectedItem().toString());
                String correctOptionString = String.valueOf(spnCorrectoption.getSelectedItem().toString());

                int numOfOption, correctOption;
                if(numOfOptionString != null && correctOptionString != null) {
                    numOfOption = Integer.parseInt(numOfOptionString);
                    correctOption = Integer.parseInt(correctOptionString);
                    Log.v("dks","numOfOption: "+numOfOption+"correct: "+correctOption);

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

                    BarDataSet dataSet = new BarDataSet(entries, getResources().getString(R.string.result_x_axis));
                    dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

                    ArrayList<String> labels = new ArrayList<>();
                    for(int i=0;i<numOfOption;i++) {
                        labels.add(String.valueOf(i));
                    }

                    BarData data = new BarData(labels, dataSet);

                    quizChart.setData(data);
                    quizChart.setDescription("");
                    YAxis leftAxis = quizChart.getAxisLeft();
                    YAxis rightAxis = quizChart.getAxisRight();

                    leftAxis.setAxisMinValue(0);
                    rightAxis.setAxisMinValue(0);
                    leftAxis.setGranularity(1);
                    rightAxis.setGranularity(1);
                    quizChart.notifyDataSetChanged();
                    quizChart.invalidate();

                    FreeSwitchApi.shared().quizDetails(new IMessageListener() {
                        @Override
                        public void success() {

                        }

                        @Override
                        public void fail() {

                        }

                        @Override
                        public void error() {

                        }

                        @Override
                        public void message(String message) {

                        }
                    }, quizId, numOfOptionString, correctOptionString);

                }
                else {
                    Toast.makeText(ShowResultsActivity.this, getString(R.string.toast_quiz_results_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
