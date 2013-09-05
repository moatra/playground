package models

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Play.current
import com.github.tototoshi.slick.JodaSupport._
import org.joda.time.DateTime
import com.lambdaworks.crypto.SCryptUtil
import scala.math.pow
import scala.util.Random
import org.apache.commons.codec.binary.Base64

case class User(id: Option[Long], firstName: String, lastName: String, email: String, password: String,
                confirm: Option[String], confirmed: Boolean, registered: DateTime, lastLogin: Option[DateTime],
                admin: Boolean) {
  def fullName = f"$firstName $lastName"
}

class UserTable extends Table[User]("users") with IdCrud[User] {
  // columns
  def firstName = column[String]("first_name")
  def lastName = column[String]("last_name")
  def email = column[String]("email")
  def password = column[String]("password")
  def confirm = column[String]("confirm", O.Nullable)
  def confirmed = column[Boolean]("confirmed")
  def registered = column[DateTime]("registered")
  def lastLogin = column[DateTime]("last_login", O.Nullable)
  def admin = column[Boolean]("admin")

  // constraints
  def email_index = index("users_email_index", email, unique = true)

  def * = id.? ~ firstName ~ lastName ~ email ~ password ~ confirm.? ~ confirmed ~
          registered ~ lastLogin.? ~ admin <> (User, User.unapply _)

  def autoInc = firstName ~ lastName ~ email ~ password ~ confirm.? ~ confirmed ~
    registered ~ lastLogin.? ~ admin returning id


  protected override def insert(user: User) = {
    DB.withSession { implicit session: Session =>
      val toInsert = sendConfirmationEmail(user)
      autoInc.insert((toInsert.firstName, toInsert.lastName, toInsert.email, toInsert.password, toInsert.confirm,
        toInsert.confirmed, toInsert.registered, toInsert.lastLogin, toInsert.admin))
    }
  }

  private def encrypt(password: String) =  {
    // http://stackoverflow.com/questions/11126315/what-are-optimal-scrypt-work-factors
    SCryptUtil.scrypt(password, pow(2, 14).toInt, 8, 1)
  }

  def sendConfirmationEmail(user: User) : User = {
    val byteArray = new Array[Byte](16)
    Random.nextBytes(byteArray)
    val confirmCode = new String(Base64.encodeBase64(byteArray))

    //todo:
    //sendEmail(confirmCode, user)

    user.copy(confirm = Some(encrypt(confirmCode)))
  }

  def checkEmailConfirmation(email: String, code: String) : Option[User] = {
    DB.withSession { implicit session: Session =>
      (Query(Users) where (_.email === email) firstOption).filter(u =>
        u.confirm != None && SCryptUtil.check(code, u.confirm.get)
      ).map(user => {
          val now = DateTime.now
          (for { u <- Users if u.email === email } yield u.lastLogin ~ u.confirm.? ~ u.confirmed).update((now, None, true))
          user.copy(lastLogin = Some(now), confirm = None, confirmed = true)
        }
      )
    }
  }

  def updateLastLogin(email: String) = {
    DB.withSession { implicit session: Session =>
      (for { u <- Users if u.email === email } yield u.lastLogin).update(DateTime.now)
    }
  }

  def isEmailAvailable(email: String) = {
    DB.withSession { implicit session: Session =>
      Query(Users where(_.email === email) length).first == 0
    }
  }

  def userByEmail(email: String) : Option[User] = {
    DB.withSession { implicit session: Session =>
      (Query(Users) where (_.email === email)).firstOption
    }
  }
}
