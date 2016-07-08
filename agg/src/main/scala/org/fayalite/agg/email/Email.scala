package org.fayalite.agg.email

/**
  * TODO : Integrate with REPL
  *
  * UNFINISHED. Just for dependencies resolution so far.
  */
object Email {

  import courier._, Defaults._

  // Sends an email!
  def sendEmail(
               smptServer: String = "smtp.gmail.com",
               smptPort: Int = 587,
               user: String,
               pass: String,
               toUser: String,
               toDomain: String,
               fromUser: String,
               fromDomain: String,
               subject: String = "",
               contentText: String = ""
               ) = {
    val mailer = Mailer(smptServer, smptPort)
      .auth(true)
      .as(user, pass)
      .startTtls(true)()

    mailer(Envelope.from(fromUser `@` fromDomain)
      .to(toUser `@` toDomain)
      .subject(subject)
      .content(Text(contentText)))

  }

  def main(args: Array[String]) {
    val user = args(0)
    val pass = args(1)
    //... etc
    //mailer.

  }
/*
  mailer(Envelope.from("you" `@` "work.com")
    .to("boss" `@` "work.com")
    .subject("tps report")
    .content(Multipart()
      .attach(new java.io.File("tps.xls"))
      .html("<html><body><h1>IT'S IMPORTANT</h1></body></html>")))
    .onSuccess {
      case _ => println("delivered report")
    }*/
}

