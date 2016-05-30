package fr.icodem.autocompleteapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private CityAutoCompleteAdapter adapter;
    //private AutoCompleteTextView acTextView;

    // delayed autocomplete view =>
    // cf http://makovkastar.github.io/blog/2014/04/12/android-autocompletetextview-with-suggestions-from-a-web-service/
    private DelayAutoCompleteTextView acTextView;
    private ProgressBar indicator;

    private City selectedCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new CityAutoCompleteAdapter(this);

        indicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        acTextView = (DelayAutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        acTextView.setAdapter(adapter);
        acTextView.setThreshold(2);
        acTextView.setLoadingIndicator(indicator);

        // ne pas afficher le toString() de City, mais le nom
        // de la ville sélectionnée dans la zone de texte
        acTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City city = (City) parent.getItemAtPosition(position);
                selectedCity = city;
                acTextView.setText(city.getName());
            }
        });

        // forcer si pas d'item sélectionné dans la liste
        acTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (adapter.getCount() > 0) {
                        City city = adapter.getItem(0);
                        selectedCity = city;
                        acTextView.setText(city.getName());
                    } else {
                        acTextView.setText("");
                    }
                }
            }
        });

    }

}
