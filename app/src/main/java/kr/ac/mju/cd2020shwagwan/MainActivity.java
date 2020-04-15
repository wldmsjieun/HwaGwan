package kr.ac.mju.cd2020shwagwan;

import android.os.Bundle;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

 public class MainActivity extends AppCompatActivity {

//     static public MainActivity ma = new MainActivity();
     // AlertDialog View layout

//     static public FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        fabAdd = findViewById(R.id.fabAdd);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        EditText tvBrand = findViewById(R.id.etBrand);
        EditText tvItem = findViewById(R.id.etName);
//        TextView tvBrand = findViewById(R.id.tvBrand);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);




    }






}
