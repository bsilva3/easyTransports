package com.transports;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.transports.account_management.AccountSettingsFragment;
import com.transports.data.Agency;
import com.transports.data.Stop;
import com.transports.schedules.SchedulesFragment;
import com.transports.schedules.SchedulesViewerV2Fragment;
import com.transports.schedules.TripChild;
import com.transports.tickets.TicketsFragment;
import com.transports.map.MapsActivity;
import com.transports.utils.Constants;

import java.io.Serializable;

import static com.transports.map.MapsActivity.INTENT_EXTRA_TRIP_ID;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        StopFragment.OnListFragmentInteractionListener,
        AgencyFragment.OnListFragmentInteractionListener,
        SchedulesViewerV2Fragment.OnFragmentInteractionListener{

    private com.google.android.material.bottomnavigation.BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationView = findViewById(R.id.navigationView);

        navigationView.setOnNavigationItemSelectedListener(this);
        int menuID = getIntent().getIntExtra(Constants.MENU_INTENT, R.id.bottom_menu_tickets);
        navigateToMenu(menuID);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return navigateToMenu(item.getItemId());
    }

    public boolean navigateToMenu(int menuID) {
        switch (menuID) {
            case R.id.bottom_menu_tickets: {
                openFragment(new TicketsFragment());
                setTitle("Tickets");
                break;
            }
            case R.id.bottom_menu_schedule: {
                openFragment(new SchedulesFragment());
                setTitle("Routing/Scheduling");
                break;
            }
            case R.id.bottom_menu_stops: {
                openFragment(new StopFragment());
                setTitle("Stops");
                break;
            }
            case R.id.bottom_menu_agencies: {
                openFragment(new AgencyFragment());
                setTitle("Agencies");
                break;
            }
            case R.id.bottom_menu_settings: {
                openFragment(new AccountSettingsFragment());
                setTitle("Settings");
                break;
            }
        }
        return true;
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onListFragmentInteraction(Stop stop) {
        Intent intent = new Intent(this, StopDetailActivity.class);
        intent.putExtra("Stop",  stop);
        startActivity(intent);
    }

    @Override
    public void onListAgencyFragmentInteraction(Agency item) {
        Intent intent = new Intent(this, AgencyDetailActivity.class);
        intent.putExtra("Agency", item);
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(TripChild child) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(INTENT_EXTRA_TRIP_ID, child.getTripID());
        Log.d("maps", child.getTripID());
        startActivity(intent);
    }
}
