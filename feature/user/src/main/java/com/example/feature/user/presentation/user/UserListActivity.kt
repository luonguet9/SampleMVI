package com.example.feature.user.presentation.user

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.feature.user.R
import com.example.core.network.NetworkConnectivity
import com.example.core.network.NetworkStatus
import com.example.feature.user.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserListActivity : AppCompatActivity() {
	
	private lateinit var binding: ActivityMainBinding
	private lateinit var navController: NavController
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		setupNavigation()
		setupNetworkMonitoring()
	}
	
	private fun setupNavigation() {
		val navHostFragment = supportFragmentManager
			.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
		navController = navHostFragment.navController
		
		// Setup ActionBar with NavController
		setSupportActionBar(binding.toolbar)
		setupActionBarWithNavController(navController)
	}
	
	private fun setupNetworkMonitoring() {
		val networkConnectivity = NetworkConnectivity(this)
		
		lifecycleScope.launch {
			repeatOnLifecycle(Lifecycle.State.STARTED) {
				networkConnectivity.observeConnectivity()
					.collect { status ->
						updateNetworkStatus(status)
					}
			}
		}
	}
	
	private fun updateNetworkStatus(status: NetworkStatus) {
		binding.networkStatusBar.apply {
			when (status) {
				is NetworkStatus.Available,
				is NetworkStatus.AvailableWifi,
				is NetworkStatus.AvailableCellular -> {
					// Hide offline banner
					visibility = View.GONE
				}
				is NetworkStatus.Lost,
				is NetworkStatus.Unavailable -> {
					// Show offline banner
					visibility = View.VISIBLE
					setBackgroundColor(getColor(R.color.status_offline))
					text = getString(R.string.network_offline)
				}
				/*is NetworkStatus.Losing -> {
					visibility = View.VISIBLE
					setBackgroundColor(getColor(R.color.status_warning))
					text = getString(R.string.network_unstable)
				}*/
			}
		}
	}
	
	override fun onSupportNavigateUp(): Boolean {
		return navController.navigateUp() || super.onSupportNavigateUp()
	}
}


