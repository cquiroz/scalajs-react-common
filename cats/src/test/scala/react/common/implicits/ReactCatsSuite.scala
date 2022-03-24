package react.common.implicits

import cats.kernel.laws.discipline._
import org.scalacheck.Arbitrary._
import react.common._
import react.common.arb._
import munit._

class StyleSuite extends DisciplineSuite {
  checkAll("Eq[Style]", EqTests[Style].eqv)
  checkAll("Eq[Css]", EqTests[Css].eqv)
  checkAll("Eq[Size]", EqTests[Size].eqv)
  checkAll("Monoid[Css]", MonoidTests[Css].monoid)
  checkAll("Monoid[Style]", MonoidTests[Style].monoid)
}
