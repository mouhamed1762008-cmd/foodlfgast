package com.example

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.FoodRepository
import com.example.model.DeliveryAddress
import com.example.model.Order
import com.example.model.OrderItem
import com.example.model.OrderStatus
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FoodRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun unauthenticatedActionThrows() {
        val repository = FoodRepository(firestore)
        var failed = false
        try {
            repository.requireUserId()
        } catch (e: IllegalStateException) {
            failed = true
        }
        assertTrue("Expected unauthenticated call to fail", failed)
    }

    @Test
    fun authenticatedUserCanPlaceOrder() = runBlocking {
        val uid = signInTestUser("customer@example.com")
        val repository = FoodRepository(firestore)

        val order = Order(
            id = "test_order_${System.currentTimeMillis()}",
            userId = uid,
            customerName = "Test Customer",
            items = listOf(
                OrderItem(
                    productId = "burger_smash_double",
                    productName = "Double Smashed Angus Burger",
                    priceCents = 849,
                    quantity = 1
                )
            ),
            subtotal = 849,
            total = 849,
            orderStatus = OrderStatus.PLACED.name,
            paymentMethod = "CASH_ON_DELIVERY"
        )

        val orderId = repository.placeOrder(order)
        assertNotNull(orderId)
    }

    @Test
    fun authenticatedUserCanSaveAddress() = runBlocking {
        val uid = signInTestUser("address_tester@example.com")
        val repository = FoodRepository(firestore)

        val address = DeliveryAddress(
            id = "addr_test_1",
            userId = uid,
            label = "Home",
            addressLine = "123 QuickBite Avenue",
            city = "Springfield",
            isDefault = true
        )

        repository.addAddress(address)
    }
}
