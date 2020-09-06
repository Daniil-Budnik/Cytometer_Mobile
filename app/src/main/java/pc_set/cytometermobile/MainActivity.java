package pc_set.cytometermobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends Activity {

    // Метаданные
    private String VersionADD, DateUpdateInfo;

    // Работа с текстом на экране
    private TextView VerT;

    // Диологи
    private AlertDialog.Builder AlB;
    private AlertDialog Al;

    // Работа с кнопками
    private Button StartB, SetB, ExitB;

    // Вызов новых окон
    private Intent intent;

    // Главные метод
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VersionADD = "0.2.6.Beta-And_St (BT_TEST)";
        DateUpdateInfo = "29.07.2019";

        VerT = (TextView) findViewById(R.id.VerText);

        VerT.setText("Ver: " + VersionADD);

        ButPushMain();

    }

    // Обработка нажатий кнопок
    private void ButPushMain() {
        StartB = (Button) findViewById(R.id.B_Start_A);
        SetB = (Button) findViewById(R.id.B_Set_A);
        ExitB = (Button) findViewById(R.id.B_Exit_A);

        VerT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Version: " + VersionADD + "\n" + "Update Date: " + DateUpdateInfo, Snackbar.LENGTH_LONG).show();
            }
        });


        StartB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this,StartActiv.class);
                startActivity(intent);
            }
        });

        SetB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this,SetActiv.class);
                startActivity(intent);
            }
        });

        ExitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlB = new AlertDialog.Builder(MainActivity.this);

                AlB.setTitle("Exit");
                AlB.setMessage("Are you sure?");

                AlB.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });

                AlB.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                Al = AlB.create();
                Al.show();
            }
        });
    }
}
