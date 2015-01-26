

object go{
  def to(a: String) = a + "!"
}
go to "1"

implicit class j(s: String) {
  def too(q: String) = s + q + "1"
}

"a" too "b"
