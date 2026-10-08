package ru.mtuci.drivenext.data.cars

class CarRepository {
    private val cars = listOf(
        Car(1, "Toyota", "Camry", 4200, "2022 · автомат · бензин"),
        Car(2, "BMW", "X3", 6900, "2023 · автомат · бензин"),
        Car(3, "Kia", "K5", 3900, "2022 · автомат · бензин"),
        Car(4, "Tesla", "Model 3", 7400, "2023 · автомат · электро"),
        Car(5, "Volkswagen", "Polo", 2800, "2021 · автомат · бензин"),
    )
    fun all(): List<Car> = cars
    fun search(query: String): List<Car> = cars.filter { "${it.brand} ${it.model}".contains(query, ignoreCase = true) }
}
