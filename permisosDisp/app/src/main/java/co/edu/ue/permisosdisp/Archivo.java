package co.edu.ue.permisosdisp;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;

import android.content.pm.PackageManager;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Archivo {


    private static final String DIRECTORY_NAME = "ArchivoUE";
    private static final int STORAGE_PERMISSION_CODE = 1;

    // Variables para el contexto y la actividad.
    private Context context;
    private Activity activity;

    // Constructor que inicializa el contexto y la actividad.
    public Archivo(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    // Método para guardar información en un archivo.
    public void guardarArchivo(String nombreArchivo, String informacion) {
        File directory = getOrCreateDirectory();

        if (directory == null) {
            Toast.makeText(context, "Permiso de almacenamiento no concedido", Toast.LENGTH_LONG).show();
            return;
        }

        File file = new File(directory, nombreArchivo);
        try {
            FileWriter writer = new FileWriter(file);
            writer.append(informacion);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Archivo guardado en: " + directory, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("Archivo", "Error al guardar el archivo", e);
            Toast.makeText(context, "Error al guardar el archivo", Toast.LENGTH_LONG).show();
        }
    }

    // Método para obtener o crear el directorio donde se guardarán los archivos.
    private File getOrCreateDirectory() {
        if (!hasStoragePermission()) {
            requestStoragePermission();
            return null;
        }

        File directory;
        // Elección del directorio dependiendo de la versión de Android.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            directory = new File(Environment.getExternalStorageDirectory(), DIRECTORY_NAME);
        } else {
            directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), DIRECTORY_NAME);
        }

        if (!directory.exists()) {
            directory.mkdir();
        }

        return directory;
    }

    // Método que verifica si tiene permiso de almacenamiento.
    private boolean hasStoragePermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    // Método que solicita el permiso de almacenamiento al usuario.
    private void requestStoragePermission() {
        if (!hasStoragePermission()) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }
}
