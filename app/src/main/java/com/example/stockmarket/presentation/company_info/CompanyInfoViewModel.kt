package com.example.stockmarket.presentation.company_info

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockmarket.domain.repository.StockRepository
import com.example.stockmarket.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: StockRepository,
) : ViewModel() {
    var state by mutableStateOf(
        CompanyInfoState()
    )

    init {
        viewModelScope.launch {
            val symbol = savedStateHandle.get<String>("symbol") ?: return@launch

            state = state.copy(isLoading = true)

            val companyInfoResult = async { repository.getCompanyInfo(symbol) }
            val intradayInfoResult = async { repository.getIntradayInfo(symbol) }

            when (val result = companyInfoResult.await()) {
                is Resource.Error -> {
                    state = state.copy(
                        isLoading = false, errorMessage = result.message, company = null
                    )
                }
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    state = state.copy(
                        company = result.data, isLoading = false, errorMessage = null
                    )
                }
            }

            when (val result = intradayInfoResult.await()) {
                is Resource.Error -> {
                    state = state.copy(
                        isLoading = false, errorMessage = result.message, stockInfos = emptyList()
                    )
                }
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    state = state.copy(
                        stockInfos = result.data ?: emptyList(),
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
        }
    }
}