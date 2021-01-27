package com.example.appahida.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.efaso.admob_advanced_native_recyvlerview.AdmobNativeAdAdapter
import com.example.appahida.R
import com.example.appahida.adapters.CategoryAdapter
import com.example.appahida.adapters.ExercicesAdapter
import com.example.appahida.constants.Constants
import com.example.appahida.databinding.FragmentAddworkoutBinding
import com.example.appahida.databinding.FragmentMotionBinding
import com.example.appahida.db.exercicesDB.ExerciseItem
import com.example.appahida.db.workoutsdb.Exercice
import com.example.appahida.objects.ExerciseToAdd
import com.example.appahida.objects.WorkoutCategory
import com.example.appahida.utils.onQueryTextChanged
import com.example.appahida.viewmodels.MainViewModel
import com.example.appahida.viewmodels.WorkoutViewModel
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.unified_ad_view.view.*

@AndroidEntryPoint
class WorkoutMotion : Fragment() {
    companion object {
        const val TAG = "PlayScreenFragment"
        fun newInstance(): WorkoutMotion {
            val args = Bundle()
            val playScreenFragment = WorkoutMotion()
            playScreenFragment.arguments = args
            return playScreenFragment
        }
    }

    lateinit var fragmentPlayScreenBinding: FragmentMotionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentPlayScreenBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_motion, container, false)
        return fragmentPlayScreenBinding.root
    }
}