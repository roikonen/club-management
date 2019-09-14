package dao

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import javax.inject.Inject
import models.Club
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import simoroikonen.streams.ClubProtocol
import slick.jdbc.JdbcProfile
import tables.ClubMember

import scala.concurrent.{ExecutionContext, Future}

class ClubDao @Inject() (
  system: ActorSystem,
  protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  implicit val materializer = ActorMaterializer()(system)

  import profile.api._

  private val clubMembers = TableQuery[ClubMembersTable]

  /**
   * @return all [[Club]]s from the DB.
   */
  def all(): Future[Seq[Club]] = {
    def clubSource: Source[Club, _] = ClubProtocol.clubMemberToClub(Source.fromPublisher(db.stream(clubMembers.result)))
    clubSource.toMat(Sink.collection)(Keep.right).run()
  }

  /**
   * Insert a new [[Club]] to a database.
   *
   * @param club club to be inserted
   * @return number of DB-rows created
   */
  def insert(club: Club): Future[Int] = {
    def insert(clubMember: ClubMember): Future[Int] = db.run(clubMembers += clubMember)
    def clubMemberSource(club: Club): Source[ClubMember, _] = ClubProtocol.clubToClubMember(Source.single(club))
    val clubMemberSink: Sink[ClubMember, Future[Int]] = Flow[ClubMember].mapAsync(1)(insert).toMat(Sink.fold[Int, Int](0)(_ + _))(Keep.right)
    clubMemberSource(club).toMat(clubMemberSink)(Keep.right).run()
  }

  private class ClubMembersTable(tag: Tag) extends Table[ClubMember](tag, "CLUB_MEMBERS") {

    def club = column[String]("CLUB")
    def member = column[String]("MEMBER")

    def * = (club, member) <> (ClubMember.tupled, ClubMember.unapply)

  }

}
