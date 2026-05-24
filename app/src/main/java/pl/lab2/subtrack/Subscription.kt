package pl.lab2.subtrack

data class Subscription(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val plan: String,
    val price: Double,
    val billingCycle: String
)