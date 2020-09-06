package pc_set.cytometermobile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Advanced extends Activity {

    private Button Back;

    // Главный метод
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced);
        ButPushAd();
    }

    // Обработка нажатий кнопок
    public void ButPushAd(){
        Back = (Button) findViewById(R.id.BackBSet);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Advanced.this.finish();
            }
        });
    }
}
