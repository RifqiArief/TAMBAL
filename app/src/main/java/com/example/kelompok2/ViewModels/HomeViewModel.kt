package com.example.kelompok2.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kelompok2.DataModels.CarBrandModel
import com.example.kelompok2.DataModels.CarModel
import com.example.kelompok2.DataModels.ServiceModel
import com.example.kelompok2.DataModels.ResultModel
import com.example.kelompok2.DataModels.UserModel
import com.example.kelompok2.Repositories.AuthenticationRepository
import com.example.kelompok2.Repositories.DatabaseRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val dbRepo: DatabaseRepository,
    private val authRepo: AuthenticationRepository
)
    : ViewModel() {

    private val _userData = MutableLiveData<UserModel>()
    val userData: LiveData<UserModel> get() = _userData

    private val _carBrands = MutableLiveData<List<CarBrandModel>>()
    val carBrands: LiveData<List<CarBrandModel>> get() = _carBrands

    private val _mostRecentService = MutableLiveData<Pair<ServiceModel, CarModel>>()
    val mostRecentService: LiveData<Pair<ServiceModel, CarModel>> get() = _mostRecentService

    private val _popularCars = MutableLiveData<List<CarModel>>()
    val popularCars: LiveData<List<CarModel>> get() = _popularCars

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchUserData() {
        viewModelScope.launch {
            val userResult = dbRepo.fetchUserData(authRepo.getCurrentUser()!!.uid)
            Log.d("fetchUserData", authRepo.getCurrentUser()!!.uid)
            when (userResult) {
                is ResultModel.Success -> {
                    _userData.value = userResult.data
                    try{
                        fetchMostRecentService(userResult.data.Service[0])
                    }
                    catch (_: Exception){}
                }
                is ResultModel.Error -> _error.value += "FetchUserData: ${userResult.exception.message}"
            }

        }
    }

    fun fetchCarBrands() {
        viewModelScope.launch {
            val dbResult = dbRepo.fetchCarBrands()
            when (dbResult) {
                is ResultModel.Success -> _carBrands.value = dbResult.data
                is ResultModel.Error -> _error.value += "FetchCarBrands: ${dbResult.exception.message} \n"
            }
        }
    }

    fun fetchPopularCars() {
        viewModelScope.launch {
            val dbResult = dbRepo.fetchAllCars()
            when (dbResult) {
                is ResultModel.Success -> _popularCars.value = dbResult.data.take(3)
                is ResultModel.Error -> _error.value += "FetchPopularCars: ${dbResult.exception.message} \n"
            }
        }
    }

    private fun fetchMostRecentService(serviceId: String) {
        viewModelScope.launch {
            val serviceResult = dbRepo.fetchMostRecentService(serviceId)
            when (serviceResult) {
                is ResultModel.Success -> {
                    val service = serviceResult.data
                    Log.d("fetchMostRecentService", service.toString())
                    val carResult = dbRepo.fetchCarDetails(service.carId)
                    when (carResult) {
                        is ResultModel.Success -> {
                            val car = carResult.data
                            _mostRecentService.value = Pair(service, car)
                        }

                        is ResultModel.Error -> _error.value += "FetchCarDetails: ${carResult.exception.message} \n"
                    }
                }

                is ResultModel.Error -> _error.value += "FetchMostRecentService: ${serviceResult.exception.message} \n"
            }
        }
    }
}


class HomeViewModelFactory(
    private val dbRepo: DatabaseRepository,
    private val authRepo: AuthenticationRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(dbRepo, authRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}