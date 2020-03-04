package todo.repo

import todo.domain._
import cats.effect.IO
import scala.collection.mutable.{Map => MMap}

class MMapTodoRepository extends TodoRepository {
  val mmap:MMap[Int, Todo] = MMap()
  var nextId:Int = 1

  override def findAll: IO[List[Todo]] = IO { mmap.values.toList }

  override def findById(id: Int): IO[Option[Todo]] = IO { mmap.get(id) }

  override def append(msg: String): IO[Unit] = IO {
    mmap(nextId) = Todo(nextId, msg, Pending)
    nextId = nextId + 1
  }

  override def complete(id: Int): IO[Option[Unit]] = IO ( for {
    todo <- mmap get id
    _    <- Option { mmap(id) = todo copy ( status = Complete ) }
  } yield ()
  )
}