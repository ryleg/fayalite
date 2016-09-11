package org.fayalite.gate.server

object PageRender {

  val defaultIndexPage = {
    import scalatags.Text.all._
    // "<!DOCTYPE html>" + // ?Necessary?
    html(
      scalatags.Text.all.head(
        scalatags.Text.tags2.title("fayalite"),
        link(rel := "icon", href := "img/fay16x16.png"),
        meta(charset := "UTF-8")
      )
      ,
      body(
        script(
          src := "fayalite-fastopt.js",
          `type` := "text/javascript"),
        script("org.fayalite.sjs.App().main()",
          `type` := "text/javascript")
      )
    ).render
  }

}
