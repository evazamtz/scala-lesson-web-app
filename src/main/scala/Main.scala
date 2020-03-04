import cats.effect._
import cats.implicits._

import org.http4s.server.blaze._

import pureconfig._
import pureconfig.generic.auto._

import todo.repo._
import todo.conf._
import todo.service._

object Main extends IOApp {

   def run(args: List[String]): IO[ExitCode] = for {
    appConfig <- IO fromEither { ConfigSource.default.load[AppConfig].leftMap(crf => new Exception("Failed to read application config" + crf)) }
    _         <- IO { println ("Loaded application conf: " + appConfig) }
    repo      = new LocalFileTodoRepository(appConfig.repo.localFile.path)
    exitCode  <- BlazeServerBuilder[IO]
                .bindHttp(appConfig.http.port, appConfig.http.host)
                .withHttpApp( TodoService.build(repo) )
                .serve
                .compile
                .drain
                .as(ExitCode.Success)
  } yield exitCode

}
// defined object Main