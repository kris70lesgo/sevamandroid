package com.sevam.core.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface SevamApiService {
    @GET("services/catalog")
    suspend fun fetchCatalog(): Response<ServiceCatalogResponseDto>

    @POST("auth/sync-profile")
    suspend fun syncProfile(
        @Header("Authorization") authorization: String,
        @Body body: SyncProfileRequestDto,
    ): Response<SyncProfileResponseDto>

    @GET("customer/profile")
    suspend fun fetchProfile(): Response<CustomerProfileResponseDto>

    @PUT("customer/profile")
    suspend fun updateProfile(
        @Body body: CustomerProfileUpdateRequestDto,
    ): Response<ResponseBody>

    @GET("customer/addresses")
    suspend fun fetchAddresses(): Response<AddressesResponseDto>

    @POST("customer/addresses")
    suspend fun createAddress(
        @Body body: AddressUpsertRequestDto,
    ): Response<AddressMutationResponseDto>

    @PUT("customer/addresses")
    suspend fun updateAddress(
        @Body body: AddressUpsertRequestDto,
    ): Response<AddressMutationResponseDto>

    @DELETE("customer/addresses")
    suspend fun deleteAddress(
        @Query("id") addressId: String,
    ): Response<ResponseBody>

    @GET("customer/orders")
    suspend fun fetchOrders(): Response<OrdersResponseDto>

    @GET("location/search")
    suspend fun searchLocations(
        @Query("q") query: String,
    ): Response<List<LocationSuggestionDto>>

    @GET("location/reverse")
    suspend fun reverseGeocode(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
    ): Response<ReverseLocationDto>

    @POST("razorpay/checkout/order")
    suspend fun createCheckoutOrder(
        @Body body: CreateOrderRequestDto,
    ): Response<CreateOrderResponseDto>

    @POST("razorpay/checkout/verify")
    suspend fun verifyPayment(
        @Body body: VerifyPaymentRequestDto,
    ): Response<VerifyPaymentResponseDto>
}
