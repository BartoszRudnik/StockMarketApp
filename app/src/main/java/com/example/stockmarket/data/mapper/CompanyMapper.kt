package com.example.stockmarket.data.mapper

import com.example.stockmarket.data.local.CompanyListingEntity
import com.example.stockmarket.data.remote.dto.CompanyInfoDto
import com.example.stockmarket.domain.model.CompanyInfo
import com.example.stockmarket.domain.model.CompanyListingModel

fun CompanyListingEntity.toCompanyListingModel(): CompanyListingModel {
    return CompanyListingModel(
        name, symbol, exchange
    )
}

fun CompanyListingModel.toCompanyListingEntity(): CompanyListingEntity {
    return CompanyListingEntity(
        name, symbol, exchange
    )
}

fun CompanyInfoDto.toCompany(): CompanyInfo {
    return CompanyInfo(
        symbol = symbol ?: "",
        description = description ?: "",
        name = name ?: "",
        country = country ?: "",
        industry = industry ?: "",
    )
}