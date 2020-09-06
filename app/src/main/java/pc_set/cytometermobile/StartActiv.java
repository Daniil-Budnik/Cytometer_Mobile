package pc_set.cytometermobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class StartActiv extends Activity {

    // Главный метод
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        CFG_Control ();                 // Настройки
        FINDBYID ();                    // Присвоение значений
        SetUp_Const_But();              // Константы для кнопок
        Button_OnClick_Power();         // Обработка нажатий клавиш
        DEFAULT_OUT();                  // ---------------------
        StarterGraphConf();             // Стартовые настройки
        TIMERS(TIMERS_POWER);           // Таймер
        CharInter(Grapg_Interact);      // Интерактив
        Bluetooth_CLIENT_SV();          // Модуль Bluetooth
    }

    // Коэф второго бита, тут надо корректировать
    final private int BinRead2Bit = 256;

    private int Frequency, MaxLineTrais, MaxYTrais, TimesUpdate, MinZCh,
            MaxZCH, CofZip, Tr, numver, Min, Max, LMax, LMin, NTT,
            COLOR_FR, COLOR_TR;

    private boolean PowerFr, PowerTr, PowerStart, PowerPause, ConBTMenu,
            ConfPower, SavePower, ExtraPower, NTR,PotocPower, BT_FINDER,
            SOCKET_ACTIVE, TIMERS_POWER, Grapg_Interact, TEST_SPEED_POWER;

    // Путь файла для чтения
    public String FileS;
    private FileInputStream fileInputStream;

    // Открытие файла
    public void OpFile() {
        try { fileInputStream = openFileInput(FileS); }
        catch (FileNotFoundException e) { Log.e("ERROR", "_________________ don't open file!!!"); }
    }

    // Закрытие фалйа
    public void ClFile() {
        try { fileInputStream.close(); }
        catch (IOException e) { Log.e("ERROR", "_________________ don't close file!!!"); }
    }

    private LinearLayout BTMENU, CONFMENU, SAVEMENU, EXTRAMENU;

    // Кнопки
    public Button ConnectBB, StartBB, PauseBB, SaveBB, MenuBB, ConfBB,
             ExtraBB, UpData_BT_BB, Disc_BT_BB, Test_Speed_BT;

    // Цветовая политра
    public class ColorSet{
        public String GoodCl = "#95D94B";
        public String BenCl = "#EACF81";
        public String RenCl = "#E5927C";
        public String TrigerCl = "#FF3639";
        public String Defult = "#DDDDDD";
        public String BlueRC = "#5E73FF";
    }

    // UUID 00001101-0000-1000-8000-00805F9B34FB
    public final UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public SetActiv setting;

    // Чтение настроек
    public void CFG_Control(){
        FileS = "Data_2Byte_REC.bin";                   // Путь файла (========= ПОЗЖЕ ПЕРЕДЕЛАТЬ =========)
        //FileS = "D.txt";


        COLOR_FR =     LOAD_COLOR_FR();                 // Цвет графика частот
        COLOR_TR =     LOAD_COLOR_TR();                 // Цвет трэйса
        CofZip =       LOAD_CofZip();                   // Коэффицент сжатия графика частот (от 1 до 4096) (Использовать при низкой производительности, искажение графика сильное, на трэйсе не отражается)
        TimesUpdate =  LOAD_TimesUpdate();              // Обновление (1000 = 1 секунде) (Менее 30 не ставить = 30 FPS, 50 самый оптимальный = 20 FPS, 100 необходимый = 10 FPS, 1000 = 1 FPS)
        MaxLineTrais = LOAD_MaxLineTrais();             // Макс по горизонтали точек трэйса
        MaxYTrais =    LOAD_MaxYTrais();                // Макс по вертикали точек трэйса
        Frequency =    LOAD_Frequency();                // Частота (16348, это стандартная чистота)
        MinZCh =       LOAD_MinZCh();                   // Минимум по вертикали точек частот
        MaxZCH =       LOAD_MaxZCH();                   // Макс по вертикали точек частот

        PowerTr        =    LOAD_PowerTr();             // Визуализация графика трэйса
        PowerFr       =     LOAD_PowerFr();             // Визуализация графика частот
        TIMERS_POWER   =    LOAD_TIMERS_POWER();        // Работка потока таймера
        Grapg_Interact =    LOAD_Grapg_Interact();      // Интерактив графика
    }

    final public String LOC = "config";

    private SharedPreferences SETTING_BUILD;
    private SharedPreferences.Editor editor;

    // ==========================================================================================================
    // === Стандартные настройки=================================================================================
    // ==========================================================================================================

    private void DEFAULT_OUT(){
        COLOR_FR = 1;                   // Цвет графика частот
        COLOR_TR = 2;                   // Цвет трэйса (1 = Зелёный, 2 = Красный, 3 = Жёлтый, 4 = Синий)
        CofZip = 1;                     // Коэффицент сжатия графика частот (от 1 до 4096) (Использовать при низкой производительности, искажение графика сильное, на трэйсе не отражается)
        TimesUpdate = 30;               // Обновление (1000 = 1 секунде) (Менее 30 не ставить = 30 FPS, 50 самый оптимальный = 20 FPS, 100 необходимый = 10 FPS, 1000 = 1 FPS)
        MaxLineTrais = 500;             // Макс по горизонтали точек трэйса
        MaxYTrais = 100000;              // Макс по вертикали точек трэйса
        Frequency = 4096;              // Частота (16348, это стандартная чaстота)
        MinZCh = -20000;                 // Минимум по вертикали точек частот
        MaxZCH = 30000;                 // Макс по вертикали точек частот
        PowerTr = true;                 // Визуализация графика трэйса
        PowerFr = true;                 // Визуализация графика частот
        TIMERS_POWER = true;            // Работка потока таймера
        Grapg_Interact = false;         // Интерактив графика
    }

    // ==========================================================================================================
    // === Метод для изминения на стандартные настройки==========================================================
    // ==========================================================================================================

    @SuppressLint("CommitPrefEdits")
    public void DEFULT_APP(){
        DEFAULT_OUT();
        SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor = SETTING_BUILD.edit ();editor.commit ();
        SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putInt ("COLOR_FR",COLOR_FR);editor.commit ();
        SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putInt ("COLOR_TR",COLOR_TR);editor.commit ();
        SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putInt ("CofZip",CofZip);editor.commit ();
        SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putInt ("TimesUpdate",TimesUpdate);editor.commit ();
        SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putInt ("MaxLineTrais",MaxLineTrais);editor.commit ();
        SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putInt ("MaxYTrais",MaxYTrais);editor.commit ();
        SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putInt ("Frequency",Frequency);editor.commit ();
        SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putInt ("MinZCh",MinZCh);editor.commit ();
        SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putInt ("MaxZCH",MaxZCH);editor.commit ();
        SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putBoolean ("PowerTr",PowerTr);editor.commit ();
        SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putBoolean ("PowerFr",PowerFr);editor.commit ();
        SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putBoolean ("TIMERS_POWER",TIMERS_POWER);editor.commit ();
        SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putBoolean ("Grapg_Interact",Grapg_Interact);editor.commit ();
    }

    // ==========================================================================================================
    // === Методы для загрузки настроек==========================================================================
    // ==========================================================================================================

    private int LOAD_COLOR_FR()      {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); return SETTING_BUILD.getInt ("COLOR_FR", 0);}
    private int LOAD_COLOR_TR()      {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); return SETTING_BUILD.getInt ("COLOR_TR", 0);}
    private int LOAD_CofZip()        {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); return SETTING_BUILD.getInt ("CofZip", 0);}
    private int LOAD_TimesUpdate()   {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); return SETTING_BUILD.getInt ("TimesUpdate", 0);}
    private int LOAD_MaxLineTrais()  {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); return SETTING_BUILD.getInt ("MaxLineTrais", 0);}
    private int LOAD_MaxYTrais()     {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); return SETTING_BUILD.getInt ("MaxYTrais", 0);}
    private int LOAD_Frequency()     {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); return SETTING_BUILD.getInt ("Frequency", 0);}
    private int LOAD_MinZCh()        {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); return SETTING_BUILD.getInt ("MinZCh", 0);}
    private int LOAD_MaxZCH()        {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); return SETTING_BUILD.getInt ("MaxZCH", 0);}

    private boolean LOAD_PowerTr()           {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); return SETTING_BUILD.getBoolean ("PowerTr", true);}
    private boolean LOAD_PowerFr()           {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); return SETTING_BUILD.getBoolean ("PowerFr", true);}
    private boolean LOAD_TIMERS_POWER()      {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); return SETTING_BUILD.getBoolean ("TIMERS_POWER", true);}
    private boolean LOAD_Grapg_Interact()    {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); return SETTING_BUILD.getBoolean ("Grapg_Interact", false);}

    // ==========================================================================================================
    // === Методы для сохранения настроек========================================================================
    // ==========================================================================================================

    private void SAVE_COLOR_FR(int X)         {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putInt ("COLOR_FR",X); editor.commit ();editor.commit ();}
    private void SAVE_COLOR_TR(int X)         {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putInt ("COLOR_TR",X);editor.commit ();editor.commit ();}
    private void SAVE_CofZip(int X)           {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putInt ("CofZip",X);editor.commit ();editor.commit ();}
    private void SAVE_TimesUpdate(int X)      {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putInt ("TimesUpdate",X);editor.commit ();editor.commit ();}
    private void SAVE_MaxLineTrais(int X)     {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putInt ("MaxLineTrais",X);editor.commit ();editor.commit ();}
    private void SAVE_MaxYTrais(int X)        {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putInt ("MaxYTrais",X);editor.commit ();editor.commit ();}
    private void SAVE_Frequency(int X)        {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putInt ("Frequency",X);editor.commit ();editor.commit ();}
    private void SAVE_MinZCh(int X)           {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putInt ("MinZCh",X);editor.commit ();editor.commit ();}
    private void SAVE_MaxZCH(int X)           {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putInt ("MaxZCH",X);editor.commit ();editor.commit ();}

    private void SAVE_PowerTr(boolean X)          {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putBoolean ("PowerTr",X);editor.commit ();editor.commit ();}
    private void SAVE_PowerFr(boolean X)          {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putBoolean ("PowerFr",X);editor.commit ();editor.commit ();}
    private void SAVE_TIMERS_POWER(boolean X)     {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putBoolean ("TIMERS_POWER",X);editor.commit ();editor.commit ();}
    private void SAVE_Grapg_Interact(boolean X)   {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); editor.putBoolean ("Grapg_Interact",X);editor.commit ();editor.commit ();}


    // Константы для интерактива меню (не изменять !!!)
    private void SetUp_Const_But(){
        PowerStart         =   false;
        PowerPause         =   false;
        TimerPower         =   false;
        timersGridPower    =   false;
        PotocPower         =   false;
        SOCKET_ACTIVE      =   false;
        TEST_SPEED_POWER   =   false;
    }

    // Диалог
    private AlertDialog DialoG;
    private AlertDialog.Builder DilBild;

    // Работа с буфером
    public BufferedReader istream;

    // Потоки
    public TimersGrid timersGrid;

    private Switch PowGr,PowTr, PowInteractiv;

    // =================================================================
    // ================ Реализация событий нажатий !!! =================
    // =================================================================

    // Присвоение объектов
    private void FINDBYID(){
            ConnectBB       =  (Button) findViewById (R.id.ConnectB);
            StartBB         =  (Button) findViewById (R.id.StartB);
            PauseBB         =  (Button) findViewById (R.id.PauseB);
            SaveBB          =  (Button) findViewById (R.id.SaveB);
            MenuBB          =  (Button) findViewById (R.id.MenuB);
            ConfBB          =  (Button) findViewById (R.id.ConfigB);
            ExtraBB         =  (Button) findViewById (R.id.ExtraB);

            Test_Speed_BT   = (Button) findViewById (R.id.SpeedB);

            PowGr           = (Switch) findViewById (R.id.SFG);
            PowTr           = (Switch) findViewById (R.id.STG);
            PowInteractiv   = (Switch) findViewById (R.id.GRAPH_INTERACT);

            UpData_BT_BB    = (Button) findViewById (R.id.UpData_BT_B);
            Disc_BT_BB      = (Button) findViewById (R.id.Discon_BT_B);

            SAVEMENU = (LinearLayout) findViewById (R.id.SaveMenu);
            CONFMENU = (LinearLayout) findViewById (R.id.ConfMenu);
            BTMENU = (LinearLayout) findViewById (R.id.BtMenu);
            EXTRAMENU = (LinearLayout) findViewById (R.id.ExtraMenu);

            CLOSE_B ();

    }

    // Обработка событий при нажатии
    public void Button_OnClick_Power() {

        // Пред настройки кнопок
        {
            StartBB.getBackground().setColorFilter(Color.parseColor(new ColorSet().GoodCl), PorterDuff.Mode.DARKEN);
            PauseBB.getBackground().setColorFilter(Color.parseColor(new ColorSet().RenCl), PorterDuff.Mode.DARKEN);
        }

        // Кнопка старт/стоп
        {
            StartBB.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    if (!PowerStart) {
                        OpFile ();
                        StartBB.getBackground().setColorFilter(Color.parseColor(new ColorSet().TrigerCl), PorterDuff.Mode.DARKEN);
                        PauseBB.getBackground().setColorFilter(Color.parseColor(new ColorSet().BenCl), PorterDuff.Mode.DARKEN);

                        try {
                            istream = new BufferedReader (new InputStreamReader (fileInputStream, "ISO-8859-15"));

                        } catch (UnsupportedEncodingException e) {
                            Log.e ("ERROR","_________________ UnsupportedEncodingException >>> istream >>> CODER ");
                        }

                        PowerStart = true;
                        StartBB.setText ("Stop");
                        TraisL = new ArrayList<> ();
                        Tr = 0;
                        TimerPower = true;

                    } else {

                        StartBB.getBackground().setColorFilter(Color.parseColor(new ColorSet().GoodCl), PorterDuff.Mode.DARKEN);
                        PauseBB.getBackground().setColorFilter(Color.parseColor(new ColorSet().RenCl), PorterDuff.Mode.DARKEN);
                        TimerPower = false;
                        ClFile ();
                        PowerStart = false;
                        PowerPause = false;
                        StartBB.setText ("Start");
                        PauseBB.setText ("Pause");
                        GraphClear ();
                    }
                }
            });
        }

        // Кнопка паузы
        {
            PauseBB.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    if (PowerStart & !PowerPause) {
                        PauseBB.getBackground().setColorFilter(Color.parseColor(new ColorSet().GoodCl), PorterDuff.Mode.DARKEN);
                        PowerPause = true;
                        PauseBB.setText ("Continue");
                        TimerPower = false;
                    } else if (PowerStart & PowerPause) {
                        PauseBB.getBackground().setColorFilter(Color.parseColor(new ColorSet().BenCl), PorterDuff.Mode.DARKEN);
                        PowerPause = false;
                        PauseBB.setText ("Pause");
                        TimerPower = true;
                    }
                }
            });
        }

        // Конпка сохранения
        {
            SaveBB.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    if(!ConfPower & !ConBTMenu &!ExtraPower){
                        S_B ();
                    }else{
                        CLOSE_B ();
                        S_B ();
                    }
                }
            });
        }

        // Конпка меню
        {
            MenuBB.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    SetUp_Const_But();
                    try {
                        GraphClear ();
                        ClFile ();
                    } catch (AndroidRuntimeException e) {
                        Log.e ("ERROR", "_________________ OFF GRAPH (AndroidRuntimeException)");
                    } catch (NullPointerException e) {
                        Log.e ("ERROR", "_________________ OFF GRAPH (NullPointerException)");
                    }
                    CloseSocet();
                    StartActiv.this.finish ();
                }
            });
        }

        // Конпка подключения к Bluetooth
        {
            ConnectBB.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    if(!ConfPower & !SavePower &!ExtraPower){
                        CBT_B ();
                    }else{
                        CLOSE_B ();
                        CBT_B ();
                    }
                }
            });
        }

        // Кнопка конфигов
        {
            ConfBB.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    if(!ConBTMenu & !SavePower &!ExtraPower){
                        Conf_B ();
                    }else{
                        CLOSE_B ();
                        Conf_B ();
                    }
                }
            });
        }

        // Кнопка открытия меню Extra
        {
            ExtraBB.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    if(!ConBTMenu & !SavePower & !ConfPower){
                        E_B ();
                    }else{
                        CLOSE_B ();
                        E_B ();
                    }
                }
            });
        }

        // Переключатель рисования графика частот
        {
            PowGr.setOnCheckedChangeListener (new CompoundButton.OnCheckedChangeListener () {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    PowerTr = b; // Визуализация графика частот
                    if(!b){
                        GraphClear ();
                        GrTr (PowerFr);
                    }else{
                        GrFr (PowerTr);
                    }
                }
            });
        }

        // Прееключатель рисования графика трэйса
        {
            PowTr.setOnCheckedChangeListener (new CompoundButton.OnCheckedChangeListener () {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    PowerFr = b; // Визуализация графика трэйса
                    if(!b){
                        GraphClear ();
                        GrFr (PowerTr);
                    }else{
                        GrTr (PowerFr);
                    }
                }
            });
        }

        // Переключатель графического интерактива
        {
            PowInteractiv.setOnCheckedChangeListener (new CompoundButton.OnCheckedChangeListener () {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Grapg_Interact   = b;
                    CharInter(b);
                }
            });
        }

        // BT обновление
        {
            UpData_BT_BB.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    Bluetooth_CLIENT_SV();
                }
            });
        }

        // Тестер скорости BT
        {
            Test_Speed_BT.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    if(SOCKET_ACTIVE & !TEST_SPEED_POWER){
                        CLOSE_B ();
                        TEST_SPEED_POWER = true;
                        TEST_SPEED_BT ();
                    }else {
                        Snackbar.make(view, "No connection...", Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }

        // BT отключение
        {
            Disc_BT_BB.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    CloseSocet();
                }
            });

        }

    }

    // Событие тестирование скорости BT
    private void TEST_SPEED_BT(){
        FPS_BT fps_bt = new FPS_BT ();
        fps_bt.execute ();
    }

    // Событие нажатия Connect
    private void CBT_B(){
        if (!ConBTMenu) {
            ConBTMenu = true;
            BT_SETUP();
            BTMENU.setVisibility (View.VISIBLE);
        } else {
            ConBTMenu = false;
            BTMENU.setVisibility (View.GONE);
        }
    }

    // Событие нажатия Save
    private void S_B(){
        if (!SavePower) {
            SavePower = true;
            SAVEMENU.setVisibility (View.VISIBLE);
        } else {
            SavePower = false;
            SAVEMENU.setVisibility (View.GONE);
        }
    }

    // Событие нажатия Config
    private void Conf_B(){
        if (!ConfPower) {
            ConfBB.getBackground().setColorFilter(Color.parseColor(new ColorSet ().BenCl), PorterDuff.Mode.DARKEN);
            ConfPower = true;
            CONFMENU.setVisibility (View.VISIBLE);
        } else {
            ConfBB.getBackground().setColorFilter(Color.parseColor(new ColorSet ().Defult), PorterDuff.Mode.DARKEN);
            ConfPower = false;
            CONFMENU.setVisibility (View.GONE);
        }
    }

    // Событие нажатия Extra
    private void E_B(){
        if (!ExtraPower) {
            ExtraBB.getBackground().setColorFilter(Color.parseColor(new ColorSet ().BenCl), PorterDuff.Mode.DARKEN);
            ExtraPower = true;
            EXTRAMENU.setVisibility (View.VISIBLE);
        } else {
            ExtraBB.getBackground().setColorFilter(Color.parseColor(new ColorSet ().Defult), PorterDuff.Mode.DARKEN);
            ExtraPower = false;
            EXTRAMENU.setVisibility (View.GONE);
        }
    }

    // Событие CLOSE
    private void CLOSE_B(){
        SAVEMENU.setVisibility (View.GONE);
        CONFMENU.setVisibility (View.GONE);
        BTMENU.setVisibility (View.GONE);
        EXTRAMENU.setVisibility (View.GONE);

        ConfBB.getBackground().setColorFilter(Color.parseColor(new ColorSet ().Defult), PorterDuff.Mode.DARKEN);
        ExtraBB.getBackground().setColorFilter(Color.parseColor(new ColorSet ().Defult), PorterDuff.Mode.DARKEN);

        ConBTMenu = false;
        ConfPower = false;
        SavePower = false;
        ExtraPower = false;
    }

    // =================================================================
    // =============== Реализация работы с графиками !!! ===============
    // =================================================================

    private LineChart CharGR;
    private BarChart TraisGR;

    // Массивы данных для точек графиков
    private List<Entry> CharL;
    private List<BarEntry> TraisL;

    // Обработка массивов
    private LineDataSet LDS;
    private BarDataSet BDS;

    // Финальная обработка данных, для построения графиков
    private LineData LD;
    private BarData BD;

    // Дескриптор
    private Description des;

    // Граф частот
    public void GrFr(boolean Power){
        if(Power){
            LDS = new LineDataSet(CharL, "");
            LDS.setColor(Color.GREEN);
            LDS.setDrawValues(false);
            LDS.setDrawCircleHole(false);
            LDS.setDrawCircles(false);
            LD = new LineData(LDS);
            CharGR.setData(LD);
            CharGR.setNoDataTextColor(Color.WHITE);
            CharGR.invalidate();
        }
    }

    // Граф трэйса
    public void GrTr(boolean Power){
        if(Power){
            BDS = new BarDataSet(TraisL, "");
            BDS.setHighLightColor(Color.WHITE);
            BDS.setValueTextColor(Color.WHITE);
            BDS.setColor(Color.YELLOW);
            BDS.setDrawValues(false);
            BD = new BarData(BDS);
            TraisGR.setData(BD);
            TraisGR.invalidate();
        }
    }

    // Постройка графиков
    public void GraphGrid() {
        GrTr (PowerFr);
        GrFr (PowerTr);
    }

    // Таймер
    private Thread thread;
    private boolean TimerPower, timersGridPower;

    // Таймер
    public void TIMERS(Boolean x) {
            thread = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (TimerPower & !PotocPower) {
                                    timersGrid = new TimersGrid ();
                                    timersGrid.onPostExecute (timersGrid.doInBackground ());
                                }
                            }

                        });
                        Thread.sleep(TimesUpdate);
                    }
                    catch (InterruptedException e) { Log.e("ERROR", "_________________ TIMER WTF???"); }
                }
            }
        };
        thread.setDaemon(true);
        thread.setPriority(10);
        if (x) {
            thread.start();
        }
    }

    // Чистка графика
    public void GraphClear() {
        CharGR.clearValues();
        CharGR.invalidate();
        TraisGR.clearValues();
        TraisGR.invalidate();
    }

    // Настройка интерактива
    public void CharInter(boolean x) {
                CharGR.setTouchEnabled(x);                  // Позволяет включить / отключить все возможные сенсорные взаимодействия с диаграммой.
                CharGR.setDragEnabled(x);                   // Включает / отключает перетаскивание (панорамирование) для диаграммы.
                CharGR.setScaleEnabled(x);                  // Включает / отключает масштабирование для диаграммы по обеим осям.
                CharGR.setScaleXEnabled(x);                 // Включает / отключает масштабирование по оси x.
                CharGR.setScaleYEnabled(x);                 // Включает / отключает масштабирование по оси Y.
                CharGR.setPinchZoom(x);                     // Если установлено значение true, пинч-масштабирование включено. Если отключено, оси X и Y можно увеличивать отдельно.
                CharGR.setDoubleTapToZoomEnabled(x);        // Установите значение false, чтобы запретить масштабирование диаграммы с помощью двойного нажатия на нее.

                TraisGR.setTouchEnabled(x);                 // Позволяет включить / отключить все возможные сенсорные взаимодействия с диаграммой.
                TraisGR.setDragEnabled(x);                  // Включает / отключает перетаскивание (панорамирование) для диаграммы.
                TraisGR.setScaleEnabled(x);                 // Включает / отключает масштабирование для диаграммы по обеим осям.
                TraisGR.setScaleXEnabled(x);                // Включает / отключает масштабирование по оси x.
                TraisGR.setScaleYEnabled(x);                // Включает / отключает масштабирование по оси Y.
                TraisGR.setPinchZoom(x);                    // Если установлено значение true, пинч-масштабирование включено. Если отключено, оси X и Y можно увеличивать отдельно.
                TraisGR.setDoubleTapToZoomEnabled(x);       // Установите значение false, чтобы запретить масштабирование диаграммы с помощью двойного нажатия на нее.
    }

    // Направляющие
    private XAxis XA;
    private YAxis YA;

    // Дизайнер
    public void CharDraws() {
        XA = CharGR.getXAxis();
        XA.setTextColor(Color.WHITE);
        XA.setAxisMinimum(0);
        XA.setAxisMaximum(Frequency);

        XA = TraisGR.getXAxis();
        XA.setTextColor(Color.WHITE);
        XA.setAxisMinimum(0);
        XA.setAxisMaximum(MaxLineTrais);

        YA = CharGR.getAxisLeft();
        YA.setTextColor(Color.WHITE);
        YA.setAxisMinimum(MinZCh);
        YA.setAxisMaximum(MaxZCH);

        YA = TraisGR.getAxisLeft();
        YA.setTextColor(Color.WHITE);
        YA.setAxisMinimum(0);
        YA.setAxisMaximum(MaxYTrais);

        YA = CharGR.getAxisRight();
        YA.setTextColor(Color.WHITE);
        YA.setAxisMinimum(MinZCh);
        YA.setAxisMaximum(MaxZCH);

        YA = TraisGR.getAxisRight();
        YA.setTextColor(Color.WHITE);
        YA.setAxisMinimum(0);
        YA.setAxisMaximum(MaxYTrais);
    }

    // Легенда
    private Legend legend;

    // Стартовые настройки
    public void StarterGraphConf() {
        CharGR = (LineChart) findViewById(R.id.GraphChar);
        TraisGR = (BarChart) findViewById(R.id.TraisChar);

        CharGR.setLogEnabled(false);
        TraisGR.setLogEnabled(false);

        des = new Description();
        des.setText("");
        CharGR.setDescription(des);
        TraisGR.setDescription(des);

        legend = CharGR.getLegend();
        legend.setEnabled(false);
        legend = TraisGR.getLegend();
        legend.setEnabled(false);

        CharL = new ArrayList<>();
        CharL.add(new Entry(0, 0));
        LDS = new LineDataSet(CharL, " ");
        LD = new LineData(LDS);
        CharGR.setData(LD);

        CharGR.invalidate();
        TraisL = new ArrayList<>();
        TraisL.add(new BarEntry(0, 0));
        BDS = new BarDataSet(TraisL, " ");
        BD = new BarData(BDS);
        TraisGR.setData(BD);
        TraisGR.invalidate();

        GraphClear();
        CharDraws();
    }

    // Чтение одного числа с бинарника
    public short OpFileRes() {
        try {
            return (short) (istream.read() + istream.read() * BinRead2Bit);
        } catch (IOException e) {
            Log.e("ERROR", "_________________ don't read file");
        }
        return 0;
    }


    private float stReadTxt;

    // Чтение cтроки
    public short OpFileResTXT() {
        try {
            stReadTxt = Float.valueOf ( istream.readLine ()) * 2000;
            return (short) stReadTxt;
        } catch (IOException e) {
            Log.e("ERROR", "_________________ don't read file");
        }
        return 0;
    }

    // Создание массива для графика (деактивирован)
    public void CharArray() {
        CharL = new ArrayList<>();
        Min = 999999;
        Max = -999999;
        for (int i = 0; i < Frequency; i++) {
            numver = OpFileRes();
            CharL.add(new Entry(i*4, numver));
            if (numver > Max) {
                Max = numver;
            }
            if (numver < Min) {
                Min = numver;
            }
        }
    }

    // Создание массива для графика с коэффицентом сжатия
    public void CharArrayBoost() {
        CharL = new ArrayList<>();
        Min = 999999;
        Max = -999999;
        NTT = 0;
        for(int g = 0; g < Frequency/(CofZip*2); g++){
            LMin = 999999;
            LMax = -999999;
            for(int i = 0; i < CofZip*2; i++){
                numver = OpFileRes ();
                if(LMin > numver){LMin = numver; NTR = true;}
                if(LMax < numver){LMax = numver; NTR = false;}
            }
            if(NTR){
                CharL.add(new Entry(NTT * CofZip, LMax));NTT++;
                CharL.add(new Entry(NTT * CofZip, LMin));NTT++;
            }else{
                CharL.add(new Entry(NTT * CofZip, LMin));NTT++;
                CharL.add(new Entry(NTT * CofZip, LMax));NTT++;
            }
            if(LMax > Max){Max = LMax;}
            if(LMin < Min){Min = LMin;}
        }
    }

    // Создание массива для графика с коэффицентом сжатия из TXT
    public void CharArrayBoostTXT() {
        CharL = new ArrayList<>();
        Min = 999999;
        Max = -999999;
        NTT = 0;
        for(int g = 0; g < Frequency/(CofZip*2); g++){
            LMin = 999999;
            LMax = -999999;
            for(int i = 0; i < CofZip*2; i++){
                numver = OpFileResTXT ();
                if(LMin > numver){LMin = numver; NTR = true;}
                if(LMax < numver){LMax = numver; NTR = false;}
            }
            if(NTR){
                CharL.add(new Entry(NTT * CofZip, LMax));NTT++;
                CharL.add(new Entry(NTT * CofZip, LMin));NTT++;
            }else{
                CharL.add(new Entry(NTT * CofZip, LMin));NTT++;
                CharL.add(new Entry(NTT * CofZip, LMax));NTT++;
            }
            if(LMax > Max){Max = LMax;}
            if(LMin < Min){Min = LMin;}
        }
    }

    // Создания массива для трэйса
    public void TrasisArray() {
        if (Tr < MaxLineTrais) {
            TraisL.add(new BarEntry(Tr, Max - Min));
        } else {
            Tr = 0;
            TraisL = new ArrayList<>();
            TraisL.add(new BarEntry(Tr, Max - Min));
        }
        Tr++;
    }

    // Поток для работы грида
    private class TimersGrid extends AsyncTask<Void,Void ,Void>{

        // Работа потока
        @Override
        protected Void doInBackground(Void... voids) {
            PotocPower = true;
            //CharArrayBoostTXT();
            CharArrayBoost();
            TrasisArray();
            return null;
        }

        // Завершение потока
        @Override
        protected void onPostExecute(Void aBoolean) {
            super.onPostExecute(aBoolean);
            GraphGrid ();
            PotocPower = false;
        }
    }

    // =================================================================
    // ======= Реализация беспроводного подключения Bluetooth!!! =======
    // =================================================================

    public String Name_BT, Adr_BT, LocName_BT, STR, SMS_BT,
            NameSer_BT, MAC_Ser_BT, NAME_HOST_BT;

    private BluetoothAdapter Adapter_BT;     // Адаптер BT
    private BluetoothDevice Servers_BT;      // Сервер BT
    private BluetoothSocket Socket_BT;       // Сокет  BT
    private Set<BluetoothDevice> Set_BT;     // Список сопрежённых устройств BT

    private TextView TextV_BT;

    private ListView ListV_BT;
    private ArrayList<String> ArList_BT;
    private ArrayAdapter<String> ArAdap_BT;

    private OutputStream OUTPUTER_BT;            // Отправка по BT
    private InputStream INTPUTER_BT;             // Приём по BT

    private BT_Connecter bt_connecter;      // Поток для подключения
    public BT_CMD_CONNECT bt_cmd_connect;   // Поток для общения по BT каналу

    public View views;

    // Проврка работоспособности BT
    public void BT_STARTER(){
        Adapter_BT = BluetoothAdapter.getDefaultAdapter ();

        // Проверка, на наличие BT
        if(Adapter_BT == null){ Log.e ("ERROR","_________________ BT Adapter don't work");}

        // Проверка, на включение BT
        if(!Adapter_BT.isEnabled ()) {
            Intent enableBluetooth = new Intent (BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult (enableBluetooth, R.layout.activity_start);
            ConnectBB.getBackground ().setColorFilter (Color.parseColor (new ColorSet ().RenCl), PorterDuff.Mode.DARKEN);
        }

        if(Adapter_BT.isEnabled ()){
            ConnectBB.getBackground().setColorFilter(Color.parseColor(new ColorSet().BenCl), PorterDuff.Mode.DARKEN);
        }
    }

    // Локальный BT
    public void BT_CFG(){
        TextV_BT = (TextView) findViewById(R.id.TextInfo);
        LocName_BT = Adapter_BT.getName ();
        STR = "Bluetooth: " + LocName_BT.toString ();
        TextV_BT.setText (STR);
        Set_BT = Adapter_BT.getBondedDevices();
        ListV_BT = (ListView) findViewById (R.id.ListV);
    }

    // Главный метод реализации BT
    public void Bluetooth_CLIENT_SV(){
        BT_STARTER();   // Начальные проверки BT
        if(Adapter_BT.isEnabled ()){
            BT_CFG();       // Настройки BT
            if(!SOCKET_ACTIVE){
                BT_SETUP();     // Работа BT
            }else{
                ConnectBB.getBackground ().setColorFilter (Color.parseColor (new ColorSet ().BlueRC), PorterDuff.Mode.DARKEN);
            }
        }
    }

    // Поиск устройств
    public void BT_SETUP(){
        BT_FINDER = false;

        // Если устройства найдены
        if(Set_BT.size ()>0){
            ArList_BT = new ArrayList<> ();
            BT_FINDER = true;

            // Производится поиск устройств
            for(BluetoothDevice device : Set_BT){
                Name_BT = device.getName ();
                Adr_BT = device.getAddress ();
                ArList_BT.add (Name_BT + " >>> " + Adr_BT);
            }

            // Переопределение в адаптеры
            ArAdap_BT = new ArrayAdapter<> (this,R.layout.support_simple_spinner_dropdown_item,ArList_BT);
            ListV_BT.setAdapter (ArAdap_BT);

            // Проверка нажатия на элемент списка сопрежённых BT устройств
            BT_ClickToConected();

            // Завершение поиска BT устройств
            Adapter_BT.cancelDiscovery();
        } else { // Если устройства небыли найдены
            ArList_BT = new ArrayList<> ();
            ArList_BT.add ("No devices found...");
            ArAdap_BT = new ArrayAdapter<> (this,R.layout.support_simple_spinner_dropdown_item,ArList_BT);
            ListV_BT.setAdapter (ArAdap_BT);
            Log.e("BT don't find","_________________ BT LIST NULL");

            // Завершение поиска BT устройств
            Adapter_BT.cancelDiscovery();
        }
    }

    // Проверка нажатия на элемент списка сопрежённых BT устройств
    public void BT_ClickToConected(){
        ListV_BT.setOnItemClickListener (new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!SOCKET_ACTIVE){
                    if(BT_FINDER){
                        ListClick (adapterView,view,i,l); // Событие. которое происходит при нажатии на выбранное BT устройство
                    }
                }else{
                    Snackbar.make(view, "You are already connected.", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    // Событие, при нажатии на выбранное BT устройство
    public void ListClick(AdapterView<?> adapterView, View view, int i, long l){
        NAME_HOST_BT = adapterView.getItemAtPosition (i).toString ();
        NameSer_BT =  adapterView.getItemAtPosition(i).toString ();
        MAC_Ser_BT = NameSer_BT.substring(NameSer_BT.length() - 17); // Вычленяем MAC-адрес
        Log.e("CONNECT BT", "_________________" + NameSer_BT + " >>>  MAC: " + MAC_Ser_BT + " >>> UUID " + SERIAL_UUID);
        Servers_BT = Adapter_BT.getRemoteDevice(MAC_Ser_BT); // Вводим МАК адрес

        // Поток для подключения
        bt_connecter = new BT_Connecter ();
        bt_connecter.execute ();
        views = view;
    }


    // Поток подключения
    public class BT_Connecter extends AsyncTask{

        // Предварительные настройки UUID
        @Override
        protected void onPreExecute() {
            super.onPreExecute ();
            try { Socket_BT = Servers_BT.createRfcommSocketToServiceRecord (SERIAL_UUID); }
            catch (IOException e) { Log.e ("Socket", "_________________ SERIAL_UUID"); }
        }

        // Подключение
        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                Socket_BT.connect ();
                SOCKET_ACTIVE = true;
                publishProgress ();
                // Проверка подключения (смотреть в терминале последовательных портов)
                SMS_BT = "Cytometer Modile >> Start connect...\n";
                OUTPUTER_BT = Socket_BT.getOutputStream (); // Для отправки
                INTPUTER_BT = Socket_BT.getInputStream (); // Для чтения
                OUTPUTER_BT.write (SMS_BT.getBytes ()); // Отправкла сообщения
            }
            catch (IOException e) { Log.e ("Socket", "_________________ don't connect"); }
            return null;

        }

        // Визуализация
        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate (values);

            Snackbar.make(views, "Connect >>> " + NAME_HOST_BT, Snackbar.LENGTH_LONG).show();

            // Для визуалки меню
            ConnectBB.getBackground ().setColorFilter (Color.parseColor (new ColorSet ().BlueRC), PorterDuff.Mode.DARKEN);
            BTMENU.setVisibility (View.GONE);
            ConBTMenu = false;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute (o);
            bt_cmd_connect = new BT_CMD_CONNECT();
            bt_cmd_connect.execute ();
        }
    }

    // Закрытие сокета
    public void CloseSocet(){
        try {
            SMS_BT = "Cytometer Modile >> Exit...\n\n";
            OUTPUTER_BT.write (SMS_BT.getBytes ());
            Socket_BT.close ();
        }
        catch (IOException e) {} catch (NullPointerException e){}

        SOCKET_ACTIVE = false;
        ConnectBB.getBackground().setColorFilter(Color.parseColor(new ColorSet().BenCl), PorterDuff.Mode.DARKEN);
        try{ Adapter_BT.cancelDiscovery(); }catch (NullPointerException e){}
    }

    // =================================================================
    // ======= Работа и передача информации по Bluetooth каналу!!! =====
    // =================================================================

    // Тестовый поток
    // ----------------------------------------------- ИСПРАВИТЬ ТОТ ТРЕШ :)
    private class FPS_BT extends AsyncTask {

        public int s;

        @Override
        protected Object doInBackground(Object[] objects) {
            for (int x = 0; x < 20; x++){
                for(int y = 0; y< Frequency; y++ ){
                    try { OUTPUTER_BT.write (String.valueOf (y).getBytes ()); } catch (IOException e) { }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute (o);
            TEST_SPEED_POWER = false;
        }
    }

    // Реализация общение по BT каналу
    // ----------------------------------------------- РЕАЛИЗАЦИЯ ГОТОВА К ФИНАЛЬНОЙ ЧАСТИ
    public class BT_CMD_CONNECT extends AsyncTask{

        @Override
        protected void onPreExecute() {
            super.onPreExecute ();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate (values);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute (o);
        }
    }



}