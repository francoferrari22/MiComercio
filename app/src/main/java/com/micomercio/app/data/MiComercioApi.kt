package com.micomercio.app.data

import retrofit2.Response
import retrofit2.http.*

interface MiComercioApi {
    suspend fun exportBackupJson(): String
    suspend fun importBackupJson(json: String): Boolean
    @GET("api/mobile/ping") suspend fun ping(): Response<Map<String,Any>>
    @GET("api/mobile/resumen") suspend fun summary(): Response<Summary>
    @GET("api/mobile/medios-pago") suspend fun paymentMethods(): Response<List<String>>
    @GET("api/mobile/productos") suspend fun products(@Query("q") q:String=""): Response<List<Product>>
    @GET("api/mobile/categorias") suspend fun categories(): Response<List<String>>
    @POST("api/mobile/categorias") suspend fun createCategory(@Body body:CategoryWrite): Response<ApiResult>
    @GET("api/mobile/productos/{id}") suspend fun product(@Path("id") id:Int): Response<Product>
    @GET("api/mobile/producto-comun") suspend fun commonProduct(): Response<CommonProductInfo>
    @POST("api/mobile/productos") suspend fun createProduct(@Body body:ProductWrite): Response<ApiResult>
    @PUT("api/mobile/productos/{id}") suspend fun updateProduct(@Path("id") id:Int,@Body body:ProductWrite): Response<ApiResult>
    @PUT("api/mobile/productos/{id}/proveedor") suspend fun assignSupplier(@Path("id") id:Int,@Body body:SupplierAssignWrite): Response<ApiResult>
    @PUT("api/mobile/productos/{id}/precio") suspend fun updatePrice(@Path("id") id:Int,@Body body:PriceUpdate): Response<ApiResult>
    @POST("api/mobile/productos/{id}/stock") suspend fun stock(@Path("id") id:Int,@Body body:StockUpdate): Response<ApiResult>
    @HTTP(method="DELETE",path="api/mobile/productos/{id}",hasBody=true) suspend fun deleteProduct(@Path("id") id:Int,@Body body:Map<String,String>): Response<ApiResult>
    @GET("api/mobile/clientes") suspend fun customers(): Response<List<Customer>>
    @POST("api/mobile/clientes") suspend fun createCustomer(@Body body:CustomerWrite): Response<ApiResult>
    @PUT("api/mobile/clientes/{id}") suspend fun updateCustomer(@Path("id") id:Int,@Body body:CustomerWrite): Response<ApiResult>
    @HTTP(method="DELETE",path="api/mobile/clientes/{id}",hasBody=true) suspend fun deleteCustomer(@Path("id") id:Int,@Body body:Map<String,String>): Response<ApiResult>
    @POST("api/mobile/clientes/{id}/abono") suspend fun customerPayment(@Path("id") id:Int,@Body body:CustomerPayment): Response<ApiResult>
    @GET("api/mobile/clientes/{id}/cuenta") suspend fun customerAccount(@Path("id") id:Int): Response<CustomerAccountResponse>
    @POST("api/mobile/clientes/{id}/cuenta/limpiar") suspend fun clearCustomerAccountHistory(@Path("id") id:Int): Response<ApiResult>
    @GET("api/mobile/ventas") suspend fun sales(): Response<List<Sale>>
    @POST("api/mobile/ventas") suspend fun createSale(@Body body:SaleWrite): Response<SaleResult>
    @POST("api/mobile/ventas-pendientes") suspend fun sendPendingSale(@Body body:SalePendingWrite): Response<ApiResult>
    @GET("api/mobile/ventas/{id}/ticket") suspend fun ticket(@Path("id") id:Long): Response<TicketResult>
    @POST("api/mobile/devoluciones/{saleItemId}") suspend fun returnItem(@Path("saleItemId") saleItemId:Long,@Body body:ReturnWrite): Response<ApiResult>
    @POST("api/mobile/ventas/{id}/cancelar") suspend fun cancelSale(@Path("id") id:Long,@Body body:CancelWrite): Response<ApiResult>
    @GET("api/mobile/caja") suspend fun cash(): Response<CashStatus>
    @GET("api/mobile/usuarios") suspend fun mobileUsers(): Response<List<MobileUser>>
    @GET("api/mobile/usuario/actual") suspend fun currentMobileUser(): Response<MobileUser>
    @POST("api/mobile/usuario/cambiar") suspend fun switchMobileUser(@Body body:MobileUserSwitch): Response<ApiResult>
    suspend fun authenticateUser(username:String,password:String): Response<ApiResult>
    suspend fun createMobileUser(body:MobileUserWrite): Response<ApiResult>
    suspend fun updateMobileUser(id:Int,body:MobileUserWrite): Response<ApiResult>
    suspend fun deleteMobileUser(id:Int): Response<ApiResult>
    @GET("api/arqueo") suspend fun arqueo(): Response<CashArqueo>
    @GET("api/mobile/config") suspend fun config(): Response<MobileConfig>
    @POST("api/mobile/caja/apertura") suspend fun openCash(@Body body:CashOpen): Response<ApiResult>
    @POST("api/mobile/caja/movimiento") suspend fun cashMovement(@Body body:CashMovement): Response<ApiResult>
    @POST("api/mobile/caja/cierre") suspend fun closeCash(@Body body:CashClose): Response<ApiResult>
    @GET("api/mobile/promociones") suspend fun promotions(): Response<List<Promotion>>
    @POST("api/mobile/promociones") suspend fun createPromotion(@Body body:PromotionWrite): Response<ApiResult>
    @PUT("api/mobile/promociones") suspend fun updatePromotion(@Body body:PromotionWrite): Response<ApiResult>
    @HTTP(method="DELETE",path="api/mobile/promociones/{id}",hasBody=true) suspend fun deletePromotion(@Path("id") id:Long,@Body body:Map<String,String>): Response<ApiResult>
    @GET("api/mobile/salones") suspend fun salons(): Response<List<SalonInfo>>
    @GET("api/mobile/mesas") suspend fun tables(@Query("salonId") salonId:Int?=null): Response<List<TableInfo>>
    @POST("api/mobile/mesas") suspend fun createTable(@Body body:TableWrite): Response<ApiResult>
    @PUT("api/mobile/mesas/{tableId}") suspend fun updateTable(@Path("tableId") tableId:Int,@Body body:TableWrite): Response<ApiResult>
    @HTTP(method="DELETE",path="api/mobile/mesas/{tableId}",hasBody=true) suspend fun deleteTable(@Path("tableId") tableId:Int,@Body body:Map<String,String>): Response<ApiResult>
    @GET("api/mobile/tickets-abiertos") suspend fun openTickets(): Response<List<OpenTicket>>
    @POST("api/mobile/tickets-abiertos") suspend fun saveOpenTicket(@Body body:OpenTicket): Response<ApiResult>
    @DELETE("api/mobile/tickets-abiertos/{tableId}") suspend fun deleteOpenTicket(@Path("tableId") tableId:Int): Response<ApiResult>
    @POST("api/mobile/mesas/{tableId}/liberar") suspend fun releaseTable(@Path("tableId") tableId:Int): Response<ApiResult>
    @POST("api/mobile/mesas/{tableId}/cobrar") suspend fun chargeTable(@Path("tableId") tableId:Int,@Body body:TableChargeWrite): Response<SaleResult>
    @POST("api/mobile/tickets-abiertos/{tableId}/agregar") suspend fun appendOpenTicket(@Path("tableId") tableId:Int,@Body body:OpenTicket): Response<ApiResult>
    @GET("api/mobile/analitica") suspend fun analytics(): Response<AnalyticsData>
    @GET("api/mobile/corte-z") suspend fun generalZ(): Response<GeneralZReport>
    @GET("api/mobile/proveedores") suspend fun suppliers(): Response<List<Supplier>>
    @GET("api/mobile/proveedores/{id}/productos") suspend fun supplierProducts(@Path("id") id:Int,@Query("q") q:String=""): Response<List<SupplierProduct>>
    @POST("api/mobile/proveedores") suspend fun createSupplier(@Body body:SupplierWrite): Response<ApiResult>
    @PUT("api/mobile/proveedores/{id}") suspend fun updateSupplier(@Path("id") id:Int,@Body body:SupplierWrite): Response<ApiResult>
    @HTTP(method="DELETE",path="api/mobile/proveedores/{id}",hasBody=true) suspend fun deleteSupplier(@Path("id") id:Int,@Body body:Map<String,String>): Response<ApiResult>
    @GET("api/mobile/compras") suspend fun purchases(): Response<List<Purchase>>
    @POST("api/mobile/compras") suspend fun createPurchase(@Body body:PurchaseWrite): Response<ApiResult>
    @DELETE("api/mobile/compras/{id}") suspend fun deletePurchase(@Path("id") id:Long): Response<ApiResult>
    @GET("api/mobile/compras/{id}") suspend fun purchaseDetail(@Path("id") id:Long): Response<PurchaseDetail>
    @PUT("api/mobile/compras/{id}") suspend fun updatePurchase(@Path("id") id:Long,@Body body:PurchaseWrite): Response<ApiResult>
    @POST("api/mobile/compras/{id}/recibir") suspend fun receivePurchase(@Path("id") id:Long,@Body body:PurchaseReceiveWrite): Response<ApiResult>
    @GET("api/mobile/movimientos-caja") suspend fun cashMovements(): Response<List<Map<String,Any>>>
    @GET("api/mobile/movimientos-stock") suspend fun stockMovements(): Response<List<Map<String,Any>>>
    @POST("api/mobile/movimientos-caja/{id}/anular") suspend fun voidCashMovement(@Path("id") id:Long,@Body body:Map<String,String>): Response<ApiResult>
    @GET("api/mobile/reportes-detallados") suspend fun detailedReports(): Response<DetailedReportData>
    @GET("api/mobile/auditoria") suspend fun auditLog(): Response<List<AuditRow>>
    @GET("api/mobile/vincular/{code}") suspend fun pairingShortCode(@Path("code",encoded=true) code:String): Response<PairingData>
}

