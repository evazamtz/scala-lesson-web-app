package todo.repo

import cats.effect.IO
import todo.domain.Todo

trait TodoRepository {
  def findAll:IO[List[Todo]]
  def findById(id:Int):IO[Option[Todo]]
  def append(msg:String):IO[Unit]
  def complete(id:Int):IO[Option[Unit]]
}
