package com.example.appahida

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ToggleButton
import androidx.core.graphics.drawable.toDrawable
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.appahida.constants.Constants
import com.example.appahida.constants.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.appahida.fragments.MainFragment
import com.example.appahida.viewmodels.WorkoutViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main_activity.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var navController : NavController

    lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        navigateToWorkingIfNeeded(intent)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        NavigationUI.setupWithNavController(toolbar, navController)

        /*val bottomSheet = findViewById(R.id.bottom_sheet) as NestedScrollView

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        val button = findViewById<ToggleButton>(R.id.toggleButton)

       button.setOnCheckedChangeListener { buttona, b ->
            if(b){
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }else{
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState){
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        button.isChecked = false
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        button.isChecked = true
                    }

                    else -> Unit
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                System.out.println("sliding $slideOffset")

                //button.isChecked = slideOffset > 0.5f
            }
        })*/
    }

    private fun navigateToWorkingIfNeeded(intent: Intent?){
        if((intent?.action) == ACTION_SHOW_TRACKING_FRAGMENT){

            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHostFragment.navController.navigate(R.id.action_global_workingFragment)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        navigateToWorkingIfNeeded(intent)
    }
}