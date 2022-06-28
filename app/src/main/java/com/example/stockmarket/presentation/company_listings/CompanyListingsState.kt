package com.example.stockmarket.presentation.company_listings

import com.example.stockmarket.domain.model.CompanyListingModel

data class CompanyListingsState(
    val companies: List<CompanyListingModel> = emptyList(),
    val loading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
)
