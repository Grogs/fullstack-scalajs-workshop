package me.gregd.scalax

import org.scalajs.dom.ext.Ajax
import play.api.libs.json._

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object Client extends autowire.Client[String, Reads, Writes] with autowire.Serializers[String, Reads, Writes] {
    def doCall(req: Request): Future[String] =
        Ajax.post(
            url = "/api/" + req.path.mkString("/"),
            data = JsObject(req.args.mapValues(Json.parse).toSeq).toString()
        ).map(_.responseText)


    def read[Result: Reads]( p: String ) = Json.fromJson( Json.parse( p ) ) match {
        case JsSuccess( r, _ ) => r
        case JsError( errors ) => throw new RuntimeException( errors.toString )
    }
    def write[Result: Writes]( r: Result ) = Json.stringify( Json.toJson( r ) )
}
