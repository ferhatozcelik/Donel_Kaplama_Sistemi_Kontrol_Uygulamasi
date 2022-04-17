package com.ferhatozcelik.spincoater;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private static final String TAG ="MainActivity";
    private BluetoothAdapter mBADP = null;
    private Set<BluetoothDevice> eslesmiscihazlar;

    public static String EXTRA_ADDRESS ="device_address";
    ListView cihazListesi;

    ArrayAdapter<String> adapter;
    Context context = MainActivity.this;
    private FirebaseAnalytics mFirebaseAnalytics;

    private final BroadcastReceiver mBroadcasReceiver1 = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    };

    private final BroadcastReceiver mBroadcasReceiver2 = new BroadcastReceiver() {
        //Burda Bluetooth  görünürlük/görünmezlik işlemleri var.
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)){
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
                switch (mode) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "Görünürlük İzni Verildi");
                        Toast.makeText(context,"Görünürlük 300 Saniye Boyunca Açık", Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "Görünürlük İzni Verildi,Bağlantı Alınabilir");
                        Toast.makeText(context,"Görünürlük 300 Saniye Boyunca Açık", Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "Görünürlük ve Bağlantı İzni Yok");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "Bağlanıyor");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "Bağlantı Sağlandı");
                        break;
                }
            }
        }
    };
    private TextView device;
    private SharedPreferences sharedPref;
    private Button baglan, button_devicedelete, toggle, btn_lister;
    private TextView textView_Red;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        baglan = findViewById(R.id.button_baglan);
        button_devicedelete = findViewById(R.id.button_devicedelete);
        toggle = findViewById(R.id.button_bt);
        btn_lister =  findViewById(R.id.button_ref);
        cihazListesi =  findViewById(R.id.listviewid);
        device = findViewById(R.id.textView_device);
        textView_Red = findViewById(R.id.textView_Red);


        mBADP = BluetoothAdapter.getDefaultAdapter();
        make_list();



        baglan.setVisibility(View.GONE);
        button_devicedelete.setVisibility(View.GONE);
        device.setVisibility(View.GONE);

        btn_lister.setVisibility(View.VISIBLE);
        toggle.setVisibility(View.VISIBLE);
        cihazListesi.setVisibility(View.VISIBLE);
        textView_Red.setVisibility(View.VISIBLE);

        if (mBADP == null) {
            // Device does not support Bluetooth
            toggle.setText("Bluetooth\nDesteklemiyor!");
            toggle.setEnabled(false);
        } else if (!mBADP.isEnabled()) {
            // Bluetooth is not enabled :)
            toggle.setText("Bluetooth\nAç");
        } else {
            // Bluetooth is enabled
            toggle.setText("Bluetooth\nKapat");
        }


        String addressString = sharedPref.getString("address",null);

        if (addressString != null){

            Intent comintent = new Intent(getApplicationContext(), Comn.class);
            comintent.putExtra(EXTRA_ADDRESS, addressString);
            startActivity(comintent);
            device.setText("Spin Coater\n" + "MAC: " + addressString);

            baglan.setVisibility(View.VISIBLE);
            button_devicedelete.setVisibility(View.VISIBLE);
            device.setVisibility(View.VISIBLE);
            btn_lister.setVisibility(View.GONE);
            toggle.setVisibility(View.GONE);
            cihazListesi.setVisibility(View.GONE);
            textView_Red.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),"Kayıtlı Cihaza Bağlanıyor...", Toast.LENGTH_SHORT).show();

        }else{
            baglan.setVisibility(View.GONE);
            button_devicedelete.setVisibility(View.GONE);
            device.setVisibility(View.GONE);
            btn_lister.setVisibility(View.VISIBLE);
            toggle.setVisibility(View.VISIBLE);
            cihazListesi.setVisibility(View.VISIBLE);
            textView_Red.setVisibility(View.VISIBLE);

        }


        baglan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String addressString = sharedPref.getString("address",null);
                if (addressString != null){
                    Intent comintent = new Intent(getApplicationContext(), Comn.class);
                    comintent.putExtra(EXTRA_ADDRESS, addressString);
                    startActivity(comintent);
                }

            }
        });

        button_devicedelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("address",null);
                editor.apply();
                editor.putString("address",null).clear();
                device.setText("Kayıtlı\nCihaz Yok");


                baglan.setVisibility(View.GONE);
                button_devicedelete.setVisibility(View.GONE);
                device.setVisibility(View.GONE);

                btn_lister.setVisibility(View.VISIBLE);
                toggle.setVisibility(View.VISIBLE);
                cihazListesi.setVisibility(View.VISIBLE);
                textView_Red.setVisibility(View.VISIBLE);





            }
        });

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enabledisableBT();
            }
        });
        btn_lister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                make_list();
            }
        });


    }


    private void enabledisableBT() {
        if(mBADP ==null)
        {
            toggle.setText("Bluetooth\nDesteklemiyor!");
            toggle.setEnabled(false);
            Toast.makeText(getApplicationContext(),"Bluetooth Cihazı Yok", Toast.LENGTH_SHORT).show();

        }else
        if(!mBADP.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcasReceiver1,BTIntent);

            toggle.setText("Bluetooth\nKapat");
            make_list();
        }else {
            mBADP.disable();
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcasReceiver1,BTIntent);

            toggle.setText("Bluetooth\nAç");
        }

    }

    public void clicked(View view) {
        Log.d(TAG,"Görünürlük 300 Saniye Boyunca Açık ");

        Intent gorunurlukIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        gorunurlukIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
        startActivity(gorunurlukIntent);

        IntentFilter intentfilter = new IntentFilter(mBADP.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcasReceiver2,intentfilter);

    }


    public void make_list()
    {

        try {
            eslesmiscihazlar = mBADP.getBondedDevices();
            ArrayList list =new ArrayList();

            if(eslesmiscihazlar.size()>0)
            {
                for(BluetoothDevice bt: eslesmiscihazlar)
                {
                    if (bt.getName().equals("SpinCoater") ||bt.getName().equals("SpinCoaterV5") || bt.getName().equals("FO_SpinCoaterV6")) {
                        list.add(bt.getName()+"\n"+bt.getAddress());
                    }
                }

            }
            else
            {
                //  Toast.makeText(getApplicationContext(),"Bluetooth Kapalı", Toast.LENGTH_LONG).show();
            }
            final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_activated_1,list);
            cihazListesi.setAdapter(adapter);

            //Listeden bir cihaz sectigimizce cagrılacak method
            cihazListesi.setOnItemClickListener(cihazSec);
        }catch (Exception e){
        }
    }
    public AdapterView.OnItemClickListener cihazSec = new AdapterView.OnItemClickListener() {

        @SuppressLint("SetTextI18n")
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("address",address);
            editor.apply();
            device.setText("Spin Coater\n" + "MAC: " + address);
            Toast.makeText(context, "Cihaz Kayıt Yapıldı!",Toast.LENGTH_LONG).show();

            baglan.setVisibility(View.VISIBLE);
            button_devicedelete.setVisibility(View.VISIBLE);
            device.setVisibility(View.VISIBLE);

            btn_lister.setVisibility(View.GONE);
            toggle.setVisibility(View.GONE);
            cihazListesi.setVisibility(View.GONE);
            textView_Red.setVisibility(View.GONE);




        }
    };

}

