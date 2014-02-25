package eu.hydrologis.geopaparazzi.preferences;

import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import eu.geopaparazzi.library.util.FileUtilities;
import eu.hydrologis.geodroid.R;

/**
 * A custom sdcard path chooser.
 * 
 * @author root
 *
 */
public class CustomSdcardPathPreference extends DialogPreference {
    private Context context;
    private EditText editView;
    private String customPath = ""; //$NON-NLS-1$
    private Spinner guessedPathsSpinner;
    private List<String> guessedSdcardsList;
    /**
     * @param ctxt  the context to use.
     * @param attrs attributes.
     */
    public CustomSdcardPathPreference( Context ctxt, AttributeSet attrs ) {
        super(ctxt, attrs);
        this.context = ctxt;
        setPositiveButtonText(ctxt.getString(android.R.string.ok));
        setNegativeButtonText(ctxt.getString(android.R.string.cancel));
    }

    @Override
    protected View onCreateDialogView() {
        LinearLayout mainLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);
        mainLayout.setLayoutParams(layoutParams);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(context);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        textView.setPadding(2, 2, 2, 2);
        textView.setText(R.string.set_path_manually);
        mainLayout.addView(textView);

        editView = new EditText(context);
        editView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        editView.setPadding(15, 5, 15, 5);
        editView.setText(customPath);
        mainLayout.addView(editView);

        TextView comboLabelView = new TextView(context);
        comboLabelView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        comboLabelView.setPadding(2, 2, 2, 2);
        comboLabelView.setText(R.string.choose_from_suggested);
        mainLayout.addView(comboLabelView);

        guessedPathsSpinner = new Spinner(context);
        guessedPathsSpinner.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        guessedPathsSpinner.setPadding(15, 5, 15, 5);
        mainLayout.addView(guessedPathsSpinner);

        guessedSdcardsList = FileUtilities.getPossibleSdcardsList();
        guessedSdcardsList.add(0, ""); //$NON-NLS-1$

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, guessedSdcardsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        guessedPathsSpinner.setAdapter(adapter);
        if (customPath != null) {
            for( int i = 0; i < guessedSdcardsList.size(); i++ ) {
                if (guessedSdcardsList.get(i).equals(customPath.trim())) {
                    guessedPathsSpinner.setSelection(i);
                    break;
                }
            }
        }

        return mainLayout;
    }
    @Override
    protected void onBindDialogView( View v ) {
        super.onBindDialogView(v);

        editView.setText(customPath);
        if (customPath != null) {
            for( int i = 0; i < guessedSdcardsList.size(); i++ ) {
                if (guessedSdcardsList.get(i).equals(customPath.trim())) {
                    guessedPathsSpinner.setSelection(i);
                    break;
                }
            }
        }
    }

    @Override
    protected void onDialogClosed( boolean positiveResult ) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            customPath = editView.getText().toString().trim();
            if (customPath.length() == 0) {
                // try combo
                customPath = guessedPathsSpinner.getSelectedItem().toString();
            }
            if (callChangeListener(customPath)) {
                persistString(customPath);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue( TypedArray a, int index ) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue( boolean restoreValue, Object defaultValue ) {

        if (restoreValue) {
            if (defaultValue == null) {
                customPath = getPersistedString(""); //$NON-NLS-1$
            } else {
                customPath = getPersistedString(defaultValue.toString());
            }
        } else {
            customPath = defaultValue.toString();
        }
    }
}