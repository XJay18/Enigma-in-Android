package com.android.xjay.enigma;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import enigma.EnigmaException;
import enigma.Simulator;

/**
 * Main Activity for Enigma in Android.
 */
public class MainActivity extends AppCompatActivity implements
        View.OnClickListener {

    /**
     * The available rotors for the first slots.
     */
    private String[] setA = {"B", "C"};

    /**
     * The available rotors for the second slots.
     */
    private String[] setB = {"Beta", "Gamma"};

    /**
     * The available rotors for the third slots.
     */
    private String[] setC = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII"};

    /**
     * The available rotors for the fourth slots.
     */
    private String[] setD = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII"};

    /**
     * The available rotors for the fifth slots.
     */
    private String[] setE = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII"};

    /**
     * The initial settings and the plugboard.
     */
    private EditText et_setting;

    /**
     * The message to be encoded.
     */
    private EditText et_encoding;

    /**
     * The encoded message.
     */
    private TextView tv_encoded;

    /**
     * Get the five rotors.
     */
    private String[] rotors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rotors = new String[5];

        Spinner spA = findViewById(R.id.sp_spa);
        Spinner spB = findViewById(R.id.sp_spb);
        Spinner spC = findViewById(R.id.sp_spc);
        Spinner spD = findViewById(R.id.sp_spd);
        Spinner spE = findViewById(R.id.sp_spe);

        et_setting = findViewById(R.id.et_set);
        et_encoding = findViewById(R.id.et_encoding);
        tv_encoded = findViewById(R.id.tv_encoded);

        setAdapter(spA, setA, 0);
        setAdapter(spB, setB, 1);
        setAdapter(spC, setC, 2);
        setAdapter(spD, setD, 3);
        setAdapter(spE, setE, 4);

        et_setting.addTextChangedListener(new JumpTextWatcher(
                this, et_setting, et_encoding));

        final SpannableString s = new SpannableString(getText(R.string.github));
        Linkify.addLinks(s, Linkify.WEB_URLS);
        ((TextView) this.findViewById(R.id.github)).setText(s);
        ((TextView) this.findViewById(R.id.github)).
                setMovementMethod(LinkMovementMethod.getInstance());

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_encode) {
            try {
                StringBuffer sbSettings = new StringBuffer();
                sbSettings.append("* ");
                for (String s : rotors) {
                    sbSettings.append(s);
                    sbSettings.append(" ");
                }
                if (et_setting.getText().toString().matches("\\s*")) {
                    sbSettings.append("AAAA AAAA");
                } else {
                    sbSettings.append(et_setting.getText().toString());
                }

                String encodes = et_encoding.getText().toString().toUpperCase();
                Simulator simulator = new Simulator(
                        new String(sbSettings) + "\n" + encodes);
                simulator.process();
                tv_encoded.setText(simulator.getEncode());
            } catch (EnigmaException exception) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
                mBuilder.setTitle("Error Found!");
                mBuilder.setMessage(exception.getMessage());
                mBuilder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                            }
                        });
                AlertDialog mAlert = mBuilder.create();
                mAlert.show();
            }
        } else if (v.getId() == R.id.logo) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            mBuilder.setTitle("Enigma Help");
            mBuilder.setMessage(R.string.title);
            mBuilder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            AlertDialog mAlert = mBuilder.create();
            mAlert.show();
        } else if (v.getId() == R.id.btn_about) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            mBuilder.setTitle("About");
            mBuilder.setMessage(R.string.about);

            mBuilder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            AlertDialog mAlert = mBuilder.create();
            mAlert.show();
        }
    }

    /**
     * Set ArrayAdapter for given Spinner sp.
     *
     * @param sp    The Spinner object to be set.
     * @param array The corresponding String array.
     * @param i     The corresponding index.
     */
    private void setAdapter(Spinner sp, final String[] array, final int i) {
        ArrayAdapter<String> mSettingAdapter = new ArrayAdapter<>(
                this, R.layout.item_select, array);
        sp.setAdapter(mSettingAdapter);
        sp.setSelection(0);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                rotors[i] = array[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
