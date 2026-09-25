package com.micomercio.app.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.security.MessageDigest
import kotlin.math.max

/**
 * Mi Comercio is deliberately offline-first: all operational data is persisted
 * inside the Android application's private storage. No PC, server, QR pairing,
 * cloud tunnel, server or computer installation is required.
 */
class LocalMiComercioApi(context: Context) : MiComercioApi {
    override suspend fun exportBackupJson(): String = db.backupJson()
    override suspend fun importBackupJson(json: String): Boolean = db.restoreBackupJson(json)
    private val db=LocalDb(context.applicationContext)

    override suspend fun ping():Response<Map<String,Any>> = Response.success(mapOf("ok" to true, "mode" to "LOCAL_ANDROID"))
    override suspend fun summary():Response<Summary> = Response.success(db.summary())
    override suspend fun paymentMethods():Response<List<String>> = Response.success(listOf("EFECTIVO","MERCADO PAGO","TARJETA","TRANSFERENCIA","CRÉDITO"))
    override suspend fun products(q:String):Response<List<Product>> = Response.success(db.products.filter { q.isBlank() || it.description.contains(q,true) || it.barcode.contains(q,true) })
    override suspend fun categories():Response<List<String>> = Response.success(db.categories.toList().sorted())
    override suspend fun createCategory(body:CategoryWrite):Response<ApiResult>{ if(body.name.isBlank()) return Response.success(ApiResult(false)); db.categories.add(body.name.trim());db.save();return Response.success(ApiResult(true)) }
    override suspend fun product(id:Int):Response<Product> = Response.success(db.products.firstOrNull{it.id==id} ?: Product(id=id))
    override suspend fun commonProduct():Response<CommonProductInfo> = Response.success(CommonProductInfo(-1,"__COMUN__","PRODUCTO COMÚN",true))
    override suspend fun createProduct(body:ProductWrite):Response<ApiResult>{val id=db.nextProductId();db.products.add(Product(id,body.barcode,body.description,body.salePrice,body.wholesalePrice,body.costPrice,body.stock,body.minStock,body.category,body.unit,true,body.bulk,body.usesInventory,body.iva21));db.save();return Response.success(ApiResult(true,id,body.salePrice))}
    override suspend fun updateProduct(id:Int,body:ProductWrite):Response<ApiResult>{val i=db.products.indexOfFirst{it.id==id};if(i<0)return Response.success(ApiResult(false));db.products[i]=db.products[i].copy(barcode=body.barcode,description=body.description,salePrice=body.salePrice,wholesalePrice=body.wholesalePrice,costPrice=body.costPrice,stock=body.stock,minStock=body.minStock,category=body.category,unit=body.unit,bulk=body.bulk,usesInventory=body.usesInventory,iva21=body.iva21);db.save();return Response.success(ApiResult(true,id.toInt()))}
    override suspend fun assignSupplier(id:Int,body:SupplierAssignWrite):Response<ApiResult>{val i=db.products.indexOfFirst{it.id==id};if(i>=0){db.products[i]=db.products[i].copy(supplierId=body.supplierId,supplierName=db.suppliers.firstOrNull{it.id==body.supplierId}?.name.orEmpty(),costPrice=if(body.unitCost>0)body.unitCost else db.products[i].costPrice);db.save()};return Response.success(ApiResult(i>=0))}
    override suspend fun updatePrice(id:Int,body:PriceUpdate):Response<ApiResult>{val i=db.products.indexOfFirst{it.id==id};if(i>=0){db.products[i]=db.products[i].copy(salePrice=body.salePrice);db.save()};return Response.success(ApiResult(i>=0))}
    override suspend fun stock(id:Int,body:StockUpdate):Response<ApiResult>{val i=db.products.indexOfFirst{it.id==id};if(i<0)return Response.success(ApiResult(false));val p=db.products[i];db.products[i]=p.copy(stock=max(0.0,p.stock+body.quantity));db.stockMoves.add(mapOf("id" to db.stockMoves.size+1,"dateTime" to now(),"product" to p.description,"quantity" to body.quantity,"type" to body.type,"reference" to body.reference));db.save();return Response.success(ApiResult(true))}
    override suspend fun deleteProduct(id:Int,body:Map<String,String>):Response<ApiResult>{val ok=db.products.removeIf{it.id==id};db.save();return Response.success(ApiResult(ok))}
    override suspend fun customers():Response<List<Customer>> = Response.success(db.customers.toList())
    override suspend fun createCustomer(body:CustomerWrite):Response<ApiResult>{val id=db.nextCustomerId();db.customers.add(Customer(id,body.name,body.document,body.phone,body.email,body.creditLimit,0.0,0.0));db.save();return Response.success(ApiResult(true,id.toInt()))}
    override suspend fun updateCustomer(id:Int,body:CustomerWrite):Response<ApiResult>{val i=db.customers.indexOfFirst{it.id==id};if(i<0)return Response.success(ApiResult(false));val old=db.customers[i];db.customers[i]=old.copy(name=body.name,document=body.document,phone=body.phone,email=body.email,creditLimit=body.creditLimit);db.save();return Response.success(ApiResult(true,id.toInt()))}
    override suspend fun deleteCustomer(id:Int,body:Map<String,String>):Response<ApiResult>{val ok=db.customers.removeIf{it.id==id && id!=1};db.save();return Response.success(ApiResult(ok))}
    override suspend fun customerPayment(id:Int,body:CustomerPayment):Response<ApiResult>{val i=db.customers.indexOfFirst{it.id==id};if(i<0)return Response.success(ApiResult(false));val c=db.customers[i];db.customers[i]=c.copy(deuda=max(0.0,c.deuda-body.amount),abonosHoy=c.abonosHoy+body.amount);db.cashMoves.add(mapOf("id" to db.cashMoves.size+1,"dateTime" to now(),"type" to "INGRESO","concept" to "Abono cliente ${c.name}","amount" to body.amount,"paymentMethod" to body.paymentMethod));db.save();return Response.success(ApiResult(true))}
    override suspend fun customerAccount(id:Int):Response<CustomerAccountResponse>{val c=db.customers.firstOrNull{it.id==id} ?: return Response.success(CustomerAccountResponse());return Response.success(CustomerAccountResponse(id,c.name,c.deuda,db.debtDetails.filter{it["customerId"]?.toString()==id.toString()}.map{CustomerDebtDetail((it["id"] as? Number)?.toLong()?:0,it["dateTime"]?.toString().orEmpty(),it["entryType"]?.toString().orEmpty(),(it["amount"] as? Number)?.toDouble()?:0.0,it["concept"]?.toString().orEmpty(),it["paymentMethod"]?.toString().orEmpty(),(it["saleId"] as? Number)?.toLong()?:0,(it["ticketNumber"] as? Number)?.toLong()?:0,it["products"]?.toString().orEmpty())}))}
    override suspend fun clearCustomerAccountHistory(id:Int):Response<ApiResult>{db.debtDetails.removeIf{it["customerId"]?.toString()==id.toString()};db.save();return Response.success(ApiResult(true))}
    override suspend fun sales():Response<List<Sale>> = Response.success(db.sales.toList().reversed())
    override suspend fun createSale(body:SaleWrite):Response<SaleResult>{return Response.success(db.createSale(body))}
    override suspend fun sendPendingSale(body:SalePendingWrite):Response<ApiResult>{db.pending.add(body);db.save();return Response.success(ApiResult(true))}
    override suspend fun ticket(id:Long):Response<TicketResult> = Response.success(TicketResult(true,"TICKET #$id"))
    override suspend fun returnItem(saleItemId:Long,body:ReturnWrite):Response<ApiResult>{
        val saleId = saleItemId / 1_000_000L
        val itemId = saleItemId % 1_000_000L
        val sale = db.sales.firstOrNull{it.id==saleId} ?: return Response.success(ApiResult(false))
        val line = sale.articulos.firstOrNull{it.id==itemId} ?: return Response.success(ApiResult(false))
        val requested = if(body.mode.equals("DINERO",true) && line.precioUnitario>0.0) body.amount/line.precioUnitario else body.quantity
        if(requested<=0.0 || requested>line.cantidad+0.0001) return Response.success(ApiResult(false))
        val saleIndex=db.sales.indexOfFirst{it.id==sale.id}
        if(saleIndex<0)return Response.success(ApiResult(false))
        val saleItems=sale.articulos.toMutableList()
        val lineIndex=saleItems.indexOfFirst{it.id==itemId}
        if(lineIndex<0)return Response.success(ApiResult(false))
        saleItems[lineIndex]=line.copy(devuelta=line.devuelta+requested)
        db.sales[saleIndex]=sale.copy(articulos=saleItems)
        val product = db.products.firstOrNull{it.barcode==line.codigo}
        if(product!=null){ val i=db.products.indexOf(product); db.products[i]=product.copy(stock=product.stock+requested) }
        db.audit.add(AuditRow(now(),"DEVOLUCIÓN","Ventas","Ticket #${sale.ticket} · ${line.producto} · ${fmtReturn(requested)} · ${body.mode} · ${body.reason}",db.currentUser.username))
        db.save(); return Response.success(ApiResult(true))
    }
    override suspend fun cancelSale(id:Long,body:CancelWrite):Response<ApiResult>{val s=db.sales.firstOrNull{it.id==id} ?: return Response.success(ApiResult(false));s.articulos.forEach{line->val p=db.products.firstOrNull{it.barcode==line.codigo};if(p!=null){val i=db.products.indexOf(p);db.products[i]=p.copy(stock=p.stock+(line.cantidad-line.devuelta).coerceAtLeast(0.0))}};db.save();return Response.success(ApiResult(true))}
    override suspend fun cash():Response<CashStatus> = Response.success(db.cashStatus())
    override suspend fun mobileUsers():Response<List<MobileUser>> = Response.success(db.users.filter{it.active})
    override suspend fun currentMobileUser():Response<MobileUser> = Response.success(db.currentUser)
    override suspend fun authenticateUser(username:String,password:String):Response<ApiResult>{
        val u=db.users.firstOrNull{it.active && it.username.equals(username.trim(),true)} ?: return Response.success(ApiResult(false))
        if(db.hashPassword(password)!=u.passwordHash) return Response.success(ApiResult(false))
        db.currentUser=u;db.save();return Response.success(ApiResult(true,u.id))
    }
    override suspend fun createMobileUser(body:MobileUserWrite):Response<ApiResult>{
        val username=body.username.trim(); if(username.isBlank()||body.password.isBlank()) return Response.success(ApiResult(false))
        if(db.users.any{it.username.equals(username,true)}) return Response.success(ApiResult(false))
        val id=db.nextUserId(); val u=MobileUser(id,username,body.fullName.trim(),body.role.trim().ifBlank{"CAJERO"},body.phone.trim(),body.email.trim(),db.hashPassword(body.password),body.permissions.distinct(),body.active)
        db.users.add(u);db.save();return Response.success(ApiResult(true,id))
    }
    override suspend fun updateMobileUser(id:Int,body:MobileUserWrite):Response<ApiResult>{
        val i=db.users.indexOfFirst{it.id==id};if(i<0)return Response.success(ApiResult(false))
        val old=db.users[i];val ph=if(body.password.isBlank())old.passwordHash else db.hashPassword(body.password)
        db.users[i]=old.copy(username=body.username.trim(),fullName=body.fullName.trim(),phone=body.phone.trim(),email=body.email.trim(),role=body.role.trim().ifBlank{"CAJERO"},passwordHash=ph,permissions=body.permissions.distinct(),active=body.active);if(db.currentUser.id==id)db.currentUser=db.users[i];db.save();return Response.success(ApiResult(true,id))
    }
    override suspend fun deleteMobileUser(id:Int):Response<ApiResult>{
        if(id==db.currentUser.id) return Response.success(ApiResult(false))
        val ok=db.users.removeIf{it.id==id};db.save();return Response.success(ApiResult(ok))
    }
    override suspend fun switchMobileUser(body:MobileUserSwitch):Response<ApiResult>{
        val u=db.users.firstOrNull{it.id==body.userId && it.active} ?: return Response.success(ApiResult(false))
        if(db.hashPassword(body.password)!=u.passwordHash)return Response.success(ApiResult(false))
        db.currentUser=u;db.save();return Response.success(ApiResult(true,u.id))
    }
    override suspend fun arqueo():Response<CashArqueo> = Response.success(db.arqueo())
    override suspend fun config():Response<MobileConfig> = Response.success(MobileConfig(true))
    override suspend fun openCash(body:CashOpen):Response<ApiResult>{db.cashOpen=body.amount;db.mpOpening=body.mercadoPagoOpening;db.cashOpenedAt=now();db.cashMoves.add(mapOf("id" to db.cashMoves.size+1,"dateTime" to now(),"type" to "APERTURA","concept" to "Apertura de caja","amount" to body.amount,"paymentMethod" to "EFECTIVO"));db.save();return Response.success(ApiResult(true))}
    override suspend fun cashMovement(body:CashMovement):Response<ApiResult>{db.cashMoves.add(mapOf("id" to db.cashMoves.size+1,"dateTime" to now(),"type" to body.type,"concept" to body.concept,"amount" to body.amount,"paymentMethod" to body.paymentMethod));db.save();return Response.success(ApiResult(true))}
    override suspend fun closeCash(body:CashClose):Response<ApiResult>{db.cashClosed=body.counted;db.cashOpenedAt="";db.save();return Response.success(ApiResult(true))}
    override suspend fun promotions():Response<List<Promotion>> = Response.success(db.promotions.toList())
    override suspend fun createPromotion(body:PromotionWrite):Response<ApiResult>{val id=db.nextPromotionId();db.promotions.add(Promotion(id,body.name,body.description,body.price,body.active,body.startAt.orEmpty(),body.endAt.orEmpty(),body.items.map{PromotionItem(it.productId,db.products.firstOrNull{p->p.id==it.productId}?.description.orEmpty(),it.quantity)}));db.save();return Response.success(ApiResult(true,id.toInt()))}
    override suspend fun updatePromotion(body:PromotionWrite):Response<ApiResult>{val i=db.promotions.indexOfFirst{it.id==body.id};if(i<0)return Response.success(ApiResult(false));db.promotions[i]=Promotion(body.id,body.name,body.description,body.price,body.active,body.startAt.orEmpty(),body.endAt.orEmpty(),body.items.map{PromotionItem(it.productId,db.products.firstOrNull{p->p.id==it.productId}?.description.orEmpty(),it.quantity)});db.save();return Response.success(ApiResult(true,body.id.toInt()))}
    override suspend fun deletePromotion(id:Long,body:Map<String,String>):Response<ApiResult>{val ok=db.promotions.removeIf{it.id==id};db.save();return Response.success(ApiResult(ok))}
    override suspend fun salons():Response<List<SalonInfo>> = Response.success(db.salons)
    override suspend fun tables(salonId:Int?):Response<List<TableInfo>> = Response.success(db.tables.filter{salonId==null || it.salonId==salonId})
    override suspend fun createTable(body:TableWrite):Response<ApiResult>{val id=(db.tables.maxOfOrNull{it.id}?:0)+1;db.tables.add(TableInfo(id,body.name,body.capacity,body.shape,false,body.x,body.y,body.width,body.height,body.salonId,body.rotation,body.color));db.save();return Response.success(ApiResult(true,id))}
    override suspend fun updateTable(tableId:Int,body:TableWrite):Response<ApiResult>{val i=db.tables.indexOfFirst{it.id==tableId};if(i<0)return Response.success(ApiResult(false));val old=db.tables[i];db.tables[i]=old.copy(name=body.name,capacity=body.capacity,shape=body.shape,salonId=body.salonId,x=body.x,y=body.y,width=body.width,height=body.height,rotation=body.rotation,color=body.color);db.save();return Response.success(ApiResult(true,tableId))}
    override suspend fun deleteTable(tableId:Int,body:Map<String,String>):Response<ApiResult>{val ok=db.tables.removeIf{it.id==tableId && !it.occupied};if(ok)db.openTickets.removeIf{it.tableId==tableId};db.save();return Response.success(ApiResult(ok))}
    override suspend fun openTickets():Response<List<OpenTicket>> = Response.success(db.openTickets.toList())
    override suspend fun saveOpenTicket(body:OpenTicket):Response<ApiResult>{val i=db.openTickets.indexOfFirst{it.tableId==body.tableId};if(i>=0)db.openTickets[i]=body else db.openTickets.add(body);db.save();return Response.success(ApiResult(true))}
    override suspend fun deleteOpenTicket(tableId:Int):Response<ApiResult>{val ok=db.openTickets.removeIf{it.tableId==tableId};db.save();return Response.success(ApiResult(ok))}
    override suspend fun releaseTable(tableId:Int):Response<ApiResult>{db.tables=db.tables.map{if(it.id==tableId)it.copy(occupied=false)else it}.toMutableList();db.save();return Response.success(ApiResult(true))}
    override suspend fun chargeTable(tableId:Int,body:TableChargeWrite):Response<SaleResult>{val t=db.tables.firstOrNull{it.id==tableId};val open=db.openTickets.firstOrNull{it.tableId==tableId};if(open!=null){db.createSale(SaleWrite(open.customerId,open.items,body.payments,body.payments.sumOf{it.amount},"SALÓN",open.notes));db.openTickets.removeIf{it.tableId==tableId}};db.tables=db.tables.map{if(it.id==tableId)it.copy(occupied=false)else it}.toMutableList();db.save();val ticket=db.nextTicket();return Response.success(SaleResult(true,0,ticket,body.payments.sumOf{it.amount}))}
    override suspend fun appendOpenTicket(tableId:Int,body:OpenTicket):Response<ApiResult>{val i=db.openTickets.indexOfFirst{it.tableId==tableId};if(i>=0)db.openTickets[i]=body else db.openTickets.add(body);db.save();return Response.success(ApiResult(true))}
    override suspend fun analytics():Response<AnalyticsData>{val top=db.sales.flatMap{it.articulos}.groupBy{it.producto}.mapValues{it.value.sumOf{x->x.cantidad}}.entries.sortedByDescending{it.value}.take(10).map{AnalyticsRow(it.key,it.value)};val low=db.products.filter{it.stock<=it.minStock}.map{AnalyticsRow(it.description,it.stock,it.minStock)};return Response.success(AnalyticsData(top,low,db.customers.map{AnalyticsRow(it.name,it.deuda)},db.suppliers.map{AnalyticsRow(it.name,0.0)},emptyList()))}
    override suspend fun generalZ():Response<GeneralZReport>{val total=db.sales.sumOf{it.total};return Response.success(GeneralZReport(true,now(),"CORTE Z GENERAL","Ventas: ${money(total)}\nTickets: ${db.sales.size}\nModo: Android local",db.sales.size,total,1,db.users.size,db.cashStatus().esperado?:0.0))}
    override suspend fun suppliers():Response<List<Supplier>> = Response.success(db.suppliers.toList())
    override suspend fun supplierProducts(id:Int,q:String):Response<List<SupplierProduct>> = Response.success(db.products.filter{it.supplierId==id && (q.isBlank()||it.description.contains(q,true))}.map{SupplierProduct(it.id,it.description,it.barcode,it.stock,it.costPrice)})
    override suspend fun createSupplier(body:SupplierWrite):Response<ApiResult>{val id=db.nextSupplierId();db.suppliers.add(Supplier(id,body.name,body.document,body.phone,body.email,body.address));db.save();return Response.success(ApiResult(true,id.toInt()))}
    override suspend fun updateSupplier(id:Int,body:SupplierWrite):Response<ApiResult>{val i=db.suppliers.indexOfFirst{it.id==id};if(i<0)return Response.success(ApiResult(false));db.suppliers[i]=db.suppliers[i].copy(name=body.name,document=body.document,phone=body.phone,email=body.email,address=body.address);db.save();return Response.success(ApiResult(true))}
    override suspend fun deleteSupplier(id:Int,body:Map<String,String>):Response<ApiResult>{val ok=db.suppliers.removeIf{it.id==id};db.save();return Response.success(ApiResult(ok))}
    override suspend fun purchases():Response<List<Purchase>> = Response.success(db.purchases.toList().reversed())
    override suspend fun createPurchase(body:PurchaseWrite):Response<ApiResult>{val id=db.nextPurchaseId();db.purchases.add(Purchase(id,"OC-$id",db.suppliers.firstOrNull{it.id==body.supplierId}?.name.orEmpty(),"PENDIENTE",body.items.sumOf{it.quantity*it.unitCost},0.0,now(),body.expectedDate,body.items,emptyList(),body.notes));db.save();return Response.success(ApiResult(true,id.toInt()))}
    override suspend fun deletePurchase(id:Long):Response<ApiResult>{val ok=db.purchases.removeIf{it.id==id};db.save();return Response.success(ApiResult(ok))}
    override suspend fun purchaseDetail(id:Long):Response<PurchaseDetail>{val p=db.purchases.firstOrNull{it.id==id} ?: return Response.success(PurchaseDetail());val items=p.items.map{w->val received=p.receivedItems.filter{it.productId==w.productId}.sumOf{it.quantity};PurchaseDetailItem(w.productId,db.products.firstOrNull{it.id==w.productId}?.description.orEmpty(),w.quantity,w.unitCost,w.notes,received,(w.quantity-received).coerceAtLeast(0.0))};return Response.success(PurchaseDetail(p.id,p.orderNo,db.suppliers.firstOrNull{it.name==p.supplier}?.id?:0,p.supplier,p.status,p.notes,p.expectedDate,items))}
    override suspend fun updatePurchase(id:Long,body:PurchaseWrite):Response<ApiResult>{val i=db.purchases.indexOfFirst{it.id==id};if(i<0)return Response.success(ApiResult(false));val p=db.purchases[i];db.purchases[i]=p.copy(supplier=db.suppliers.firstOrNull{it.id==body.supplierId}?.name.orEmpty(),total=body.items.sumOf{it.quantity*it.unitCost},expectedDate=body.expectedDate,items=body.items,receivedItems=p.receivedItems,notes=body.notes);db.save();return Response.success(ApiResult(true))}
    override suspend fun receivePurchase(id:Long,body:PurchaseReceiveWrite):Response<ApiResult>{body.items.forEach{item->val i=db.products.indexOfFirst{p->p.id==item.productId};if(i>=0){val p=db.products[i];db.products[i]=p.copy(stock=p.stock+item.quantity,costPrice=if(item.unitCost>0)item.unitCost else p.costPrice)}};val i=db.purchases.indexOfFirst{it.id==id};if(i>=0){val p=db.purchases[i];val merged=p.receivedItems.toMutableList();body.items.forEach{incoming->val j=merged.indexOfFirst{it.productId==incoming.productId};if(j>=0)merged[j]=merged[j].copy(quantity=merged[j].quantity+incoming.quantity,unitCost=incoming.unitCost,notes=incoming.notes)else merged.add(incoming)};db.purchases[i]=p.copy(status=if(body.closeComplete)"RECIBIDA" else "PARCIAL",receivedTotal=p.receivedTotal+body.items.sumOf{it.quantity*it.unitCost},receivedItems=merged)};db.save();return Response.success(ApiResult(true))}
    override suspend fun cashMovements():Response<List<Map<String,Any>>> = Response.success(db.cashMoves.toList())
    override suspend fun stockMovements():Response<List<Map<String,Any>>> = Response.success(db.stockMoves.toList())
    override suspend fun voidCashMovement(id:Long,body:Map<String,String>):Response<ApiResult>{val ok=db.cashMoves.removeIf{(it["id"] as? Number)?.toLong()==id};db.save();return Response.success(ApiResult(ok))}
    override suspend fun detailedReports():Response<DetailedReportData>{val sales=db.sales.sumOf{it.total};return Response.success(DetailedReportData(sales,db.sales.size.toLong(),if(db.sales.isEmpty())0.0 else sales/db.sales.size,emptyList(),emptyList(),emptyList()))}
    override suspend fun auditLog():Response<List<AuditRow>> = Response.success(db.audit.toList().reversed())
    override suspend fun pairingShortCode(code:String):Response<PairingData> = Response.success(PairingData(mode="LOCAL_ANDROID"))
    private fun fmtReturn(v:Double)=String.format(Locale.US,"%.3f",v)
    private fun money(v:Double) = String.format(Locale.US,"$%,.2f",v)
    private fun now()=SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault()).format(Date())
}

private class LocalSqlStore(val context:Context):android.database.sqlite.SQLiteOpenHelper(context,"mi_comercio.db",null,1){
    override fun onCreate(db:android.database.sqlite.SQLiteDatabase){
        db.execSQL("CREATE TABLE IF NOT EXISTS local_state (k TEXT PRIMARY KEY NOT NULL, v TEXT NOT NULL)")
    }
    override fun onUpgrade(db:android.database.sqlite.SQLiteDatabase,oldVersion:Int,newVersion:Int){}
    fun get(key:String):String?{
        readableDatabase.query("local_state",arrayOf("v"),"k=?",arrayOf(key),null,null,null).use{c->return if(c.moveToFirst())c.getString(0) else null}
    }
    fun put(key:String,value:String){
        writableDatabase.insertWithOnConflict("local_state",null,android.content.ContentValues().apply{put("k",key);put("v",value)},android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE)
    }
}

private class LocalDb(context:Context){
    private val store=LocalSqlStore(context)
    private val gson=Gson()
    var products=load("products",object:TypeToken<MutableList<Product>>(){}.type,mutableListOf<Product>()).also{if(it.isEmpty())it.add(Product(1,"","PRODUCTO COMÚN",0.0,0.0,0.0,0.0,0.0,"General","UN",true,false,true,false))}
    var customers=load("customers",object:TypeToken<MutableList<Customer>>(){}.type,mutableListOf(Customer(1,"Consumidor Final","","","",0.0,0.0,0.0)))
    var suppliers=load("suppliers",object:TypeToken<MutableList<Supplier>>(){}.type,mutableListOf<Supplier>())
    var sales=load("sales",object:TypeToken<MutableList<Sale>>(){}.type,mutableListOf<Sale>())
    var promotions=load("promotions",object:TypeToken<MutableList<Promotion>>(){}.type,mutableListOf<Promotion>())
    var purchases=load("purchases",object:TypeToken<MutableList<Purchase>>(){}.type,mutableListOf<Purchase>())
    var openTickets=load("openTickets",object:TypeToken<MutableList<OpenTicket>>(){}.type,mutableListOf<OpenTicket>())
    var pending=load("pending",object:TypeToken<MutableList<SalePendingWrite>>(){}.type,mutableListOf<SalePendingWrite>())
    var debtDetails=load("debtDetails",object:TypeToken<MutableList<Map<String,Any>>>(){}.type,mutableListOf<Map<String,Any>>())
    var cashMoves=load("cashMoves",object:TypeToken<MutableList<Map<String,Any>>>(){}.type,mutableListOf<Map<String,Any>>())
    var stockMoves=load("stockMoves",object:TypeToken<MutableList<Map<String,Any>>>(){}.type,mutableListOf<Map<String,Any>>())
    var audit=load("audit",object:TypeToken<MutableList<AuditRow>>(){}.type,mutableListOf<AuditRow>())
    var categories=load("categories",object:TypeToken<MutableList<String>>(){}.type,mutableListOf("General","Bebidas","Almacén","Limpieza"))
    var salons=load("salons",object:TypeToken<MutableList<SalonInfo>>(){}.type,mutableListOf(SalonInfo(1,"Salón principal","",true)))
    var tables=load("tables",object:TypeToken<MutableList<TableInfo>>(){}.type,MutableList(12){i->TableInfo(i+1,"Mesa ${i+1}",4,"RECTANGLE",false,(i%3)*150,(i/3)*110,120,80,1,0.0,"")})
    var users=load("users",object:TypeToken<MutableList<MobileUser>>(){}.type,mutableListOf(MobileUser(1,"admin","Administrador","ADMIN","","",hashPassword("123456"),listOf("ALL"),true)))
    var currentUser=users.firstOrNull() ?: MobileUser(1,"admin","Administrador","ADMIN","","",hashPassword("123456"),listOf("ALL"),true)
    var cashOpen= store.get("cash_open")?.toDoubleOrNull() ?: 0.0
    var mpOpening= store.get("mp_open")?.toDoubleOrNull() ?: 0.0
    var cashClosed= store.get("cash_closed")?.toDoubleOrNull() ?: 0.0
    var cashOpenedAt= store.get("cash_opened_at").orEmpty()
    private var ticketSeq=store.get("ticket_seq")?.toLongOrNull() ?: 0L

    init {
        if(users.isEmpty()) users.add(MobileUser(1,"admin","Administrador","ADMIN","","",hashPassword("123456"),listOf("ALL"),true))
        val ai=users.indexOfFirst{it.username.equals("admin",true)}
        if(ai>=0 && users[ai].passwordHash.isBlank()) users[ai]=users[ai].copy(passwordHash=hashPassword("123456"),role="ADMIN",permissions=listOf("ALL"),active=true)
        currentUser=users.firstOrNull{it.active} ?: users.first()
        save()
        restoreExternalBackupIfNeeded()
    }

    private fun restoreExternalBackupIfNeeded(){
        // Solo restaura cuando la base privada está recién creada/default.
        // Una instalación existente nunca es reemplazada por el respaldo.
        if (products.size > 1 || customers.size > 1 || sales.isNotEmpty() || purchases.isNotEmpty()) return
        val raw = MiComercioBackup.read(store.context) ?: return
        runCatching {
            val root = gson.fromJson(raw, com.google.gson.JsonObject::class.java)
            fun <T> restore(key:String, type:java.lang.reflect.Type, current:T):T =
                if (root.has(key)) gson.fromJson<T>(root.get(key), type) else current
            products = restore("products", object:TypeToken<MutableList<Product>>(){}.type, products)
            customers = restore("customers", object:TypeToken<MutableList<Customer>>(){}.type, customers)
            suppliers = restore("suppliers", object:TypeToken<MutableList<Supplier>>(){}.type, suppliers)
            sales = restore("sales", object:TypeToken<MutableList<Sale>>(){}.type, sales)
            promotions = restore("promotions", object:TypeToken<MutableList<Promotion>>(){}.type, promotions)
            purchases = restore("purchases", object:TypeToken<MutableList<Purchase>>(){}.type, purchases)
            openTickets = restore("openTickets", object:TypeToken<MutableList<OpenTicket>>(){}.type, openTickets)
            pending = restore("pending", object:TypeToken<MutableList<SalePendingWrite>>(){}.type, pending)
            debtDetails = restore("debtDetails", object:TypeToken<MutableList<Map<String,Any>>>(){}.type, debtDetails)
            cashMoves = restore("cashMoves", object:TypeToken<MutableList<Map<String,Any>>>(){}.type, cashMoves)
            stockMoves = restore("stockMoves", object:TypeToken<MutableList<Map<String,Any>>>(){}.type, stockMoves)
            audit = restore("audit", object:TypeToken<MutableList<AuditRow>>(){}.type, audit)
            categories = restore("categories", object:TypeToken<MutableList<String>>(){}.type, categories)
            salons = restore("salons", object:TypeToken<MutableList<SalonInfo>>(){}.type, salons)
            tables = restore("tables", object:TypeToken<MutableList<TableInfo>>(){}.type, tables)
            users = restore("users", object:TypeToken<MutableList<MobileUser>>(){}.type, users)
            val ai=users.indexOfFirst{it.username.equals("admin",true)}
            if(ai>=0 && users[ai].passwordHash.isBlank()) users[ai]=users[ai].copy(passwordHash=hashPassword("123456"),role="ADMIN",permissions=listOf("ALL"),active=true)
            currentUser = users.firstOrNull{it.active} ?: currentUser
            cashOpen = root.get("cashOpen")?.asDouble ?: cashOpen
            mpOpening = root.get("mpOpening")?.asDouble ?: mpOpening
            cashClosed = root.get("cashClosed")?.asDouble ?: cashClosed
            cashOpenedAt = root.get("cashOpenedAt")?.asString ?: cashOpenedAt
            ticketSeq = root.get("ticketSeq")?.asLong ?: ticketSeq
            save()
        }
    }

    fun backupJson(): String {
        val backup = mapOf(
            "version" to 2, "products" to products, "customers" to customers, "suppliers" to suppliers,
            "sales" to sales, "promotions" to promotions, "purchases" to purchases, "openTickets" to openTickets,
            "pending" to pending, "debtDetails" to debtDetails, "cashMoves" to cashMoves, "stockMoves" to stockMoves,
            "audit" to audit, "categories" to categories, "salons" to salons, "tables" to tables, "users" to users,
            "cashOpen" to cashOpen, "mpOpening" to mpOpening, "cashClosed" to cashClosed, "cashOpenedAt" to cashOpenedAt,
            "ticketSeq" to ticketSeq
        )
        return gson.toJson(backup)
    }

    fun restoreBackupJson(raw: String): Boolean = runCatching {
        val root = gson.fromJson(raw, com.google.gson.JsonObject::class.java)
        fun <T> restore(key:String, type:java.lang.reflect.Type, current:T):T =
            if (root.has(key)) gson.fromJson<T>(root.get(key), type) else current
        products = restore("products", object:TypeToken<MutableList<Product>>(){}.type, products)
        customers = restore("customers", object:TypeToken<MutableList<Customer>>(){}.type, customers)
        suppliers = restore("suppliers", object:TypeToken<MutableList<Supplier>>(){}.type, suppliers)
        sales = restore("sales", object:TypeToken<MutableList<Sale>>(){}.type, sales)
        promotions = restore("promotions", object:TypeToken<MutableList<Promotion>>(){}.type, promotions)
        purchases = restore("purchases", object:TypeToken<MutableList<Purchase>>(){}.type, purchases)
        openTickets = restore("openTickets", object:TypeToken<MutableList<OpenTicket>>(){}.type, openTickets)
        pending = restore("pending", object:TypeToken<MutableList<SalePendingWrite>>(){}.type, pending)
        debtDetails = restore("debtDetails", object:TypeToken<MutableList<Map<String,Any>>>(){}.type, debtDetails)
        cashMoves = restore("cashMoves", object:TypeToken<MutableList<Map<String,Any>>>(){}.type, cashMoves)
        stockMoves = restore("stockMoves", object:TypeToken<MutableList<Map<String,Any>>>(){}.type, stockMoves)
        audit = restore("audit", object:TypeToken<MutableList<AuditRow>>(){}.type, audit)
        categories = restore("categories", object:TypeToken<MutableList<String>>(){}.type, categories)
        salons = restore("salons", object:TypeToken<MutableList<SalonInfo>>(){}.type, salons)
        tables = restore("tables", object:TypeToken<MutableList<TableInfo>>(){}.type, tables)
        users = restore("users", object:TypeToken<MutableList<MobileUser>>(){}.type, users)
        currentUser = users.firstOrNull() ?: currentUser
        cashOpen = root.get("cashOpen")?.asDouble ?: cashOpen
        mpOpening = root.get("mpOpening")?.asDouble ?: mpOpening
        cashClosed = root.get("cashClosed")?.asDouble ?: cashClosed
        cashOpenedAt = root.get("cashOpenedAt")?.asString ?: cashOpenedAt
        ticketSeq = root.get("ticketSeq")?.asLong ?: ticketSeq
        save()
        true
    }.getOrDefault(false)

    fun save(){
        fun put(k:String,v:Any){store.put(k,gson.toJson(v))}
        put("products",products);put("customers",customers);put("suppliers",suppliers);put("sales",sales);put("promotions",promotions);put("purchases",purchases);put("openTickets",openTickets);put("pending",pending);put("debtDetails",debtDetails);put("cashMoves",cashMoves);put("stockMoves",stockMoves);put("audit",audit);put("categories",categories);put("salons",salons);put("tables",tables);put("users",users)
        store.put("cash_open",cashOpen.toString());store.put("mp_open",mpOpening.toString());store.put("cash_closed",cashClosed.toString());store.put("cash_opened_at",cashOpenedAt);store.put("ticket_seq",ticketSeq.toString())
        // Copia independiente de la instalación. No reemplaza la base local: es un respaldo adicional.
        runCatching { MiComercioBackup.write(store.context, backupJson()) }
    }
    fun <T> load(key:String,type:java.lang.reflect.Type,default:T):T=runCatching{store.get(key)?.let{gson.fromJson<T>(it,type)}?:default}.getOrDefault(default)
    fun nextProductId()=products.maxOfOrNull{it.id}?.plus(1)?:1
    fun nextCustomerId()=customers.maxOfOrNull{it.id}?.plus(1)?:1
    fun nextSupplierId()=suppliers.maxOfOrNull{it.id}?.plus(1)?:1
    fun nextPromotionId()=(promotions.maxOfOrNull{it.id}?:0)+1
    fun nextPurchaseId()=(purchases.maxOfOrNull{it.id}?:0)+1
    fun nextTicket():Long{ticketSeq++;save();return ticketSeq}
    fun createSale(body:SaleWrite):SaleResult{
        val ticket=nextTicket();val id=(sales.maxOfOrNull{it.id}?:0)+1
        val total=body.items.sumOf{it.quantity*it.unitPrice-it.discount}
        body.items.forEach{line->val i=products.indexOfFirst{p->p.id==line.productId};if(i>=0){val p=products[i];if(p.usesInventory)products[i]=p.copy(stock=max(0.0,p.stock-line.quantity))}}
        val customer=customers.firstOrNull{it.id==body.customerId}
        if(customer!=null && body.payments.any{it.method.equals("CRÉDITO",true)||it.method.equals("CREDITO",true)}){
            val i=customers.indexOf(customer);customers[i]=customer.copy(deuda=customer.deuda+total)
            debtDetails.add(mapOf("id" to debtDetails.size+1,"customerId" to customer.id,"dateTime" to now(),"entryType" to "SALE","amount" to total,"concept" to "Venta a crédito","paymentMethod" to "CRÉDITO","saleId" to id,"ticketNumber" to ticket,"products" to body.items.joinToString{it.description}))
        }
        val sale=Sale(id,ticket,total,body.payments.joinToString(","){it.method},body.saleChannel,now(),customer?.name.orEmpty(),currentUser.fullName,body.items.mapIndexed{idx,l->SaleItem(idx.toLong()+1,products.firstOrNull{it.id==l.productId}?.barcode.orEmpty(),l.description,l.quantity,l.unitPrice,l.discount,l.quantity*l.unitPrice-l.discount)},body.payments)
        sales.add(sale);audit.add(AuditRow(now(),"VENTA","Ventas","Ticket #$ticket · ${money(total)}",currentUser.username));save();return SaleResult(true,id,ticket,total)
    }
    fun summary():Summary{val total=sales.sumOf{it.total};return Summary(now(),cashOpenedAt.hashCode().toLong(),total,sales.size.toLong(),products.sumOf{it.stock},products.size.toLong(),products.count{it.stock<=it.minStock}.toLong(),customers.sumOf{it.deuda},cashStatus().esperado?:0.0)}
    fun cashStatus():CashStatus{val opened=cashOpenedAt.isNotBlank();val salesCash=sales.sumOf{if(it.pagos.isNotEmpty())it.pagos.filter{p->p.method.equals("EFECTIVO",true)}.sumOf{p->p.amount}else if(it.medio.contains("EFECTIVO",true))it.total else 0.0};val expected=cashOpen+salesCash+cashMoves.filter{it["type"]=="INGRESO"&&it["paymentMethod"].toString().equals("EFECTIVO",true)}.sumOf{(it["amount"] as? Number)?.toDouble()?:0.0}-cashMoves.filter{it["type"]=="EGRESO"&&it["paymentMethod"].toString().equals("EFECTIVO",true)}.sumOf{(it["amount"] as? Number)?.toDouble()?:0.0};return CashStatus(if(opened)1 else 0,if(opened)"ABIERTA" else "CERRADA",cashOpen,cashOpenedAt,"",if(!opened)cashClosed else null,if(opened)expected else null,if(opened)null else cashClosed-expected,currentUser.fullName,true,mpOpening,0.0)}
    fun arqueo():CashArqueo{val c=cashStatus();val cashSales=sales.sumOf{if(it.pagos.isNotEmpty())it.pagos.filter{p->p.method.equals("EFECTIVO",true)}.sumOf{p->p.amount}else if(it.medio.contains("EFECTIVO",true))it.total else 0.0};val mpSales=sales.sumOf{if(it.pagos.isNotEmpty())it.pagos.filter{p->p.method.equals("MERCADO PAGO",true)}.sumOf{p->p.amount}else if(it.medio.contains("MERCADO PAGO",true))it.total else 0.0};val cashIncome=cashMoves.filter{it["type"]=="INGRESO"&&it["paymentMethod"].toString().equals("EFECTIVO",true)}.sumOf{(it["amount"] as? Number)?.toDouble()?:0.0};val cashExpense=cashMoves.filter{it["type"]=="EGRESO"&&it["paymentMethod"].toString().equals("EFECTIVO",true)}.sumOf{(it["amount"] as? Number)?.toDouble()?:0.0};val mpIncome=cashMoves.filter{it["type"]=="INGRESO"&&it["paymentMethod"].toString().equals("MERCADO PAGO",true)}.sumOf{(it["amount"] as? Number)?.toDouble()?:0.0};val mpExpense=cashMoves.filter{it["type"]=="EGRESO"&&it["paymentMethod"].toString().equals("MERCADO PAGO",true)}.sumOf{(it["amount"] as? Number)?.toDouble()?:0.0};val cashExpected=cashOpen+cashSales+cashIncome-cashExpense;val mpExpected=mpOpening+mpSales+mpIncome-mpExpense;return CashArqueo(c.estado=="ABIERTA",c.estado,c.id,currentUser.fullName,c.aperturaAt,c.apertura,cashSales,cashIncome,cashExpense,cashExpected,true,mpOpening,mpSales,mpIncome,mpExpense,0.0,0.0,mpExpected,cashExpected+mpExpected)}
    fun nextUserId():Int=(users.maxOfOrNull{it.id}?:0)+1
    fun hashPassword(value:String):String=MessageDigest.getInstance("SHA-256").digest(value.toByteArray(Charsets.UTF_8)).joinToString(""){String.format("%02x",it)}
    private fun now()=SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault()).format(Date())
    private fun money(v:Double)=String.format(Locale.US,"$%,.2f",v)
}
