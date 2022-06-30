package com.example.stockmarket.di

import com.example.stockmarket.data.csv.CompanyListingsParser
import com.example.stockmarket.data.csv.CsvParser
import com.example.stockmarket.data.csv.IntradayInfoParser
import com.example.stockmarket.domain.model.CompanyListingModel
import com.example.stockmarket.domain.model.IntradayInfo
import com.example.stockmarket.domain.repository.StockRepository
import com.example.stockmarket.domain.repository.StockRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCompanyListingParser(companyListingsParser: CompanyListingsParser): CsvParser<CompanyListingModel>

    @Binds
    @Singleton
    abstract fun bindIntradayInfoParser(intradayInfoParser: IntradayInfoParser): CsvParser<IntradayInfo>

    @Binds
    @Singleton
    abstract fun bindStockRepository(stockRepositoryImpl: StockRepositoryImpl): StockRepository
}