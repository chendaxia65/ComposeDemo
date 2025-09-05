package com.servicebio.compose.application.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.servicebio.compose.application.MainViewModel
import com.servicebio.compose.application.component.BottomNavigationBar
import com.servicebio.compose.application.component.PagerScaffold
import com.servicebio.compose.application.model.TopLevelDestination
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    val scope = rememberCoroutineScope()
    val currentPageIndex by viewModel.currentPageIndex.collectAsState()

    val pageSata = rememberPagerState(currentPageIndex) { TopLevelDestination.entries.size }

//    LaunchedEffect(pageSata.currentPage) {
//        viewModel.updateDestination(pageSata.currentPage)
//        pageSata.animateScrollToPage(pageSata.currentPage)
//    }

    PagerScaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(
                destinations = TopLevelDestination.entries,
                onNavigate2Destination = {
                    viewModel.updateDestination(it)
                    scope.launch { pageSata.animateScrollToPage(it) }
                },
                currentDestinationIndex = currentPageIndex,
            )
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pageSata,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            beyondViewportPageCount = 1
        ) { index ->
            key(index) {
                when (index) {
                    0 -> HomeScreen(navController)
                    1 -> ConversationListScreen(navController)
                    2 -> CartScreen()
                    3 -> MeScreen()
                }
            }
        }
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen() {

    PagerScaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(text = "购物车")
            })
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text("Cart", modifier = Modifier.align(Alignment.TopCenter))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeScreen() {

    PagerScaffold(
        topBar = {},
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                "Me",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(WindowInsets.statusBars.asPaddingValues())
            )
        }
    }
}