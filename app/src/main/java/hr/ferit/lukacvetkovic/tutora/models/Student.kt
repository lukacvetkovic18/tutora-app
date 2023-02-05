package hr.ferit.lukacvetkovic.tutora.models

data class Student(
    var firstName: String = "",
    var lastName: String = "",
    var age: Int = 0,
    var email: String = "",
    var password: String = ""
)
