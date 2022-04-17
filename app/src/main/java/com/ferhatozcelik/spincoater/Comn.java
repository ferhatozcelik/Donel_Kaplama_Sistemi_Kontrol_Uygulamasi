package com.ferhatozcelik.spincoater;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class Comn extends AppCompatActivity {
    private static final String TAG = "Comn";

    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private StringBuilder DataStringIN = new StringBuilder();
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    TextView textdata;
    private ReadInput mReadThread = null;
    private Button button_StartStop, button_HizKaydet, send, button_HumTemp, button_HizKaydet2, button_HizKaydet3;
    private EditText editText_Hiz;
    private TextView textViewRpm, textViewHiz, textViewNem, textViewTemp;
    private SeekBar seekBarHiz, seekBarTime;
    private CheckBox checkBox_Time;
    int i;
    private EditText editText_ProTime;
    boolean timestatus;
    private CountDownTimer countDownTimer;


    LottieAnimationView thumb_up;
    ArrayList<Integer> timesplit;
    ArrayList<Integer> hizsplit;

    double hiz = 0;
    private Button button_terminal;
    ArrayList list;
    Context context = Comn.this;
    ArrayAdapter adapter;

    int index = 0;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent newint = getIntent();
        address = newint.getStringExtra(MainActivity.EXTRA_ADDRESS);
        setContentView(R.layout.activity_comn);

        textdata = findViewById(R.id.textViewdata);

        button_HumTemp = findViewById(R.id.button_HumTemp);
        button_HizKaydet2 = findViewById(R.id.button_HizKaydet2);
        button_HizKaydet3 = findViewById(R.id.button_HizKaydet3);
        button_StartStop = findViewById(R.id.button_StartStop);
        button_HizKaydet = findViewById(R.id.button_HizKaydet);
        editText_Hiz = findViewById(R.id.editText_Hiz);
        textViewRpm = findViewById(R.id.textViewRpm);
        textViewHiz = findViewById(R.id.textViewHiz);
        textViewNem = findViewById(R.id.textViewNem);
        textViewTemp = findViewById(R.id.textViewTemp);
        seekBarHiz = findViewById(R.id.seekBarHiz);
        seekBarTime = findViewById(R.id.seekBarTime);
        checkBox_Time = findViewById(R.id.checkBox_Time);
        editText_ProTime = findViewById(R.id.editText_ProTime);

        button_terminal = findViewById(R.id.button_terminal);

        thumb_up = findViewById(R.id.animationView);


        list = new ArrayList();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_activated_1, list);


        seekBarHiz.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                editText_Hiz.setText("" + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                editText_ProTime.setText("" + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        checkBox_Time.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox_Time.isChecked()) {
                    timestatus = true;
                    Toast.makeText(getApplicationContext(), "Zamanlayıcı Aktif!", Toast.LENGTH_SHORT).show();

                } else {
                    timestatus = false;
                    Toast.makeText(getApplicationContext(), "Zamanlayıcı Deaktif!", Toast.LENGTH_SHORT).show();

                }
            }
        });


        button_HizKaydet2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sayistr = editText_Hiz.getText().toString();
                int sayi = Integer.parseInt(sayistr);
                if (sayi > 0){
                    sayi = sayi - 1;
                    String newsayi = sayi + "";
                    editText_Hiz.setText(newsayi);
                }

            }
        });

        button_HizKaydet3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sayistr = editText_Hiz.getText().toString();
                int sayi = Integer.parseInt(sayistr);
                if (sayi < 100){
                sayi = sayi + 1;
                String newsayi = sayi + "";
                editText_Hiz.setText(newsayi);
                }
            }
        });

        button_StartStop.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (hiz != 0) {

                    Log.d("MotorKontrol", "hiz"+hiz);
                    BtnDelay();
                    if (button_StartStop.getText().toString().equals(getString(R.string.ba_lat))) {
                        Log.d("MotorKontrol", "hiz" + timestatus);
                        MotorKontrol(1);
                        button_StartStop.setText(getString(R.string.durdur));
                        if (timestatus) {
                            Log.d("MotorKontrol", "hiz"+timestatus);
                            Zamanlayici();
                        }

                    } else if (button_StartStop.getText().toString().equals(getString(R.string.durdur))) {

                        Log.d("MotorKontrol", "hiz"+hiz);
                        MotorKontrol(0);
                        button_StartStop.setText(getString(R.string.ba_lat));
                        if (timestatus) {
                            Log.d("MotorKontrol", "hiz"+timestatus);
                            countDownTimer.cancel();
                            checkBox_Time.setEnabled(true);
                        }
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Hiz Giriniz Min:%15", Toast.LENGTH_SHORT).show();
                }

            }
        });

        button_HumTemp.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                BtnDelay();

                if (btSocket != null) {
                    try {
                        String nemtempwrite = "HumTemp";
                        btSocket.getOutputStream().write(nemtempwrite.getBytes());
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "Hata Mesajı : " + e, Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });


        button_HizKaydet.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                BtnDelay();
                String hizread = editText_Hiz.getText().toString();
                if (hizread.equals("")) {
                    Toast.makeText(getApplicationContext(), "Değer giriniz!", Toast.LENGTH_SHORT).show();
                } else {
                    hiz = Double.parseDouble(hizread);
                    if (hiz > 14 && hiz <= 100) {
                        String hizwrite = "hiz:" + hiz;
                        hizKaydet(hizwrite);
                        textViewHiz.setText("Hiz(%): " + hiz);
                    } else {
                        Toast.makeText(getApplicationContext(), "15-100 arasında değer giriniz!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        button_terminal.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {


                final AlertDialog dialogBuilder = new AlertDialog.Builder(Comn.this).create();
                LayoutInflater inflater = Comn.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.alert_dialog_terminal, null);

                final EditText edt_com = dialogView.findViewById(R.id.edt_com);
                Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
                Button buttonSubmit = dialogView.findViewById(R.id.buttonSubmit);

                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.cancel();
                    }
                });
                buttonSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Log.d("MotorKontrol", "Baslat");
                        if (!edt_com.getText().toString().equals("")) {

                            Log.d("MotorKontrol", "edt_com");
                            if (btSocket != null) {
                                try {
                                    btSocket.getOutputStream().write(edt_com.getText().toString().getBytes());
                                    Toast.makeText(getApplicationContext(), "Komut Gönderildi!", Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    Toast.makeText(getApplicationContext(), "Hata Mesajı : " + e, Toast.LENGTH_SHORT).show();

                                }
                            }
                            dialogBuilder.cancel();
                        } else {
                            Toast.makeText(getApplicationContext(), "Boş Alanaları Doldurunuz!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialogBuilder.setView(dialogView);
                dialogBuilder.getWindow().setBackgroundDrawableResource(R.drawable.edittext_backgroundalert);
                dialogBuilder.show();


            }
        });
        new BTbaglan().execute();
    }

    private void Timer() {

        if (index <= adapter.getCount() - 1) {
            String item = adapter.getItem(index).toString();
            String[] motorhizüretimssure = item.trim().split(",");
            String[] hiz = motorhizüretimssure[0].trim().split(":");
            String[] time = motorhizüretimssure[1].trim().split(":");
            String hizvalue = hiz[1];
            int timevalue = Integer.parseInt(time[1]) * 1000;

            String hizwrite = "hiz:" + hizvalue;
            hizKaydet(hizwrite);
            textViewHiz.setText("Hiz(%): " + hizvalue);


            Log.d("Test", "" + hizwrite);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    MotorKontrol(1);

                }
            }, 1000);


            Log.d("Test", "" + timevalue);


            button_StartStop.setText(getString(R.string.durdur));
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MotorKontrol(0);
                    if (adapter.getCount() - 1 >= index) {
                        index = index + 1;
                        Timer();
                    } else {
                        Toast.makeText(getApplicationContext(), "Üretim Bitti! ", Toast.LENGTH_SHORT).show();

                        button_StartStop.setText(getString(R.string.ba_lat));
                    }
                }
            }, timevalue);


        } else {
            //boş
        }
    }


    public AdapterView.OnItemClickListener macrosec = new AdapterView.OnItemClickListener() {

        @SuppressLint("SetTextI18n")
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String info = ((TextView) view).getText().toString();

            list.remove(position);
            adapter.notifyDataSetChanged();


        }
    };

    private void BtnDelay() {
        thumb_up.setVisibility(View.VISIBLE);
        BtnEnable(false);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                thumb_up.setVisibility(View.GONE);
                BtnEnable(true);
            }
        }, 2000);
    }

    int time;

    private void Zamanlayici() {

        time = Integer.parseInt(editText_ProTime.getText().toString());
        countDownTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                editText_ProTime.setText(String.valueOf(millisUntilFinished / 1000));
                checkBox_Time.setEnabled(false);

            }

            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(), "Zaman Doldu! ", Toast.LENGTH_SHORT).show();
                MotorKontrol(0);
                button_StartStop.setText(getString(R.string.ba_lat));
                editText_ProTime.setText(time + "");
                checkBox_Time.setEnabled(true);
            }
        }.start();
    }

    private void hizKaydet(String hizwrite) {

        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(hizwrite.getBytes());
                Toast.makeText(getApplicationContext(), "Motor hızı değişti!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Hata Mesajı : " + e, Toast.LENGTH_SHORT).show();

            }
        }

    }

    @SuppressLint("SetTextI18n")
    private void MotorKontrol(int value) {

        Log.d("MotorKontrol", "hiz: " + value);
        if (value == 0) {
            if (btSocket != null) {

                Log.d("MotorKontrol", btSocket.toString());

                try {

                    Log.d("MotorKontrol", "0 - " + btSocket.toString());
                    btSocket.getOutputStream().write("Durdur".toString().getBytes());
                    Toast.makeText(getApplicationContext(), "Motor Durdu!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Bağlantı Kesildi!", Toast.LENGTH_SHORT).show();

                }
            }
        } else if (value == 1) {
            if (btSocket != null) {
                try {

                    Log.d("MotorKontrol", "1 - " + btSocket.toString());

                    btSocket.getOutputStream().write("Baslat".toString().getBytes());
                    Toast.makeText(getApplicationContext(), "Motor Çalışıyor!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Bağlantı Kesildi!", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void ArdiunoReadData(String strInput) {

        String[] value = strInput.split(":");

        switch (value[0]) {
            case "rpm":
                String v = value[1].trim();
                textViewRpm.setText(getString(R.string.rpm) + v);
                break;
            case "nem":
                String vv = value[1].trim();
                textViewNem.setText(getString(R.string.hum) + vv);
                break;
            case "temp":
                String vvv = value[1].trim();
                textViewTemp.setText(getString(R.string.temp) + vvv + "\u2103");
                break;
            case "status":
                String vvvv = value[1].trim();
                textdata.setText(getString(R.string.status) + vvvv);
                break;
        }

    }

    private int mMaxChars = 50000;

    private class ReadInput implements Runnable {

        private boolean bStop = false;
        private Thread t;

        public ReadInput() {
            textdata.setText("");
            t = new Thread(this, "Input Thread");
            t.start();
        }

        public boolean isRunning() {
            return t.isAlive();
        }

        @Override
        public void run() {
            InputStream inputStream;
            try {
                inputStream = btSocket.getInputStream();

                while (!bStop) {
                    byte[] buffer = new byte[256];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;
                        /*
                         * This is needed because new String(buffer) is taking the entire buffer i.e. 256 chars on Android 2.3.4 http://stackoverflow.com/a/8843462/1287554
                         */
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                        }
                        final String strInput = new String(buffer, 0, i);

                        /*
                         * If checked then receive text, better design would probably be to stop thread if unchecked and free resources, but this is a quick fix
                         */

                        textdata.post(new Runnable() {
                            @Override
                            public void run() {
                                ClearStatus();
                                //textdata.append(strInput);
                                ArdiunoReadData(strInput);
                           /*
                                int txtLength = textdata.getEditableText().length();
                                if(txtLength > mMaxChars){
                                    textdata.getEditableText().delete(0,  txtLength - mMaxChars);
                                }
                               */
                            }

                            private void ClearStatus() {

                                textdata.setText("");
                            }
                        });


                    }
                    Thread.sleep(500);
                }
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void stop() {
            bStop = true;

        }

    }

    private void Disconnect() {
        MotorKontrol(0);
        BtnEnable(false);
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                // msg("Error");
            }
        }
        finish();
    }

    private void BtnEnable(boolean b) {
        if (!b) {
            button_HizKaydet.setEnabled(false);
            button_StartStop.setEnabled(false);
        } else {
            button_HizKaydet.setEnabled(true);
            button_StartStop.setEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Disconnect();
    }

    private class BTbaglan extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(Comn.this, "Baglanıyor...", "Lütfen Bekleyin");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice cihaz = myBluetooth.getRemoteDevice(address);
                    btSocket = cihaz.createInsecureRfcommSocketToServiceRecord(myUUID);

                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();

                    if (mReadThread != null) {
                        mReadThread.stop();
                        while (mReadThread.isRunning())
                            ; // Wait until it stops
                        mReadThread = null;

                    }
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!ConnectSuccess) {
                // msg("Baglantı Hatası, Lütfen Tekrar Deneyin");
                Toast.makeText(getApplicationContext(), "Bağlantı Hatası Tekrar Deneyin", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                //   msg("Baglantı Basarılı");
                Toast.makeText(getApplicationContext(), "Bağlantı Başarılı", Toast.LENGTH_SHORT).show();

                mReadThread = new ReadInput();
                isBtConnected = true;
            }
            progress.dismiss();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Disconnect();
        MotorKontrol(0);
    }


}

