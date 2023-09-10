package co.edu.ue.permisosdisp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.camera2.CameraManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Variables para mantener referencias al contexto y actividad.
    private Context context;
    private Activity activity;

    // Componentes para mostrar la versión de Android.
    private TextView versionAndroid;
    private int versionSDK;

    // Componentes para mostrar el nivel de batería.
    private ProgressBar pbLevelBattery;
    private TextView tvLevelBattery;
    private IntentFilter batteryFilter;

    // Variables para controlar el flash de la cámara.
    private CameraManager cameraManager;
    private String cameraId;
    private Button btnOnLight;
    private Button btnOffLight;

    // Componentes para la funcionalidad de guardar archivo.
    private EditText fileName;
    private Archivo archivo;

    // Componente para mostrar el estado de la conexión a internet.
    private TextView tvConexion;
    private ConnectivityManager conexion;

    // Variables para controlar Bluetooth.
    private BluetoothAdapter mBluetoothAdapter;
    private Button btnOnBluetooth;
    private Button btnOffBluetooth;
    private Button btnAddFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización de componentes y establecimiento de listeners.
        initUIComponents();
        setBatteryListener();
        setButtonListeners();
    }

    // Método para inicializar componentes de la UI y recursos.
    private void initUIComponents() {
        this.context = getApplicationContext();
        this.activity = this;

        // Referencias a los componentes de la UI.
        this.versionAndroid = findViewById(R.id.tvVersionAndroid);
        this.pbLevelBattery = findViewById(R.id.pbLevelBattery);
        this.tvLevelBattery = findViewById(R.id.tvLevelBatteryLB);
        this.fileName = findViewById(R.id.etFileName);
        this.tvConexion = findViewById(R.id.tvConexion);
        this.btnOnLight = findViewById(R.id.btnOn);
        this.btnOffLight = findViewById(R.id.btnOff);
        this.btnOnBluetooth = findViewById(R.id.btnBluetoothOn);
        this.btnOffBluetooth = findViewById(R.id.btnOffBluetooth);
        this.btnAddFile = findViewById(R.id.btnAddFile);

        // Inicialización de otros objetos.
        this.archivo = new Archivo(context, activity);
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    // Método para establecer el listener de la batería.
    private void setBatteryListener() {
        batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(broadcastReceiver, batteryFilter);
    }

    // Método para establecer listeners para los botones.
    private void setButtonListeners() {
        btnOnLight.setOnClickListener(this::onLight);
        btnOffLight.setOnClickListener(this::offLight);
        btnAddFile.setOnClickListener(this::guardarArchivoOnClick);
        btnOnBluetooth.setOnClickListener(this::onBluetooth);
        btnOffBluetooth.setOnClickListener(this::offBluetooth);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar la versión de Android y verificar la conexión cuando la actividad se reanude.
        updateAndroidVersion();
        checkConnection();
    }

    // Método para actualizar el texto que muestra la versión de Android.
    private void updateAndroidVersion() {
        String versionSO = Build.VERSION.RELEASE;
        versionSDK = Build.VERSION.SDK_INT;
        versionAndroid.setText("Version Android: " + versionSO + " /SDK: " + versionSDK);
    }

    // Método para encender la linterna del dispositivo.
    private void onLight(View view) {
        try {
            if (cameraManager == null) {
                cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                cameraId = cameraManager.getCameraIdList()[0];
            }
            cameraManager.setTorchMode(cameraId, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para apagar la linterna del dispositivo.
    private void offLight(View view) {
        try {
            if (cameraManager != null) {
                cameraManager.setTorchMode(cameraId, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // BroadcastReceiver que escucha cambios en el nivel de batería y actualiza la UI en consecuencia.
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int levelBattery = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            pbLevelBattery.setProgress(levelBattery);
            tvLevelBattery.setText("3. Nivel de la batería: " + levelBattery + "%");
        }
    };

    // Método para verificar el estado de la conexión a Internet y actualizar la UI.
    private void checkConnection() {
        if (conexion == null) {
            conexion = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        NetworkInfo network = conexion.getActiveNetworkInfo();
        boolean isConnected = network != null && network.isConnectedOrConnecting();
        tvConexion.setText(isConnected ? "Conexion a internet: " + network.getTypeName() : "Conexion a internet: No hay conexion");
    }

    // Método que se ejecuta al hacer clic en el botón para guardar información en un archivo.
    private void guardarArchivoOnClick(View view) {
        String nombreArchivo = fileName.getText().toString().trim();
        if (nombreArchivo.isEmpty()) {
            Toast.makeText(context, "Ingrese un nombre de archivo", Toast.LENGTH_SHORT).show();
        } else {
            String informacion = "Información del archivo";
            archivo.guardarArchivo(nombreArchivo, informacion);
        }
    }

    // Método para desactivar el Bluetooth del dispositivo.
    private void offBluetooth(View view) {
        if (mBluetoothAdapter == null) {
            Toast.makeText(context, "El dispositivo no admite Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.disable();
                Toast.makeText(context, "Bluetooth Desactivado", Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException e) {
            Toast.makeText(context, "No tiene permiso para desactivar el Bluetooth", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para activar el Bluetooth del dispositivo.
    private void onBluetooth(View view) {
        if (mBluetoothAdapter == null) {
            Toast.makeText(context, "El dispositivo no admite Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
                Toast.makeText(context, "Bluetooth Activado", Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException e) {
            Toast.makeText(context, "No tiene permiso para activar el Bluetooth", Toast.LENGTH_SHORT).show();
        }
    }
}
