package hr.ferit.lukacvetkovic.tutora.models

data class StudentSolvesQuiz(
    var studentEmail: String = "",
    var quizName: String = "",
    var subjectName: String = "",
    var correctAnswers: Int = 0,
    var solved: Boolean = false
)
