package react.common

import scala.scalajs.js.|

trait EnumValue[A] {
  def value(a: A): String
}

object EnumValue {
  @inline final def apply[A](implicit ev: EnumValue[A]): EnumValue[A] = ev

  def instance[A](f: A => String): EnumValue[A] =
    new EnumValue[A] {
      def value(a: A): String = f(a)
    }

  def toLowerCaseString[A]: EnumValue[A] =
    new EnumValue[A] {
      def value(a: A): String = a.toString.toLowerCase
    }
}

trait EnumValueB[A] {
  def value(a: A): Boolean | String
}

object EnumValueB {
  @inline final def apply[A](implicit ev: EnumValueB[A]): EnumValueB[A] = ev

  def instance[A](f: A => Boolean | String): EnumValueB[A] =
    new EnumValueB[A] {
      def value(a: A): Boolean | String = f(a)
    }

  def toLowerCaseStringT[A](trueValue: A): EnumValueB[A] =
    new EnumValueB[A] {
      def value(a: A): Boolean | String =
        a match {
          case b if b == trueValue => true
          case _                   => a.toString.toLowerCase
        }
    }

  def toLowerCaseStringF[A](falseValue: A): EnumValueB[A] =
    new EnumValueB[A] {
      def value(a: A): Boolean | String =
        a match {
          case b if b == falseValue => false
          case _                    => a.toString.toLowerCase
        }
    }

  def toLowerCaseStringTF[A](trueValue: A, falseValue: A): EnumValueB[A] =
    new EnumValueB[A] {
      def value(a: A): Boolean | String =
        a match {
          case b if b == trueValue  => true
          case b if b == falseValue => false
          case _                    => a.toString.toLowerCase
        }
    }
}
