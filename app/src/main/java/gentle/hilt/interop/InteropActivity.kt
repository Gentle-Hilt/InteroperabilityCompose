package gentle.hilt.interop

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import gentle.hilt.interop.databinding.ActivityInteropBinding
import gentle.hilt.interop.databinding.NavHeaderInteropBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class InteropActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityInteropBinding
    private val viewModel: InteropViewModel by viewModels()
    private val startActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    private lateinit var searchView: SearchView
    private lateinit var searchMenu: MenuItem

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

        loadingBar(binding.appBarMain.pbLoading)
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
        searchMenu = binding.appBarMain.toolbar.menu.findItem(R.id.action_search)
        searchView = searchMenu.actionView as SearchView

        searchView.imeOptions = EditorInfo.IME_FLAG_NO_FULLSCREEN

        lifecycleScope.launch {
            viewModel.isSearchExpanded.collectLatest { isExpanded ->
                if (isExpanded) {
                    Timber.d("$isExpanded watching")
                    binding.appBarMain.searchCharacter.pagedData = viewModel.pagingFlow
                    binding.appBarMain.searchCharacter.navController = navController
                    binding.appBarMain.searchCharacter.visibility = View.VISIBLE
                    searchView.isIconified = false
                    searchView.requestFocus()
                    viewModel.setPreviouslySearchedCharacter(searchView)
                } else {
                    binding.appBarMain.searchCharacter.visibility = View.GONE
                    searchView.clearFocus()
                    searchView.isIconified = true
                }
            }
        }
        search()
        return true
    }

    private fun search() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    viewModel.saveLastCharacterSearch(query)
                }
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) {
                    viewModel.updatedSearch(
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
            Timber.d("$hasFocus")
            viewModel.saveIsSearchExpandedState(hasFocus)
        }
    }

    override fun onSupportNavigateUp() =
        navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    private val navController by lazy {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navHostFragment.navController
    }

    private fun loadingBar(pbLoading: ComposeView) {
        pbLoading.setContent {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                color = Color.Green,
                strokeWidth = 7.dp

            )
        }
        viewModel.loadingState(pbLoading)
    }

    /*private fun reconnect() {
        viewModel.networkObserve.observe(this) { networkState ->
            when (networkState) {
                NetworkStatus.Available -> {
                    viewModel.loadPages(binding.appBarMain.searchCharacter)

                }
                NetworkStatus.Unavailable -> {
                    viewModel.loadPages(binding.appBarMain.searchCharacter)
                }
            }
        }
    }*/
}
