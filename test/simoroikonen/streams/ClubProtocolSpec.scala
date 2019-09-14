package simoroikonen.streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep}
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import akka.testkit.TestKit
import models.{Club, Member}
import org.junit.runner.RunWith
import org.scalatest.FlatSpecLike
import org.scalatest.junit.JUnitRunner
import tables.ClubMember

@RunWith(classOf[JUnitRunner])
class ClubProtocolSpec extends TestKit(ActorSystem("ClubProtocolSpec")) with FlatSpecLike {

  implicit val materializer = ActorMaterializer()

  behavior of "ClubProtocol"

  it should "convert Club stream to ClubMember stream and vice versa" in {

    val flowBack = Flow[ClubMember].map(clubMember => clubMember)
    val protocolUnderTest = ClubProtocol.stack.join(flowBack)
    val (pub, sub) = TestSource.probe[Club].via(protocolUnderTest).toMat(TestSink.probe[Club])(Keep.both).run()
    val expectedClub1 = Club("Name1", List(Member("Member11"), Member("Member12")))
    val expectedClub2 = Club("Name2", List(Member("Member21"), Member("Member22")))
    val expectedClub3 = Club("Name3", List(Member("Member31"), Member("Member32")))

    sub.request(3)
    pub.sendNext(expectedClub1)
    pub.sendNext(expectedClub2)
    pub.sendNext(expectedClub3)
    sub.expectNext(expectedClub1)
    sub.expectNext(expectedClub2)
    pub.sendComplete()
    sub.expectNext(expectedClub3)

  }

}
