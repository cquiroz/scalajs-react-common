package react.common

import japgolly.scalajs.react.CtorType
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.component.Js
import japgolly.scalajs.react.component.JsFn
import japgolly.scalajs.react.vdom.TagMod
import japgolly.scalajs.react.vdom.Builder
import scala.scalajs.js

class AttrsBuilder(p: js.Object) extends Builder.ToJs {
  props = p

  def toJs(attrs: Seq[TagMod]): Unit = {
    attrs.foreach(_.applyTo(this))
    addKeyToProps()
    addClassNameToProps()
    addStyleToProps()
  }
}

trait JsComponent[P <: js.Object] {
  def cprops: P
}

trait GenericJsComponent[P <: js.Object, CT[-p, +u] <: CtorType[p, u], U] extends JsComponent[P] {
  @inline def render: Render[P]
}

trait GenericJsComponentC[P <: js.Object, CT[-p, +u] <: CtorType[p, u], U, A]
    extends JsComponent[P] {
  val children: CtorType.ChildrenArgs
  def withChildren(children: CtorType.ChildrenArgs): A

  @inline def renderWith: RenderC[P]
  @inline def render: Render[P] = renderWith(children)
}

trait Passthrough[P <: js.Object] extends JsComponent[P] {
  val modifiers: Seq[TagMod]
}

trait PassthroughA[P <: js.Object] extends Passthrough[P] {
  def cprops: P = {
    val props   = cprops
    val builder = new AttrsBuilder(props)
    builder.toJs(modifiers)
    props
  }
}

trait PassthroughAC[P <: js.Object] extends Passthrough[P] {
  def rawModifiers: (P, List[VdomNode]) = {
    val props   = cprops
    val builder = new AttrsBuilder(props)
    builder.toJs(modifiers)
    (props, builder.childrenAsVdomNodes)
  }

  def cprops: P = rawModifiers._1
}

trait GenericFnComponentA[P <: js.Object, CT[-p, +u] <: CtorType[p, u], U, A]
    extends PassthroughA[P] {
  protected val component: JsFn.Component[P, CT]
  def addModifiers(modifiers: Seq[TagMod]): A

  @inline def render: Render[P] = component.applyGeneric(cprops)()
}

trait GenericFnComponentAC[P <: js.Object, CT[-p, +u] <: CtorType[p, u], U, A]
    extends PassthroughAC[P] {
  protected val component: JsFn.Component[P, CT]
  def addModifiers(modifiers: Seq[TagMod]): A

  @inline def render: Render[P] = {
    val (props, children) = rawModifiers
    component.applyGeneric(props)(children: _*)
  }
}

trait GenericJsComponentA[P <: js.Object, CT[-p, +u] <: CtorType[p, u], U, A]
    extends PassthroughA[P] {
  protected val component: Js.ComponentWithRawType[P, Null, Js.RawMounted[P, Null], CT]
  def addModifiers(modifiers: Seq[TagMod]): A

  @inline def render: Render[P] = component.applyGeneric(cprops)()
}

trait GenericJsComponentAC[P <: js.Object, CT[-p, +u] <: CtorType[p, u], U, A]
    extends PassthroughAC[P] {
  protected val component: Js.ComponentWithRawType[P, Null, Js.RawMounted[P, Null], CT]
  def addModifiers(modifiers: Seq[TagMod]): A

  @inline def render: Render[P] = {
    val (props, children) = rawModifiers
    component.applyGeneric(props)(children: _*)
  }
}
