package eu.hydrologis.geopaparazzi.maps;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import eu.geopaparazzi.library.database.GPLog;
import eu.geopaparazzi.library.util.LibraryConstants;
import eu.hydrologis.geodroid.R;
import eu.hydrologis.geopaparazzi.chart.ProfileChartActivity;
import eu.hydrologis.geopaparazzi.database.DaoGpsLog;
import eu.hydrologis.geopaparazzi.util.Constants;

/**
 * Data properties activity.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class GpsDataPropertiesActivity extends Activity {
    private static List<String> colorList;
    private static List<String> widthsList;
    private LogMapItem item;

    // properties
    private String newText;
    private float newWidth;
    private String newColor;

    public void onCreate( Bundle icicle ) {
        super.onCreate(icicle);

        setContentView(R.layout.gpslog_properties);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getResourcesAndColors();

        Bundle extras = getIntent().getExtras();
        Object object = extras.get(Constants.PREFS_KEY_GPSLOG4PROPERTIES);
        if (object instanceof LogMapItem) {
            item = (LogMapItem) object;

            final TextView startTimeTextView = (TextView) findViewById(R.id.starttime_label);
            String startTime = item.getStartTime();
            String startText = startTimeTextView.getText().toString();
            startTimeTextView.setText(startText + startTime);
            final TextView endTimeTextView = (TextView) findViewById(R.id.endtime_label);
            String endTime = item.getEndTime();
            String endText = endTimeTextView.getText().toString();
            endTimeTextView.setText(endText + endTime);

            final EditText lognameTextView = (EditText) findViewById(R.id.gpslogname);
            final Spinner colorView = (Spinner) findViewById(R.id.color_spinner);
            final Spinner widthView = (Spinner) findViewById(R.id.widthText);

            lognameTextView.setText(item.getName());
            newText = item.getName();
            lognameTextView.addTextChangedListener(new TextWatcher(){

                public void onTextChanged( CharSequence s, int start, int before, int count ) {
                    newText = lognameTextView.getText().toString();
                }
                public void beforeTextChanged( CharSequence s, int start, int count, int after ) {
                    // ignore
                }
                public void afterTextChanged( Editable s ) {
                    // ignore
                }
            });

            newWidth = item.getWidth();
            ArrayAdapter< ? > widthSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_widths,
                    android.R.layout.simple_spinner_item);
            widthSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            widthView.setAdapter(widthSpinnerAdapter);
            int widthIndex = widthsList.indexOf(String.valueOf((int) item.getWidth()));
            widthView.setSelection(widthIndex);
            widthView.setOnItemSelectedListener(new OnItemSelectedListener(){
                public void onItemSelected( AdapterView< ? > arg0, View arg1, int arg2, long arg3 ) {
                    Object selectedItem = widthView.getSelectedItem();
                    newWidth = Float.parseFloat(selectedItem.toString());
                }
                public void onNothingSelected( AdapterView< ? > arg0 ) {
                    // ignore
                }
            });

            // color box
            newColor = item.getColor();
            ArrayAdapter< ? > colorSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_colornames,
                    android.R.layout.simple_spinner_item);
            colorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            colorView.setAdapter(colorSpinnerAdapter);
            int colorIndex = colorList.indexOf(item.getColor());
            colorView.setSelection(colorIndex);
            colorView.setOnItemSelectedListener(new OnItemSelectedListener(){

                public void onItemSelected( AdapterView< ? > arg0, View arg1, int arg2, long arg3 ) {
                    Object selectedItem = colorView.getSelectedItem();
                    newColor = selectedItem.toString();
                }
                public void onNothingSelected( AdapterView< ? > arg0 ) {
                    // ignore
                }
            });

            final Button chartButton = (Button) findViewById(R.id.gpslog_chart);
            chartButton.setOnClickListener(new Button.OnClickListener(){
                public void onClick( View v ) {
                    Intent intent = new Intent(GpsDataPropertiesActivity.this, ProfileChartActivity.class);
                    intent.putExtra(Constants.ID, item.getId());
                    startActivity(intent);
                }
            });
            final Button zoomToStartButton = (Button) findViewById(R.id.gpslog_zoom_start);
            zoomToStartButton.setOnClickListener(new Button.OnClickListener(){
                public void onClick( View v ) {
                    try {
                        double[] firstPoint = DaoGpsLog.getGpslogFirstPoint(item.getId());
                        if (firstPoint != null) {
                            Intent intent = getIntent();
                            intent.putExtra(LibraryConstants.LATITUDE, firstPoint[1]);
                            intent.putExtra(LibraryConstants.LONGITUDE, firstPoint[0]);
                            setResult(Activity.RESULT_OK, intent);
                        }
                    } catch (IOException e) {
                        GPLog.error(this, e.getLocalizedMessage(), e);
                        e.printStackTrace();
                    }
                    finish();
                }
            });
            final Button zoomToEndButton = (Button) findViewById(R.id.gpslog_zoom_end);
            zoomToEndButton.setOnClickListener(new Button.OnClickListener(){
                public void onClick( View v ) {
                    try {
                        double[] firstPoint = DaoGpsLog.getGpslogLastPoint(item.getId());
                        if (firstPoint != null) {
                            Intent intent = getIntent();
                            intent.putExtra(LibraryConstants.LATITUDE, firstPoint[1]);
                            intent.putExtra(LibraryConstants.LONGITUDE, firstPoint[0]);
                            setResult(Activity.RESULT_OK, intent);
                        }
                        finish();
                    } catch (IOException e) {
                        GPLog.error(this, e.getLocalizedMessage(), e);
                        e.printStackTrace();
                    }
                }
            });

            final Button deleteButton = (Button) findViewById(R.id.gpslog_delete);
            deleteButton.setOnClickListener(new Button.OnClickListener(){
                public void onClick( View v ) {
                    try {
                        long id = item.getId();
                        new DaoGpsLog().deleteGpslog(GpsDataPropertiesActivity.this, id);
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void finish() {
        updateWithNewValues();
        super.finish();
    }

    private void updateWithNewValues() {
        try {
            DaoGpsLog.updateLogProperties(item.getId(), newColor, newWidth, item.isVisible(), newText);
        } catch (IOException e) {
            GPLog.error(this, e.getLocalizedMessage(), e);
            e.printStackTrace();
        }
    }

    private void getResourcesAndColors() {
        if (colorList == null) {
            String[] colorArray = getResources().getStringArray(R.array.array_colornames);
            colorList = Arrays.asList(colorArray);
            String[] widthsArray = getResources().getStringArray(R.array.array_widths);
            widthsList = Arrays.asList(widthsArray);
        }

    }

}
