package com.example.stockmarket.domain.repository

import com.example.stockmarket.data.csv.CsvParser
import com.example.stockmarket.data.local.StockDatabase
import com.example.stockmarket.data.mapper.toCompanyListingEntity
import com.example.stockmarket.data.mapper.toCompanyListingModel
import com.example.stockmarket.data.remote.StockApi
import com.example.stockmarket.domain.model.CompanyListingModel
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
) : StockRepository {
    private val dao = db.dao;

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
}