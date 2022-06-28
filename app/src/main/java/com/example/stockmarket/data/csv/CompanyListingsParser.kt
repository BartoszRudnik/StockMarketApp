package com.example.stockmarket.data.csv

import com.example.stockmarket.domain.model.CompanyListingModel
import com.opencsv.CSVReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompanyListingsParser @Inject constructor() : CsvParser<CompanyListingModel> {
    override suspend fun parse(stream: InputStream): List<CompanyListingModel> {
        val csvReader = CSVReader(InputStreamReader(stream))

        return withContext(Dispatchers.IO) {
            csvReader.readAll().drop(1).mapNotNull { line ->
                val symbol = line.getOrNull(0)
                val name = line.getOrNull(1)
                val exchange = line.getOrNull(2)

                CompanyListingModel(
                    name ?: return@mapNotNull null,
                    symbol ?: return@mapNotNull null,
                    exchange ?: return@mapNotNull null,
                )
            }.also {
                csvReader.close()
            }
        }
    }
}