package todo.domain


// ADT

sealed trait TodoStatus
case object Pending extends TodoStatus
case object Complete extends TodoStatus

case class Todo(id:Int, msg:String, status:TodoStatus)