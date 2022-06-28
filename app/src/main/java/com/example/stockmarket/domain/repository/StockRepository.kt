package com.example.stockmarket.domain.repository

import com.example.stockmarket.domain.model.CompanyListingModel
import com.example.stockmarket.utils.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String,
    ): Flow<Resource<List<CompanyListingModel>>>
}