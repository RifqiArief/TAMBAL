package com.example.kelompok2.Repositories

import com.example.kelompok2.DataModels.CarBrandModel
import com.example.kelompok2.DataModels.CarModel
import com.example.kelompok2.DataModels.ServiceModel
import com.example.kelompok2.DataModels.ResultModel
import com.example.kelompok2.DataModels.UserModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.sql.Date

class DatabaseRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun saveUserData(userId: String, fullName: String, email: String): ResultModel<Unit> {
        return try {
            val userData = UserModel(userId, fullName, email)
            db.collection("Users").document(userId).set(userData).await()
            ResultModel.Success(Unit)

        } catch (e: Exception) {
            ResultModel.Error(e)
        }
    }

    suspend fun fetchUserData(userId: String): ResultModel<UserModel> {
        return try {
            val result = db.collection("Users").document(userId).get().await()
            val user = result.toObject(UserModel::class.java)
            if (user != null) {
                ResultModel.Success(user)
            } else {
                ResultModel.Error(Exception("User not found"))
            }
        } catch (e: Exception) {
            ResultModel.Error(e)
        }
    }

    suspend fun fetchCarBrands(): ResultModel<List<CarBrandModel>> {
        return try {
            val result = db.collection("CarBrands").get().await()
            val brands = result.toObjects(CarBrandModel::class.java)
            ResultModel.Success(brands)
        } catch (e: Exception) {
            ResultModel.Error(e)
        }
    }

    suspend fun fetchAllCars(): ResultModel<List<CarModel>> {
        return try {
            val result = db.collection("Cars").get().await()
            val cars = result.toObjects(CarModel::class.java)
            ResultModel.Success(cars)
        } catch (e: Exception) {
            ResultModel.Error(e)
        }
    }

    suspend fun saveCarService(
        userId: String, carId: String, serviceDate: Date, expectedReturnDate: Date)
    : ResultModel<Unit> {
        return try {
            val serviceId = db.collection("ServiceHistory").document().id
            val serviceData = ServiceModel(serviceId, userId, carId, serviceDate, expectedReturnDate)

            db.collection("ServiceHistory").document(serviceId).set(serviceData).await()
            db.collection("Users").document(userId).update("ServiceHistory", FieldValue.arrayUnion(serviceId)).await()

            ResultModel.Success(Unit)

        } catch (e: Exception) {
            ResultModel.Error(e)
        }
    }

    suspend fun fetchMostRecentService(servisId: String): ResultModel<ServiceModel> {
        return try {
            val dbResult = db.collection("ServiceHistory").document(servisId).get().await()
            val serviceData = dbResult.toObject(ServiceModel::class.java)

            if (serviceData != null) {
                ResultModel.Success(serviceData)
            } else {
                ResultModel.Error(Exception("Null data"))
            }

        } catch (e: Exception) {
            ResultModel.Error(e)
        }
    }

    suspend fun fetchCarDetails(carId: String): ResultModel<CarModel> {
        return try {
            val dbResult = db.collection("Cars").document(carId).get().await()
            val car = dbResult.toObject(CarModel::class.java)
            if (car != null) {
                ResultModel.Success(car)
            } else {
                ResultModel.Error(Exception("Car not found"))
            }
        } catch (e: Exception) {
            ResultModel.Error(e)
        }
    }
 }
