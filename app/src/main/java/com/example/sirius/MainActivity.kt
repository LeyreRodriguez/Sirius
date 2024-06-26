package com.example.sirius

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.sirius.model.User
import com.example.sirius.ui.theme.SiriusTheme
import com.example.sirius.navigation.NavigationController
import com.example.sirius.view.screens.OwnerScreen
import com.example.sirius.viewmodel.AnimalViewModel
import com.example.sirius.viewmodel.ChatViewModel
import com.example.sirius.viewmodel.NewsViewModel
import com.example.sirius.viewmodel.ShelterViewModel
import com.example.sirius.viewmodel.UserViewModel
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by lazy {
        val animalApplication = application as AnimalApplication
        animalApplication.initContext(applicationContext)
        UserViewModel((application as AnimalApplication).userDao)
    }

    private val animalViewModel: AnimalViewModel by lazy {
        val animalApplication = application as AnimalApplication
        animalApplication.initContext(applicationContext)
        AnimalViewModel((application as AnimalApplication).animalDao)
    }

    private val newsViewModel: NewsViewModel by lazy {
        val animalApplication = application as AnimalApplication
        animalApplication.initContext(applicationContext)
        NewsViewModel((application as AnimalApplication).newsDao)
    }

    private val chatViewModel: ChatViewModel by lazy {
        val animalApplication = application as AnimalApplication
        animalApplication.initContext(applicationContext)
        ChatViewModel((application as AnimalApplication).chatDao, userViewModel)
    }

    private val shelterViewModel: ShelterViewModel by lazy {
        val animalApplication = application as AnimalApplication
        animalApplication.initContext(applicationContext)
        ShelterViewModel((application as AnimalApplication).shelterDao)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = applicationContext.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

        setContent {
            SiriusTheme {
                NavigationController(userViewModel, animalViewModel, newsViewModel, chatViewModel, shelterViewModel)
                //LandingPage()
                //MyComposable(viewModel = viewModel)
            }
        }

        lifecycleScope.launchWhenCreated {
            if (userViewModel.currentUser.value == null) {
                val userInfoJson = sharedPreferences.getString("user_info", null)
                if (userInfoJson != null) {
                    val user = Gson().fromJson(userInfoJson, User::class.java)
                    userViewModel.login(user.username, user.password)
                }
            }
        }
    }

    /*private val userViewModel: UserViewModel by lazy {
        val animalApplication = application as AnimalApplication
        animalApplication.initContext(applicationContext)
        UserViewModel((application as AnimalApplication).userDao)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Establece el contenido de la actividad utilizando Jetpack Compose
        setContent {
            // Llama a la función que contiene la vista principal
            OwnerScreen(viewModel = userViewModel)
        }
    }*/
}
