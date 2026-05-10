package com.yarik.listdetail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.savedstate.serialization.SavedStateConfiguration
import com.yarik.listdetail.navigation.ListScreenKey
import com.yarik.listdetail.ui.theme.ListDetailTheme
import androidx.navigation3.ui.NavDisplay
import com.yarik.listdetail.navigation.DetailsScreenKey
import com.yarik.listdetail.ui.screens.DetailScreen
import com.yarik.listdetail.ui.screens.ListScreen
import com.yarik.listdetail.ui.viewmodels.SharedViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val backStack = rememberNavBackStack(
                configuration = SavedStateConfiguration {
                    serializersModule = SerializersModule {
                        polymorphic(NavKey::class) {
                            subclass(ListScreenKey::class, ListScreenKey.serializer())
                        }
                        polymorphic(NavKey::class) {
                            subclass(DetailsScreenKey::class, DetailsScreenKey.serializer())
                        }
                    }
                },
                ListScreenKey
            )

            val sharedViewModel: SharedViewModel = viewModel()

            ListDetailTheme {
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    entryProvider = entryProvider {
                        entry<ListScreenKey> {
                            ListScreen(
                                navigateToDetails = { id -> backStack.add(DetailsScreenKey(id)) },
                                sharedViewModel = sharedViewModel
                            )
                        }
                        entry<DetailsScreenKey> { key ->
                            DetailScreen(
                                itemId = key.noteId,
                                sharedViewModel = sharedViewModel
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ListDetailTheme {
        Greeting("Android")
    }
}