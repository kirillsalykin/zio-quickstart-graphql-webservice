package dev.zio.quickstart

import caliban.ZHttpAdapter
import dev.zio.quickstart.ExampleData._
import zhttp.http._
import zhttp.service.Server
import zio.ZIOAppDefault
import zio.stream.ZStream

import scala.language.postfixOps


object MainApp extends ZIOAppDefault {
  private val graphiql = Http.fromStream(ZStream.fromResource("graphiql.html"))

  override def run =
    (for {
      interpreter <- ExampleApi.api.interpreter
      _           <- Server
        .start(
          8088,
          Http.collectHttp[Request] {
            case _ -> !! / "api" / "graphql" => ZHttpAdapter.makeHttpService(interpreter)
            case _ -> !! / "graphiql"        => graphiql
          }
        )
        .forever
    } yield ())
      .provideLayer(ExampleService.make(sampleCharacters))
      .exitCode
}
