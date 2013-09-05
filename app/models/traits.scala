package models

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import scala.slick.lifted.ColumnBase
import play.api.Play.current

trait IdCrud[T <: AnyRef { val id: Option[Long] }] { self: Table[T] =>
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def * : ColumnBase[T]

  def get(id: Long) : Option[T] = {
    DB.withSession { implicit session: Session =>
      Query(this).where(_.id === id).firstOption
    }
  }

  def exists(id: Long) : Boolean = {
    DB.withSession { implicit session: Session =>
      Query(tableToQuery(this).where(_.id === id).length).first > 0
    }
  }

  def count() : Int = {
    DB.withSession { implicit session : Session =>
      Query(tableToQuery(this).length).first
    }
  }

  /**
   * Either inserts or updates the entity, returning the entity's primary key
   *
   * @return Primary key of the entity
   */
  def save(entity: T) : Long = {
    entity.id match {
      case Some(_) => update(entity)
      case None => insert(entity)
    }
  }

  /**
   * Should insert the entity without passing a null id, and return the autoInc id
   * @param entity
   * @return
   */
  protected def insert(entity: T) : Long
  protected def update(entity: T) = {
    DB.withSession { implicit session: Session =>
      tableQueryToUpdateInvoker(
        tableToQuery(this).where(_.id === entity.id)
      ).update(entity)
    }
    entity.id.get
  }

  def delete(entity: T) : Unit = {
    entity.id.map(delete)
  }

  def delete(id: Long) : Unit = {
    DB.withSession { implicit session: Session =>
      queryToDeleteInvoker(
        tableToQuery(this).where(_.id === id)
      ).delete
    }
  }
}