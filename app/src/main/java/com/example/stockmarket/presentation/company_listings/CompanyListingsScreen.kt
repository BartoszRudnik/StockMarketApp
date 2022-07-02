package com.example.stockmarket.presentation.company_listings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stockmarket.presentation.destinations.CompanyInfoScreenDestination
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination(start = true)
fun CompanyListingsScreen(
    navigator: DestinationsNavigator,
    viewModel: CompanyListingsViewModel = hiltViewModel(),
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.state.isRefreshing)
    val state = viewModel.state

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(), maxLines = 1,
            singleLine = true,
            value = state.searchQuery,
            onValueChange = { newQuery ->
                viewModel.onEvent(CompanyListingsEvent.OnSearchQueryChange(newQuery))
            })
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.onEvent(CompanyListingsEvent.Refresh) }) {
            LazyColumn(modifier = Modifier.fillMaxHeight()) {
                items(state.companies.size) { index ->
                    Column {
                        CompanyItem(
                            company = state.companies[index],
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navigator.navigate(
                                        CompanyInfoScreenDestination(symbol = state.companies[index].symbol)
                                    )
                                }
                                .padding(8.dp)
                        )
                        if (index < state.companies.size) {
                            Divider(modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }
    }
}