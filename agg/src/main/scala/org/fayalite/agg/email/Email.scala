package org.fayalite.agg.email

/**
  * Created by aa on 3/10/2016.
  */
object Email {
  import courier._, Defaults._
  val mailer = Mailer("smtp.gmail.com", 587)
    .auth(true)
    .as("you@gmail.com", "p@$$w3rd")
    .startTtls(true)()

  mailer(Envelope.from("you" `@` "gmail.com")
    .to("mom" `@` "gmail.com")
    .cc("dad" `@` "gmail.com")
    .subject("miss you")
    .content(Text("hi mom"))).onSuccess {
    case _ => println("message delivered")
  }

  mailer(Envelope.from("you" `@` "work.com")
    .to("boss" `@` "work.com")
    .subject("tps report")
    .content(Multipart()
      .attach(new java.io.File("tps.xls"))
      .html("<html><body><h1>IT'S IMPORTANT</h1></body></html>")))
    .onSuccess {
      case _ => println("delivered report")
    }
}

