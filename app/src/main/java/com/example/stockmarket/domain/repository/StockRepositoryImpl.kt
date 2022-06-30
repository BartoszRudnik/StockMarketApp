package com.example.stockmarket.domain.repository

import com.example.stockmarket.data.csv.CsvParser
import com.example.stockmarket.data.csv.IntradayInfoParser
import com.example.stockmarket.data.local.StockDatabase
import com.example.stockmarket.data.mapper.toCompany
import com.example.stockmarket.data.mapper.toCompanyListingEntity
import com.example.stockmarket.data.mapper.toCompanyListingModel
import com.example.stockmarket.data.remote.StockApi
import com.example.stockmarket.domain.model.CompanyInfo
import com.example.stockmarket.domain.model.CompanyListingModel
import com.example.stockmarket.domain.model.IntradayInfo
import com.example.stockmarket.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val db: StockDatabase,
    private val parser: CsvParser<CompanyListingModel>,
    private val intradayInfoParser: CsvParser<IntradayInfo>,
) : StockRepository {
    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListingModel>>> {
        return flow {
            emit(Resource.Loading(isLoading = true))

            val localListings = dao.searchCompanyListing(query)

            emit(Resource.Success(data = localListings.map { localListing ->
                localListing.toCompanyListingModel()
            }))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote

            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteListings = try {
                val response = api.getListings()

                parser.parse(response.byteStream())
            } catch (e: IOException) {
                emit(Resource.Error("Couldn't load the data"))
                null
            } catch (e: HttpException) {
                emit(Resource.Error("Couldn't load the data"))
                null
            }

            remoteListings?.let { listings ->
                dao.clearCompanyListings()
                dao.insertCompanyListing(listings.map {
                    it.toCompanyListingEntity()
                })

                emit(Resource.Success(listings))
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>> {
        return try {
            val response = api.getIntradayInfo(symbol)

            val results = intradayInfoParser.parse(response.byteStream())

            Resource.Success(results)
        } catch (e: IOException) {
            Resource.Error(message = "Couldn't get intraday info")
        } catch (e: HttpException) {
            Resource.Error(message = "Couldn't get intraday info")
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try {
            val response = api.getCompanyInfo(symbol)

            Resource.Success(response.toCompany())
        } catch (e: IOException) {
            Resource.Error(message = "Couldn't get company info")
        } catch (e: HttpException) {
            Resource.Error(message = "Couldn't get company info")
        }
    }
}