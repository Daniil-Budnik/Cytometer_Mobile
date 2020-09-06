package pc_set.cytometermobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SetActiv extends Activity {

    // Вызов новых окон
    private Intent intent;

    // Работа с кнопками
    private Button Back, Save, Def, Adv;

    private int CofZip,TimesUpdate,MaxLineTrais,MaxYTrais,Frequency,MinZCh,MaxZCH, COLOR_FR, COLOR_TR;
    private boolean PowerTr, PowerFr, TIMERS_POWER, Grapg_Interact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        Load_Event ();
        ButPushSet();
    }


    // Обработка нажатий кнопок
    private void ButPushSet(){
        Back = (Button) findViewById(R.id.BackBMenu);
        Save = (Button) findViewById(R.id.SaveBBS);
        Def = (Button) findViewById(R.id.DefaultBS);
        Adv = (Button) findViewById(R.id.AdvancedBS);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetActiv.this.finish();
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Save_CLICK();
            }
        });

        Def.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Default_CLICK();
            }
        });

        Adv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(SetActiv.this, Advanced.class);
                startActivity(intent);
            }
        });

    }

    // Клик на Save
    private void Save_CLICK(){
        Get_Event();

        SAVE_COLOR_FR(COLOR_FR);
        SAVE_COLOR_TR(COLOR_TR);
        SAVE_CofZip(CofZip);
        SAVE_TimesUpdate(TimesUpdate);
        SAVE_MaxLineTrais(MaxLineTrais);
        SAVE_MaxYTrais(MaxYTrais);
        SAVE_Frequency(Frequency);
        SAVE_MinZCh(MinZCh);
        SAVE_MaxZCH(MaxZCH);

        SAVE_PowerTr(PowerTr);
        SAVE_PowerFr(PowerFr);
        SAVE_TIMERS_POWER(TIMERS_POWER);
        SAVE_Grapg_Interact(Grapg_Interact);

        Set_Event ();
    }

    // Клик на Default
    private void Default_CLICK(){
        DEFULT_APP();
    }

    // Событие загрузки
    private void Load_Event(){
        COLOR_FR = LOAD_COLOR_FR()    ;
        COLOR_TR = LOAD_COLOR_TR()    ;
        CofZip = LOAD_CofZip()      ;
        TimesUpdate = LOAD_TimesUpdate() ;
        MaxLineTrais = LOAD_MaxLineTrais();
        MaxYTrais = LOAD_MaxYTrais()   ;
        Frequency = LOAD_Frequency()   ;
        MinZCh = LOAD_MinZCh()      ;
        MaxZCH = LOAD_MaxZCH()      ;
        PowerTr = LOAD_PowerTr()         ;
        PowerFr = LOAD_PowerFr()         ;
        TIMERS_POWER = LOAD_TIMERS_POWER()  ;
        Grapg_Interact =  LOAD_Grapg_Interact ()  ;

        Set_Event();
    }

    // Событие сохранения
    public void Get_Event(){

    }

    // Событие обновления
    public void Set_Event(){

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
        MaxYTrais = 20000;              // Макс по вертикали точек трэйса
        Frequency = 16348;              // Частота (16348, это стандартная чистота)
        MinZCh = -2500;                 // Минимум по вертикали точек частот
        MaxZCH = 10000;                 // Макс по вертикали точек частот
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

    private boolean LOAD_PowerTr()           {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); return SETTING_BUILD.getBoolean ("PowerTr", false);}
    private boolean LOAD_PowerFr()           {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); return SETTING_BUILD.getBoolean ("PowerFr", false);}
    private boolean LOAD_TIMERS_POWER()      {SETTING_BUILD = getSharedPreferences (LOC,MODE_PRIVATE); return SETTING_BUILD.getBoolean ("TIMERS_POWER", false);}
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


}
