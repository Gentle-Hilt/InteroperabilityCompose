package gentle.hilt.interop.ui.interop

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import gentle.hilt.interop.R
import gentle.hilt.interop.databinding.ActivityInteropBinding
import gentle.hilt.interop.databinding.NavHeaderInteropBinding
import gentle.hilt.interop.ui.settings.theme.robotoFontFamily
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InteropActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityInteropBinding
    private val viewModel: InteropViewModel by viewModels()
    private val startActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    private lateinit var searchView: SearchView
    private lateinit var searchMenu: MenuItem
    private lateinit var settingsMenu: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityInteropBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_settings
            ),
            binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navGraph.setupWithNavController(navController)

        backFromNavigationDrawerImplementation()
        redirectToApiWebsiteDialog()
        loadingBar(binding.appBarMain.pbLoading)
        launchAppFirstTimeDialog()
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
        settingsMenu = binding.appBarMain.toolbar.menu.findItem(R.id.action_settings)
        searchMenu = binding.appBarMain.toolbar.menu.findItem(R.id.action_search)
        searchView = searchMenu.actionView as SearchView
        viewModel.observeMenuSearchState(binding.appBarMain.searchCharacter, navController, searchView)
        searchListener()
        drawerListener()
        settingsListener()
        return true
    }

    private fun drawerListener() {
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                searchView.clearFocus()
            }
        })
    }

    private fun settingsListener() {
        settingsMenu.setOnMenuItemClickListener {
            searchView.clearFocus()
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.nav_home, true)
                .build()
            navController.navigate(R.id.nav_settings, null, navOptions)
            true
        }
    }

    private fun searchListener() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) viewModel.saveLastCharacterSearch(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) viewModel.updatedSearch(newText)
                return true
            }
        })

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            viewModel.saveMenuSearchState(hasFocus)
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

    private fun redirectToApiWebsiteDialog() {
        val navHeaderBinding = NavHeaderInteropBinding.bind(binding.navGraph.getHeaderView(0))
        navHeaderBinding.apiLinkCompose.setContent {
            val showDialog = remember { mutableStateOf(false) }
            if (showDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    title = { Text(text = getString(R.string.redirect_tittle)) },
                    text = { Text(text = getString(R.string.redirect_message)) },
                    confirmButton = {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.api_link)))
                                startActivityLauncher.launch(intent)
                                showDialog.value = false
                            }
                        ) { Text(text = "Yes") }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDialog.value = false },
                            colors = ButtonDefaults.buttonColors(Color.Gray)
                        ) { Text(text = "No", color = Color.White) }
                    }
                )
            }

            Text(
                fontSize = 13.sp,
                text = resources.getString(R.string.api),
                color = Color.Cyan,
                modifier = Modifier.fillMaxWidth().clickable {
                    showDialog.value = true
                }
            )
        }
    }

    private fun launchAppFirstTimeDialog() {
        binding.appBarMain.firstTimeDialog.setContent {
            val showDialog = remember { mutableStateOf(false) }
            lifecycleScope.launch {
                val firstTime = viewModel.dataStore.readFirstTimeDialog.first()
                if (firstTime) {
                    showDialog.value = true
                    viewModel.dataStore.saveFirstTimeDialog(false)
                } else {
                    binding.appBarMain.firstTimeDialog.visibility = View.GONE
                    showDialog.value = false
                }
            }
            if (showDialog.value) {
                FirstTimeDialog(showDialog)
            }
        }
    }
}

@Composable
fun FirstTimeDialog(showDialog: MutableState<Boolean>) {
    if (showDialog.value) {
        Dialog(
            onDismissRequest = { showDialog.value = false },
            content = {
                Box(
                    Modifier
                        .background(Color.DarkGray, RoundedCornerShape(10))
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "This app was made to learn about Api " +
                                "calls, Pagination, Local and Network Caching, Handling Connectivity and" +
                                " Reconnection issues and properly display it to the user.\nAs the name says it's an interop: compose inside xml fragments." +
                                "\nI released it for developers to see performance with Compose.\nOverall it's my second app " +
                                "and first where i used compose, also i tested everything that is related to network.",
                            fontWeight = FontWeight.Light,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Start,
                            fontFamily = robotoFontFamily,
                            color = Color.White
                        )
                        Button(
                            colors = ButtonDefaults.buttonColors(Color.Gray),
                            onClick = { showDialog.value = false },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 10.dp)
                        ) {
                            Text(
                                text = "OK",
                                color = Color.White,
                                fontFamily = robotoFontFamily

                            )
                        }
                    }
                }
            }
        )
    }
}
