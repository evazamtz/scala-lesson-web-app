package todo.repo

import java.io.PrintWriter

import cats.effect.IO
import cats.effect._
import cats.implicits._
import io.circe.parser.decode
import io.circe.syntax._
import io.circe.generic.auto._
import todo.domain._

import scala.io.Source

class LocalFileTodoRepository(val pathToDb:String) extends TodoRepository {
  type Todos = Map[Int,Todo]

  protected def load:IO[Todos] = for {
    raw <- IO { val s = Source.fromFile(pathToDb, "UTF-8"); val r = s.mkString; s.close; r }
    map <- IO fromEither { decode[Todos](raw) }
  } yield map

  protected def flush(map:Todos):IO[Unit] = {
    val jsonRaw = map.asJson.noSpaces
    IO { new PrintWriter(pathToDb, "UTF-8") { write(jsonRaw); close() } }
  }

  override def findAll: IO[List[Todo]] = for {
    map <- load
  } yield map.values.toList

  override def findById(id: Int): IO[Option[Todo]] = for {
    todos <- load
  } yield todos get id

  override def append(msg: String): IO[Unit] = for {
    todos    <- load
    newId    = if (todos.isEmpty) 1 else (todos maxBy (_._1))._1 + 1
    newTodos = todos.updated(newId, Todo(newId, msg, Pending))
    _        <- flush(newTodos)
  } yield ()

  override def complete(id: Int): IO[Option[Unit]] = for {
    todos    <- load
    todoO    =  todos.get(id) map { _.copy(status=Complete) }
    flushIO  =  todoO map {t => flush(todos.updated(id, t)) as Some( () ) }
    io       <- flushIO getOrElse { IO.pure(Option.empty[Unit]) }
  } yield io
}