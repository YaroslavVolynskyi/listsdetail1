package com.yarik.listdetail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yarik.listdetail.ui.screens.DetailScreen
import com.yarik.listdetail.ui.screens.ListScreen
import com.yarik.listdetail.ui.theme.ListDetailTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navigator = rememberListDetailPaneScaffoldNavigator<Long>()
            val coroutineScope = rememberCoroutineScope()
            val timeFromDetail = remember { mutableStateOf<Long?>(null) }

            ListDetailTheme {
                ListDetailPaneScaffold(
                    directive = navigator.scaffoldDirective,
                    value = navigator.scaffoldValue,
                    listPane = {
                        AnimatedPane {
                            ListScreen(
                                navigateToDetails = { id ->
                                    coroutineScope.launch {
                                        navigator.navigateTo(
                                            ListDetailPaneScaffoldRole.Detail,
                                            id
                                        )
                                    }
                                },
                                timeFromCallback = timeFromDetail.value,
                                onClearTimeFromCallback = { timeFromDetail.value = null }
                            )
                        }
                    },
                    detailPane = {
                        AnimatedPane {
                            navigator.currentDestination?.contentKey?.let { itemId ->
                                DetailScreen(
                                    itemId = itemId,
                                    onNavigateBack = {
                                        coroutineScope.launch { navigator.navigateBack() }
                                    },
                                    onNavigateBackWithTime = { seconds ->
                                        timeFromDetail.value = seconds
                                    }
                                )
                            }
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
