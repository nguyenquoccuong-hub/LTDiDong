

fun main() {

    printCakeBottom(age = 10, layers = 5)

    val num = 4
    if (num > 4) {
        println("The variable is greater than 4")
    } else if (num == 4) {
        println("The variable is equal to 4")
    } else {
        println("The variable is less than 4")
    }

    val luckyNumber = 4
    val rollResult = roll()

    when (rollResult) {
        luckyNumber -> println("You won!")
        1 -> println("So sorry! You rolled a 1. Try again!")
        2 -> println("Sadly, you rolled a 2. Try again!")
        3 -> println("Unfortunately, you rolled a 3. Try again!")
        4 -> println("No luck! You rolled a 4. Try again!")
        5 -> println("Don't cry! You rolled a 5. Try again!")
        6 -> println("Apologies! You rolled a 6. Try again!")
    }

    printHello()
}

fun roll(): Int {
    return (1..6).random()
}

fun printCakeBottom(age: Int, layers: Int) {
    println("Cake bottom with age $age and layers $layers")
}

fun printHello() {
    println("Hello Kotlin")
}