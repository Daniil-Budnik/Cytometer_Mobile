package pc_set.cytometermobile;


public class Setting {

    private int CofZip,TimesUpdate,MaxLineTrais,MaxYTrais,Frequency,MinZCh,MaxZCH, COLOR_FR, COLOR_TR;
    private boolean PowerTr, PowerFr, GR_INTERACTIVE, TIMERS_POWER, Grapg_Interact;

    public void DEFAULT_OUT(){
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
        GR_INTERACTIVE = false;         // Интерактив графиков
        TIMERS_POWER = true;            // Работка потока таймера
        Grapg_Interact = false;         // Интерактив графика
    }
}
