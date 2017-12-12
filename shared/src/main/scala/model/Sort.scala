package model

import enumeratum.{Enum, EnumEntry}

sealed trait Sort extends EnumEntry

object Sort extends Enum[Sort] {
  val values = findValues
  case object Chronological extends Sort
  case object Geographical extends Sort
}
