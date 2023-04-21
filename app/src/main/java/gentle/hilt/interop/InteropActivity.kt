
package gentle.hilt.interop

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import gentle.hilt.interop.databinding.ActivityInteropBinding
import gentle.hilt.interop.databinding.NavHeaderInteropBinding

@AndroidEntryPoint
class InteropActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityInteropBinding
    private val viewModel: InteropViewModel by viewModels()
    private val startActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInteropBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_slideshow
            ),
            binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navGraph.setupWithNavController(navController)

        backFromNavigationDrawerImplementation()

        val navHeaderBinding = NavHeaderInteropBinding.bind(binding.navGraph.getHeaderView(0))
        navHeaderBinding.apiLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://rickandmortyapi.com"))
            startActivityLauncher.launch(intent)
        }
    }

    private fun backFromNavigationDrawerImplementation() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    true -> binding.drawerLayout.closeDrawer(GravityCompat.START)
                    false -> {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        menuInflater.inflate(R.menu.menu_search, menu)
        search()
        return true
    }

    private fun search() {
        val searchMenu = binding.appBarMain.toolbar.menu.findItem(R.id.action_search)
        val searchView = searchMenu.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    binding.appBarMain.searchCharacter.visibility = View.GONE
                } else {
                    binding.appBarMain.searchCharacter.visibility = View.VISIBLE
                    viewModel.search(
                        newText,
                        binding.appBarMain.searchCharacter,
                        navController,
                        this,
                        searchView
                    )
                }
                return true
            }
        })

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                searchView.isIconified = true
            }
        }
    }

    override fun onSupportNavigateUp() = navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    private val navController by lazy {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navHostFragment.navController
    }
}
