package todo.service

import cats.effect._
import cats.implicits._
import fs2.{Stream, text}
import io.circe.Encoder
import org.http4s.dsl.io._
import todo.repo.TodoRepository
import io.circe.syntax._
import io.circe.generic.auto._
import org.http4s.{Header, HttpRoutes, Response, Status}
import org.http4s.dsl.io._
import org.http4s.implicits._

object TodoService {
  def json[A : Encoder](obj:A): IO[Response[IO]] = Ok(obj.asJson.noSpaces, Header("Content-Type", "application/json"))

  implicit class JsonOps[A : Encoder](obj:A) {
    def jsoned:String = obj.asJson.noSpaces
  }

  implicit class OptionOps[A : Encoder](opt:Option[A]) {
    def jsonedOr404(msg404:String = "херня"):IO[Response[IO]] = opt map { t => Ok(t jsoned, Header("Content-Type", "application/json")) } getOrElse NotFound(msg404)
  }

  def build(repo:TodoRepository) = HttpRoutes.of[IO] {

    case GET -> Root / "todo" => for {
      todos    <- repo.findAll
      response <- Ok (todos.jsoned, Header("Content-Type", "application/json") )
    } yield response

    case GET -> Root / "todo" / IntVar(id) => for {
      todo     <- repo findById id
      response <- todo jsonedOr404()
    } yield response

    case req @ POST -> Root / "todo" => for {
      msg      <- req.as[String]
      response <- (repo append msg) *> Ok()
    } yield response

    case PATCH -> Root / "todo" / IntVar(id) / "complete" => for {
      work     <- repo complete id
      response <- work jsonedOr404()
    } yield response
  }.orNotFound.handleError(e => Response[IO]( status = Status(500) , body=Stream("Error occured " + e.toString() + e.getStackTrace).through(text.utf8Encode) ) )
}
