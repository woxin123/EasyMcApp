package top.mcwebsite.novel.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.coroutineScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.mcwebsite.novel.R
import java.lang.IllegalStateException
import java.lang.RuntimeException

class MainActivity : AppCompatActivity() {

    private val showBottomNavigationViewDestIds = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_tab)
        bottomNavigationView
            .setupWithNavController(navController)
        showBottomNavigationViewDestIds.apply {
            add(R.id.bookShelfFragment)
            add(R.id.discoveryFragment)
            add(R.id.rankFragment)
            add(R.id.meFragment)
        }
        navController.addOnDestinationChangedListener { _, destation, _ ->
            if (showBottomNavigationViewDestIds.contains(destation.id)) {
                bottomNavigationView.visibility = View.VISIBLE
            } else {
                bottomNavigationView.visibility = View.GONE
            }
        }

    }
}