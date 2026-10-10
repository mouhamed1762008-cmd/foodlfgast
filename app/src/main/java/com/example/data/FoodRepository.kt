package com.example.data

import android.content.Context
import com.example.R
import com.example.model.Coupon
import com.example.model.DeliveryAddress
import com.example.model.FoodCategory
import com.example.model.FoodProduct
import com.example.model.Order
import com.example.model.OrderItem
import com.example.model.OrderStatus
import com.example.model.Review
import com.example.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FoodRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be authenticated before performing this action.")
    }

    // --- PRODUCTS & CATEGORIES ---

    fun getCategories(): List<FoodCategory> = FoodCatalogData.categories

    fun getProducts(): List<FoodProduct> = FoodCatalogData.products

    fun getProductById(id: String): FoodProduct? {
        return FoodCatalogData.products.firstOrNull { it.id == id }
    }

    fun getCoupons(): List<Coupon> = FoodCatalogData.coupons

    fun searchProducts(
        query: String = "",
        categoryId: String = "all",
        vegetarianOnly: Boolean = false,
        spicyOnly: Boolean = false,
        maxPriceCents: Int? = null,
        sortBy: String = "POPULAR" // POPULAR, PRICE_LOW, PRICE_HIGH, RATING
    ): List<FoodProduct> {
        return FoodCatalogData.products.filter { product ->
            val matchesQuery = query.isBlank() ||
                    product.name.contains(query, ignoreCase = true) ||
                    product.description.contains(query, ignoreCase = true)

            val matchesCategory = categoryId == "all" || product.categoryId == categoryId
            val matchesVeg = !vegetarianOnly || product.isVegetarian
            val matchesSpicy = !spicyOnly || product.isSpicy
            val matchesPrice = maxPriceCents == null || product.displayPriceCents <= maxPriceCents

            matchesQuery && matchesCategory && matchesVeg && matchesSpicy && matchesPrice
        }.sortedWith { a, b ->
            when (sortBy) {
                "PRICE_LOW" -> a.displayPriceCents.compareTo(b.displayPriceCents)
                "PRICE_HIGH" -> b.displayPriceCents.compareTo(a.displayPriceCents)
                "RATING" -> b.rating.compareTo(a.rating)
                else -> (if (b.isPopular) 1 else 0).compareTo(if (a.isPopular) 1 else 0)
            }
        }
    }

    // --- USER PROFILE ---

    fun observeUserProfile(userId: String): Flow<UserProfile?> = callbackFlow {
        val docRef = db.collection("users").document(userId)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.GET, docRef.path)
                trySend(null)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val profile = snapshot.toObject(UserProfile::class.java)
                trySend(profile)
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val data = hashMapOf<String, Any>(
            "uid" to uid,
            "email" to profile.email,
            "fullName" to profile.fullName,
            "phoneNumber" to profile.phoneNumber,
            "preferredLanguage" to profile.preferredLanguage,
            "favorites" to profile.favorites,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (profile.profileImageUrl.isNotBlank()) {
            data["profileImageUrl"] = profile.profileImageUrl
        }
        try {
            docRef.set(data, com.google.firebase.firestore.SetOptions.merge()).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun toggleFavorite(productId: String, currentFavorites: List<String>): List<String> {
        val uid = requireUserId()
        val updated = if (currentFavorites.contains(productId)) {
            currentFavorites - productId
        } else {
            currentFavorites + productId
        }
        val docRef = db.collection("users").document(uid)
        try {
            docRef.update("favorites", updated, "updatedAt", FieldValue.serverTimestamp()).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
        }
        return updated
    }

    // --- ADDRESSES ---

    fun observeAddresses(userId: String): Flow<List<DeliveryAddress>> = callbackFlow {
        val collRef = db.collection("users").document(userId).collection("addresses")
        val listener = collRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.LIST, collRef.path)
                trySend(emptyList())
                return@addSnapshotListener
            }
            val addresses = snapshot?.toObjects(DeliveryAddress::class.java) ?: emptyList()
            trySend(addresses)
        }
        awaitClose { listener.remove() }
    }

    suspend fun addAddress(address: DeliveryAddress) {
        val uid = requireUserId()
        val addressId = if (address.id.isNotBlank()) address.id else "addr_${System.currentTimeMillis()}"
        val docRef = db.collection("users").document(uid).collection("addresses").document(addressId)
        val payload = hashMapOf<String, Any>(
            "id" to addressId,
            "userId" to uid,
            "label" to address.label,
            "addressLine" to address.addressLine,
            "city" to address.city,
            "postalCode" to address.postalCode,
            "phoneNumber" to address.phoneNumber,
            "isDefault" to address.isDefault,
            "createdAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteAddress(addressId: String) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("addresses").document(addressId)
        try {
            docRef.delete().await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    // --- ORDERS ---

    fun observeOrders(userId: String): Flow<List<Order>> = callbackFlow {
        val collRef = db.collection("orders")
        val query = collRef.whereEqualTo("userId", userId)
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                handleFirestoreError(error, OperationType.LIST, collRef.path)
                trySend(emptyList())
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Order::class.java)?.copy(id = doc.id)
            } ?: emptyList()
            // Sort by most recent
            trySend(list.sortedByDescending { it.createdAt?.seconds ?: Long.MAX_VALUE })
        }
        awaitClose { listener.remove() }
    }

    suspend fun placeOrder(order: Order): String {
        val uid = requireUserId()
        val orderId = if (order.id.isNotBlank()) order.id else "QB-${System.currentTimeMillis().toString().takeLast(6)}"
        val docRef = db.collection("orders").document(orderId)

        val itemsPayload = order.items.map { item ->
            mapOf(
                "productId" to item.productId,
                "productName" to item.productName,
                "priceCents" to item.priceCents,
                "quantity" to item.quantity,
                "customizationSummary" to item.customizationSummary,
                "imageUrl" to item.imageUrl
            )
        }

        val orderPayload = hashMapOf<String, Any>(
            "id" to orderId,
            "userId" to uid,
            "customerName" to order.customerName,
            "customerPhone" to order.customerPhone,
            "deliveryAddress" to order.deliveryAddress,
            "deliveryInstructions" to order.deliveryInstructions,
            "isDelivery" to order.isDelivery,
            "items" to itemsPayload,
            "subtotal" to order.subtotal,
            "discount" to order.discount,
            "deliveryFee" to order.deliveryFee,
            "total" to order.total,
            "couponCode" to order.couponCode,
            "paymentMethod" to order.paymentMethod,
            "paymentStatus" to order.paymentStatus,
            "orderStatus" to order.orderStatus,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            docRef.set(orderPayload).await()
            return orderId
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        val docRef = db.collection("orders").document(orderId)
        try {
            docRef.update(
                "orderStatus", newStatus.name,
                "updatedAt", FieldValue.serverTimestamp()
            ).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun cancelOrder(orderId: String) {
        updateOrderStatus(orderId, OrderStatus.CANCELLED)
    }

    // --- REVIEWS ---

    suspend fun addReview(review: Review) {
        val uid = requireUserId()
        val reviewId = "rev_${System.currentTimeMillis()}"
        val docRef = db.collection("reviews").document(reviewId)
        val payload = hashMapOf<String, Any>(
            "id" to reviewId,
            "userId" to uid,
            "userName" to review.userName,
            "productId" to review.productId,
            "rating" to review.rating,
            "comment" to review.comment,
            "createdAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }
}
