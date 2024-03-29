package react.common

import japgolly.scalajs.react._
import japgolly.scalajs.react.internal.FacadeExports
import japgolly.scalajs.react.facade.React.RefHandle
import japgolly.scalajs.react.test._
import japgolly.scalajs.react.vdom.html_<^._

class ReactPropsWithChildrenSuite extends munit.FunSuite {

  case class PropsWithChildren()
      extends ReactPropsWithChildren[PropsWithChildren](propsWithChildrenComponent)

  val propsWithChildrenComponent =
    ScalaComponent.builder[PropsWithChildren].render_C(c => <.div(c)).build

  test("propsWithChildren") {
    val p        = PropsWithChildren()
    val u        = p(<.div)
    val key      = u.key
    val ref      = u.ref
    val props    = u.props
    val children = u.propsChildren
    assertEquals(key, None)
    assertEquals(ref, None)
    assertEquals(props, PropsWithChildren())
    assertEquals(children.count, 1)
    ReactTestUtils.withNewBodyElement { mountNode =>
      p.renderIntoDOM(mountNode)
      val html = mountNode.innerHTML
      assertEquals(html, """<div></div>""")
    }
    ReactTestUtils.withNewBodyElement { mountNode =>
      u.renderIntoDOM(mountNode)
      val html = mountNode.innerHTML
      assertEquals(html, """<div><div></div></div>""")
    }
  }

  test("propsWithChildren.withKey") {
    val p        = PropsWithChildren().withKey("key")
    val u        = p(<.div)
    val key      = u.key
    val ref      = u.ref
    val props    = u.props
    val children = u.propsChildren
    assertEquals(key, Some("key": FacadeExports.Key))
    assertEquals(ref, None)
    assertEquals(props, PropsWithChildren())
    assertEquals(children.count, 1)
    ReactTestUtils.withNewBodyElement { mountNode =>
      p.renderIntoDOM(mountNode)
      val html = mountNode.innerHTML
      assertEquals(html, """<div></div>""")
    }
    ReactTestUtils.withNewBodyElement { mountNode =>
      u.renderIntoDOM(mountNode)
      val html = mountNode.innerHTML
      assertEquals(html, """<div><div></div></div>""")
    }
  }

  test("propsWithChildren.withRef") {
    val p        = PropsWithChildren()
    val r        = Ref.toScalaComponent(p.component)
    val pr       = p.withRef(r)
    val u        = pr(<.div)
    val key      = u.key
    val ref      = u.ref
    val props    = u.props
    val children = u.propsChildren
    assertEquals(key, None)
    assertEquals(ref.map(_.asInstanceOf[RefHandle[Any]]), Some(r.raw.asInstanceOf[RefHandle[Any]]))
    assertEquals(props, PropsWithChildren())
    assertEquals(children.count, 1)
    ReactTestUtils.withNewBodyElement { mountNode =>
      pr.renderIntoDOM(mountNode)
      val html = mountNode.innerHTML
      assertEquals(html, """<div></div>""")
    }
    ReactTestUtils.withNewBodyElement { mountNode =>
      u.renderIntoDOM(mountNode)
      val html = mountNode.innerHTML
      assertEquals(html, """<div><div></div></div>""")
    }
  }

  test("propsWithChildren.withRef.withKey") {
    val p        = PropsWithChildren()
    val r        = Ref.toScalaComponent(p.component)
    val pr       = p.withRef(r).withKey("key")
    val u        = pr(<.div)
    val key      = u.key
    val ref      = u.ref
    val props    = u.props
    val children = u.propsChildren
    assertEquals(key, Some("key": FacadeExports.Key))
    assertEquals(ref.map(_.asInstanceOf[RefHandle[Any]]), Some(r.raw.asInstanceOf[RefHandle[Any]]))
    assertEquals(props, PropsWithChildren())
    assertEquals(children.count, 1)
    ReactTestUtils.withNewBodyElement { mountNode =>
      pr.renderIntoDOM(mountNode)
      val html = mountNode.innerHTML
      assertEquals(html, """<div></div>""")
    }
    ReactTestUtils.withNewBodyElement { mountNode =>
      u.renderIntoDOM(mountNode)
      val html = mountNode.innerHTML
      assertEquals(html, """<div><div></div></div>""")
    }
  }
}
