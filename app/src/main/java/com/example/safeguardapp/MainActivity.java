package com.example.safeguardapp;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safeguardapp.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FloatingActionButton fab;
    LinearLayout bottomSheet;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheet.setVisibility(View.GONE);


        replaceFragment(new MapFragment());
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.map) {
                replaceFragment(new MapFragment());
            } else if (itemId == R.id.report) {
                replaceFragment(new ReportFragment());
            } else if (itemId == R.id.moreInfo) {
                replaceFragment(new LocEduFragment());
            } else if (itemId == R.id.profile) {
                replaceFragment(new ProfileFragment());
            }
            return true;
        });



        // Implementation of SOS button
        fab = findViewById(R.id.btn_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "press longer to activate SOS", Toast.LENGTH_SHORT).show();
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openSOSActivated();
                return false;
            }
        });


        //Check flag from SOSActivatedActivity
//        fromSosActivated = getIntent().getBooleanExtra("FROM_SOS_ACTIVATED", false);



    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void openSOSActivated(){
        Intent intent = new Intent(this, SosActivatedActivity.class);
        startActivityForResult(intent,1);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result is from AnotherActivity
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Display the persistent bottom sheet
//            bottomSheetBehavior.setPeekHeight(110);
//            bottomSheetBehavior.setHideable(false);
//            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheet.setVisibility(View.VISIBLE);
            TextView TV_view = findViewById(R.id.TV_view);
            TV_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSOSActivated();
                }
            });

        }else if(resultCode == RESULT_CANCELED){
//            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bottomSheet.setVisibility(View.GONE);

        }
    }





    private void onBackPressedFromActivity() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack(); // Go back to the previous fragment
        } else {
            super.onBackPressed(); // If no fragments in the back stack, perform default back action
        }
    }
}