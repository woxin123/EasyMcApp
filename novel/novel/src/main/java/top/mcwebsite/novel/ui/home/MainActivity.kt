package top.mcwebsite.novel.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import top.mcwebsite.common.android.ext.setVisible
import top.mcwebsite.novel.R

class MainActivity : AppCompatActivity() {

    private val showBottomNavigationViewDestIds = mutableListOf<Int>()

    private lateinit var bottomNavigationView: BottomNavigationView

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initObservable()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavigationView = findViewById(R.id.bottom_tab)
        bottomNavigationView
            .setupWithNavController(navController)
        showBottomNavigationViewDestIds.apply {
            add(R.id.bookShelfFragment)
            add(R.id.discoveryFragment)
            add(R.id.meFragment)
        }
        navController.addOnDestinationChangedListener { _, destation, _ ->
            viewModel.changeBottomNavigationStatus(
                showBottomNavigationViewDestIds.contains(
                    destation.id
                )
            )
        }
    }

    private fun initObservable() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bottomNavigationStatus.collect {
                    bottomNavigationView.setVisible(it)
                }
            }
        }
    }
}
