package com.theworld.androidtemplatewithnavcomponents

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.navigation.NavigationView
import com.theworld.androidtemplatewithnavcomponents.utils.*
import com.theworld.androidtemplatewithnavcomponents.databinding.ActivityMainBinding
import com.theworld.androidtemplatewithnavcomponents.utils.redirectToDestination
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// Must change Package name first

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener,
    NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private val context = this
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sharedPrefManager = SharedPrefManager(context)

        init()
        clickListeners()
        manageHeaderView()
    }


    /*---------------------------------------------- Init -----------------------------------------------------*/

    private fun init() {

        setSupportActionBar(binding.includeContent.toolbarInclude.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController


        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.dashboardFragment,
//                R.id.profileFragment,
            ), binding.drawer
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.navView.setupWithNavController(navController)
        binding.navView.setNavigationItemSelectedListener(this)

        navController.addOnDestinationChangedListener(this)

        binding.includeContent.toolbarInclude.toolbar.setOnClickListener {
            backStackList()
        }


    }


    /*--------------------------------- Manage header View -----------------------------------------*/


    private fun manageHeaderView() {
        val header: View = binding.navView.getHeaderView(0)
        val tv = header.findViewById<TextView>(R.id.tvUserName)
        val imageView = header.findViewById<ImageView>(R.id.profileImage)

        if (context.isLogin()) {
            tv.text = sharedPrefManager.getString("name")
            val myUrl = sharedPrefManager.getString("profile_image")
            imageView.load(

                if (myUrl.isValidUrl()) {
                    myUrl
                } else {
                    imageUrl + myUrl
                }

            ) {
                crossfade(true)
//                placeholder(R.drawable.ic_la)
//                error(R.drawable.ic_la)
                transformations(
                    CircleCropTransformation()
                )
            }

        } else {
            tv.text = "Login"

            tv.setOnClickListener {
                navController.navigate(R.id.login_graph)
            }
        }


//        val menuOpen = binding.navView.menu
//        menuOpen.findItem(R.id.logout).isVisible = context.isLogin()
//        menuOpen.findItem(R.id.profileFragment).isVisible = context.isLogin()


    }


    /*----------------------------------------- Click Listeners -------------------------------*/

    private fun clickListeners() {


    }

    /*---------------------------------------------- On Navigate Up -----------------------------------------------------*/

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    /*-------------------------------------------- On Destination Changed -----------------------------------------------------*/


    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {

            R.id.loginFragment,
            R.id.registerFragment,
//            R.id.playerFragment,
//            -> {
//                displayToolbar(false)
//                manageDrawer(false)
//            }

            R.id.profileFragment
            -> {
                displayToolbar(true)
                manageDrawer(true)
            }

            R.id.dashboardFragment -> {
                manageHeaderView()
                displayToolbar(true)
                manageDrawer(false)
            }

            else -> {
                displayToolbar(true)
                manageDrawer(false)
            }
        }
    }


    /*-------------------------------------------- Display Toolbar -----------------------------------------------------*/

    private fun displayToolbar(isVisible: Boolean) {
        binding.includeContent.toolbarInclude.toolbar.isVisible = isVisible
    }


    /*-------------------------------------------- Handle Drawer -----------------------------------------------------*/

    private fun manageDrawer(wantToLock: Boolean = false) {

        binding.drawer.closeDrawers()

        if (wantToLock) {
            binding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } else {
            binding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }

    }


    /*-------------------------------------------- BackStack List -----------------------------------------------------*/

    @SuppressLint("RestrictedApi")
    private fun backStackList() {
        val stacks = mutableListOf<String>()
        stacks.clear()
        navController.backStack.forEach {
            stacks.add(it.destination.displayName)
        }


        for (stack in stacks) {
            Log.d(TAG, "backStackList: $stack")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        return if (item.itemId == R.id.logout) {
            sharedPrefManager.clear()
            manageHeaderView()
            navController.redirectToDestination(R.id.dashboardFragment)
            true
        } else {
            item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
        }
    }


}