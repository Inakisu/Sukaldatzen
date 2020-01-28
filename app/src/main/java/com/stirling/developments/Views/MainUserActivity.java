package com.stirling.developments.Views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.stirling.developments.Models.POJOs.Cazuela;
import com.stirling.developments.Models.gson2pojo.Source;
import com.stirling.developments.R;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainUserActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.toolbar) Toolbar toolbar;

    public Fragment visuFrag;

    private Menu menu;
    private SubMenu modulos;
    private String correoUsu = "Correo del usuario";
    private FirebaseAuth auth;
    ArrayList<Cazuela> listaModulos = new ArrayList<Cazuela>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        sharedPreferences = getBaseContext().getSharedPreferences("navprefs",
                Context.MODE_PRIVATE);

        //Bind UI elements
        ButterKnife.bind(this);

        //Initialize toolbar
        setSupportActionBar(toolbar);

        //Obtenemos correo del usuario para mostrar en el NavDrawer
        correoUsu = FirebaseAuth.getInstance().getCurrentUser().getEmail(); //Null Object reference, por Login Google?

        //Initialize Navigation Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Set click listener for items in nav drawer
        navigationView.setNavigationItemSelectedListener
                (new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // Handle navigation view item clicks here.
                int id = item.getItemId();

                switch (id)
                {
                    case R.id.nav_newPot:
                        //Para emparejar nueva olla por bluetooth
                        //Nos movemos a activity de sincr. bluetooth
                        Intent btIntent = new Intent (MainUserActivity.this,
                                BluetoothActivity.class);
                        startActivityForResult(btIntent, 1);
                        break;

                    case R.id.nav_settings:
                        Toast.makeText(MainUserActivity.this, "Función ajustes en " +
                                "desarrollo.", Toast.LENGTH_LONG).show();
                        Intent ajustesIntent = new Intent(MainUserActivity.this,
                                AjustesActivity.class);
                        startActivity(ajustesIntent);
                        break;

                    case R.id.nav_signOut:
                        Toast.makeText(MainUserActivity.this, "Sesión cerrada",
                                Toast.LENGTH_SHORT).show();
                        //Obtenemos instancia de FirebaseAuth actual
                        auth = FirebaseAuth.getInstance();
                        //Cerramos sesión de firebase
                        auth.signOut();
                        //Cambiamos a la activity Login
                        Intent loginIntent = new Intent(MainUserActivity.this,
                                LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                        break;
                }


                drawer.closeDrawer(GravityCompat.START);

                return true;
            }

        });
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.textView);
        navUsername.setText(correoUsu);


        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override public void onDrawerSlide(View drawerView, float slideOffset) {}
            @Override public void onDrawerOpened(View drawerView) {}
            @Override public void onDrawerStateChanged(int newState) {
                //método de actualizar lista
                actualizarDrawer();
            }

            @Override
            public void onDrawerClosed(View drawerView) {


            }
        });

        Fragment visuFrag = new VisualizationFragment();
        openNewFragment(visuFrag, "VisualizationFragment",null,
                false);
        menu = navigationView.getMenu();
        modulos = menu.addSubMenu("Módulos");


    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onResume(){
        super.onResume();
//        actualizarDrawer();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
//            Intent refresh = new Intent(this, MainUserActivity.class);
//            startActivity(refresh);
//            this.finish();
            actualizarDrawer();
        }
    }


    /**************************************************************************
     *  /name: openNewFragment
     *  /brief: This method opens the specified fragment in parameters
     **************************************************************************/
    public void openNewFragment(Fragment fragment, String fragmentTitle, Bundle extras,
                                boolean addToBackStack)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(extras);
        transaction.replace(R.id.frame_container, fragment, fragmentTitle);
        if(addToBackStack)
            transaction.addToBackStack(null);
        transaction.commit();
    }
    public ArrayList<Cazuela> getArrayList(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<Cazuela>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void actualizarDrawer(){
        listaModulos = getArrayList("navprefs");
        modulos.clear();
        for (int i = 1; i < listaModulos.size(); i++){
            modulos.add(0,i,0,listaModulos.get(i).getNombreCazuela());
        }
    }

}