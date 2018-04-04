package services.events

import model.Conference

object ImageService {

  def imageName(conferenceName: String): String = {
    conferenceName match {
      case "SCALA EXCHANGE 2017" => "scala exchange.png"
      case "SCALA EXCHANGE" => "scala exchange.png"
      case "F(BY) 2017" => "f(by).png"
      case "LAMBDACONF WINTER RETREAT" => "lambdaconf.jpg"
      case "JVMCON" => "jvmcon.png"
      case "SCALA MATSURI" => "scalamatsuri.png"
      case "SCALAR" => "scalar.jpg"
      case "SCALASPHERE" => "scalasphere.png"
      case "SCALAUA" => "scalaua.jpg"
      case "FLATMAP(OSLO)" => "flatMapOslo.png"
      case "SCALA DAYS (EUROPE)" => "scaladays.png"
      case "LAMBDACONF 2018" => "lambdaconf.jpg"
      case "LAMBDACONF" => "lambdaconf.jpg"
      case "SCALA DAYS (NORTH AMERICA)" => "scaladays.png"
      case "SCALA SWARM" => "scalaswarm.jpg"
      case "BEESCALA 2018" => "beescala.png"
      case "BEESCALA" => "beescala.png"
      case "TYPELEVEL SUMMIT BERLIN" => "typelevel.jpg"
      case "LX SCALA" => "lx-scala.jpg"
      case "SCALAPEñO" => "scalapeno.png"
      case "SCALAWAVE" => "scala-wave.png"
      case "SCALA ITALY" => "scala-italy.png"
      case "LAMBDA WORLD" =>"lambda-world.png"
      case "SCALA SYMPOSIUM" => "scala-symposium.jpg"
      case _ => "placeholder.png"
    }
  }

}
