package react

package common

import scala.scalajs.js
import scala.scalajs.js.|
import js.JSConverters._
import japgolly.scalajs.react.Reusability
import japgolly.scalajs.react.vdom.html_<^._

package object style             {
  implicit val IntStyleExtractor: StyleExtractor[Int] = new StyleExtractor[Int] {
    override def extract(s: Style, key: String): Option[Int] =
      s.styles.get(key).flatMap { x =>
        (x: Any) match {
          case x: Int => Some(x)
          case _      => None
        }
      }
  }

  implicit val StringStyleExtractor: StyleExtractor[String] = new StyleExtractor[String] {
    override def extract(s: Style, key: String): Option[String] =
      s.styles.get(key).flatMap { x =>
        (x: Any) match {
          case x: String => Some(x)
          case _         => None
        }
      }
  }
}

package style {
  sealed trait StyleExtractor[A] {
    def extract(s: Style, key: String): Option[A]
  }

  final class ClassnameCssOps(cn: (js.UndefOr[String], js.UndefOr[Css])) {
    def toJs: js.UndefOr[String] =
      (cn._1.toOption, cn._2.toOption) match {
        case (cn @ Some(_), None) => cn.orUndefined
        case (None, cz @ Some(_)) => cz.map(_.htmlClass).orUndefined
        case (Some(cs), Some(cz)) => s"$cs ${cz.htmlClass}"
        case _                    => js.undefined
      }
  }

  trait StyleSyntax                                         {
    implicit def styePairU(a: (js.UndefOr[String], js.UndefOr[Css])): ClassnameCssOps =
      new ClassnameCssOps(a)

    implicit final def cssToTagMod(s: Css): TagMod =
      ^.className := s.htmlClass

    implicit final def listCssToTagMod(s: List[Css]): TagMod =
      s.map(cssToTagMod).toTagMod
  }

  /**
   * Simple class to represent styles
   */
  final case class Style(styles: Map[String, String | Int]) {
    def isEmpty: Boolean = styles.isEmpty

    def nonEmpty: Boolean = styles.nonEmpty

    def toJsObject: js.Object = Style.toJsObject(this)

    def extract[A](key: String)(implicit S: StyleExtractor[A]): Option[A] =
      S.extract(this, key)

    def remove(key: String): Style =
      if (this.styles.contains(key))
        Style(this.styles - key)
      else
        this

    def when_(pred: => Boolean): Style =
      if (pred)
        this
      else
        Style.Empty

    def unless_(pred: => Boolean): Style =
      if (pred)
        Style.Empty
      else
        this

  }

  object Style                                    {
    def toJsObject(style: Style): js.Object =
      style.styles.toJSDictionary.asInstanceOf[js.Object]

    def fromJsObject(o: js.Object): Style = {
      val xDict = o.asInstanceOf[js.Dictionary[String | Int]]
      val map   = (for ((prop, value) <- xDict) yield prop -> value).toMap
      Style(map)
    }

    val Empty: Style = Style(Map.empty)
  }

  /**
   * Simple class to represent css class
   */
  final case class Css(htmlClasses: List[String]) {
    def isEmpty: Boolean = htmlClasses.isEmpty

    def nonEmpty: Boolean = htmlClasses.nonEmpty

    val htmlClass: String = htmlClasses.mkString(" ")

    def when_(pred: => Boolean): Css =
      if (pred)
        this
      else
        Css.Empty

    def unless_(pred: => Boolean): Css =
      if (pred)
        Css.Empty
      else
        this
  }

  object Css {
    def apply(htmlClass: String): Css = Css(List(htmlClass))

    val Empty: Css = Css(Nil)

    implicit val cssReuse: Reusability[Css] = Reusability.by(_.htmlClass)
  }
}
