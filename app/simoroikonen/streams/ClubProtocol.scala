package simoroikonen.streams

import akka.stream.scaladsl.{BidiFlow, Flow, Sink, Source}
import models.{Club, Member}
import tables.ClubMember

/**
 * Protocol for managing serialization and deserialization
 * of table entities ([[ClubMember]]) to model entities ([[Club]]) and vice versa.
 */
object ClubProtocol {

  /**
   * Creates source of [[Club]]-elements from source of [[ClubMember]]-elements.
   * Uses protocol stack of [[BidiFlow]]s in the creation for the sake of an example.
   *
   * @param source of [[ClubMember]]-elements
   * @return source of [[Club]]-elements
   */
  def clubMemberToClub(source: Source[ClubMember, _]): Source[Club, _] =
    Source.empty.via(stack.join(Flow.fromSinkAndSource(Sink.ignore, source)))

  /**
   * Creates source of [[ClubMember]]-elements from source of [[Club]]-elements.
   * Uses reversed protocol stack of [[BidiFlow]]s in the creation for the sake of an example.
   *
   * @param source of [[Club]]-elements
   * @return source of [[ClubMember]]-elements
   */
  def clubToClubMember(source: Source[Club, _]): Source[ClubMember, _] =
    Source.empty.via(stack.reversed.join(Flow.fromSinkAndSource(Sink.ignore, source)))

  /**
   * Protocol stack for handling Clubs to ClubMember-format and back.
   *
   *       +-------------------------------------------+
   *       | stack                                     |
   *       |                                           |
   *       |  +-------+                  +----------+  |
   *  ~>   O~~o       |       ~>         |          o~~O    ~>
   * Club  |  | codec | List[ClubMember] | grouping |  | ClubMember
   *  <~   O~~o       |       <~         |          o~~O    <~
   *       |  +-------+                  +----------+  |
   *       +-------------------------------------------+
   */
  final val stack = codec.atop(grouping)
  private def codec = BidiFlow.fromFlows(clubToClubMemberGroup, clubMemberGroupToClub)
  private def grouping = BidiFlow.fromFlows(ungroupClubMembers, groupClubMembers)

  // Club -> List[ClubMember]
  private def clubToClubMemberGroup: Flow[Club, List[ClubMember], _] =
    Flow.fromFunction(serialize)
  private def serialize(club: Club): List[ClubMember] = club.members.map(member => ClubMember(club.name, member.name)).toList

  // List[ClubMember] -> ClubMember
  private def ungroupClubMembers: Flow[List[ClubMember], ClubMember, _] =
    Flow[List[ClubMember]].mapConcat(identity)

  // ClubMember -> List[ClubMember]
  private def groupClubMembers: Flow[ClubMember, List[ClubMember], _] =
    Flow.fromGraph(new GrouperGraph[ClubMember]((first: ClubMember, second: ClubMember) => first.club.equals(second.club)))

  // List[ClubMember] -> Club
  private def clubMemberGroupToClub: Flow[List[ClubMember], Club, _] = Flow.fromFunction(deserialize).collect { case Some(club) => club }
  private def deserialize(clubMembers: List[ClubMember]): Option[Club] =
    clubMembers.foldLeft(clubMembers.headOption.map(club => Club(club.club, List())))(clubWithMemberAdded(_, _))
  private def clubWithMemberAdded(club: Option[Club], clubMember: ClubMember): Option[Club] =
    club.map(club => club.copy(members = club.members :+ Member(clubMember.member)))

}
