package react.common

import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala
import japgolly.scalajs.react.component.ScalaFn
import japgolly.scalajs.react.component.ScalaForwardRef

import scalajs.js

sealed trait ReactRender[Props, CT[-p, +u] <: CtorType[p, u], U] {
  protected[common] val props: Props

  val ctor: CT[Props, U]

  @inline def apply(
    first:       CtorType.ChildArg,
    rest:        CtorType.ChildArg*
  )(implicit ev: CT[Props, U] <:< CtorType.PropsAndChildren[Props, U]): U =
    ctor.applyGeneric(props)((first +: rest): _*)

  @inline val toUnmounted: U = ctor.applyGeneric(props)()
}

sealed trait CtorWithProps[Props, CT[-p, +u] <: CtorType[p, u], U]
    extends ReactRender[Props, CT, U] { self =>

  protected def clone[CT0[-p, +u] <: CtorType[p, u]](
    newCtor: CT0[Props, U]
  ): CtorWithProps[Props, CT0, U] =
    new CtorWithProps[Props, CT0, U] {
      override lazy val ctor                    = newCtor
      override protected[common] lazy val props = self.props
    }

  def withKey(key: Key) =
    clone(ctor.withKey(key))

  final def withKey(k: Long) =
    clone(ctor.withKey(k))

  def addMod(f: CtorType.ModFn) =
    clone(ctor.addMod(f))

  final def withRawProp(name: String, value: js.Any) =
    clone(ctor.withRawProp(name, value))
}

sealed trait ReactComponentProps[Props, CT[-p, +u] <: CtorType[p, u]]
    extends CtorWithProps[Props, CT, Scala.Unmounted[Props, _, _]] { self =>
  val component: Scala.Component[Props, _, _, CT]

  protected[common] lazy val props: Props = this.asInstanceOf[Props]

  override lazy val ctor: CT[Props, Scala.Unmounted[Props, _, _]] = component.ctor

  private def copyComponent(
    newComponent: Scala.Component[Props, _, _, CT]
  ): ReactComponentProps[Props, CT] =
    new ReactComponentProps[Props, CT] {
      override lazy val component               = newComponent
      override protected[common] lazy val props = self.props
    }

  def withRef[S, B](ref: Ref.Handle[ScalaComponent.RawMounted[Props, S, B]]) =
    copyComponent(
      component
        .asInstanceOf[ScalaComponent.Component[Props, S, B, CT]]
        .withRef(ref)
    )

  def withOptionalRef[S, B](ref: Option[Ref.Handle[ScalaComponent.RawMounted[Props, S, B]]]) =
    copyComponent(
      component
        .asInstanceOf[Scala.Component[Props, S, B, CT]]
        .withOptionalRef(ref)
    )
}

class ReactProps[Props](val component: Scala.Component[Props, _, _, CtorType.Props])
    extends ReactComponentProps[Props, CtorType.Props]

class ReactPropsWithChildren[Props](
  val component: Scala.Component[Props, _, _, CtorType.PropsAndChildren]
) extends ReactComponentProps[Props, CtorType.PropsAndChildren]

sealed trait ReactFnComponentProps[Props, CT[-p, +u] <: CtorType[p, u], U]
    extends CtorWithProps[Props, CT, U] {
  val component: ScalaFn.Component[Props, CT]

  protected[common] lazy val props: Props = this.asInstanceOf[Props]
}

class ReactFnProps[Props](val component: ScalaFn.Component[Props, CtorType.Props])
    extends ReactFnComponentProps[Props, CtorType.Props, ScalaFn.Unmounted[Props]] {
  override lazy val ctor: CtorType.Props[Props, ScalaFn.Unmounted[Props]] = component.ctor
}

class ReactFnPropsWithChildren[Props](
  val component: ScalaFn.Component[Props, CtorType.PropsAndChildren]
) extends ReactFnComponentProps[Props, CtorType.PropsAndChildren, ScalaFn.Unmounted[Props]] {
  override lazy val ctor: CtorType.PropsAndChildren[Props, ScalaFn.Unmounted[Props]] =
    component.ctor
}

sealed trait ReactComponentPropsForwardRef[Props, R, CT[-p, +u] <: CtorType[p, u]]
    extends CtorWithProps[Props, CT, ScalaForwardRef.Unmounted[Props, R]] { self =>
  val component: ScalaForwardRef.Component[Props, R, CT]

  protected[common] lazy val props: Props = this.asInstanceOf[Props]

  override lazy val ctor: CT[Props, ScalaForwardRef.Unmounted[Props, R]] = component.ctor

  private def copyComponent(
    newComponent: ScalaForwardRef.Component[Props, R, CT]
  ): ReactComponentPropsForwardRef[Props, R, CT] =
    new ReactComponentPropsForwardRef[Props, R, CT] {
      override lazy val component               = newComponent
      override protected[common] lazy val props = self.props
    }

  def withRef(ref: Ref.Handle[R]) =
    copyComponent(component.withRef(ref).asInstanceOf[ScalaForwardRef.Component[Props, R, CT]])

  def withOptionalRef(ref: Option[Ref.Handle[R]]) =
    copyComponent(
      component
        .withOptionalRef(ref)
        .asInstanceOf[ScalaForwardRef.Component[Props, R, CT]]
    )
}

class ReactPropsForwardRef[Props, R](
  val component: ScalaForwardRef.Component[Props, R, CtorType.Props]
) extends ReactComponentPropsForwardRef[Props, R, CtorType.Props]

class ReactPropsForwardRefWithChildren[Props, R](
  val component: ScalaForwardRef.Component[Props, R, CtorType.PropsAndChildren]
) extends ReactComponentPropsForwardRef[Props, R, CtorType.PropsAndChildren]
