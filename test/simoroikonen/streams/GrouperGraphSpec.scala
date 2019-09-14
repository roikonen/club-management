package simoroikonen.streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep}
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import akka.testkit.TestKit
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpecLike

@RunWith(classOf[JUnitRunner])
class GrouperGraphSpec extends TestKit(ActorSystem("GrouperGraphSpec")) with FlatSpecLike {

  implicit val materializer = ActorMaterializer()

  behavior of "GrouperGraph"

  it should "group following similar items to lists" in {

    // Arrange
    case class GroupItem(similarityId: String, order: Int)
    def similar(first: GroupItem, second: GroupItem) = first.similarityId.equals(second.similarityId)
    val flowUnderTest = Flow.fromGraph(new GrouperGraph[GroupItem](similar))
    val (pub, sub) = TestSource.probe[GroupItem].via(flowUnderTest).toMat(TestSink.probe[List[GroupItem]])(Keep.both).run()

    // Act
    sub.request(n = 4)
    pub.sendNext(GroupItem("A", 1))
    pub.sendNext(GroupItem("B", 1))
    pub.sendNext(GroupItem("B", 2))
    pub.sendNext(GroupItem("B", 3))
    pub.sendNext(GroupItem("C", 1))
    pub.sendNext(GroupItem("C", 2))
    pub.sendNext(GroupItem("A", 2))
    pub.sendNext(GroupItem("A", 3))
    pub.sendComplete()

    // Assert
    sub.expectNextUnordered(
      List(GroupItem("A", 1)),
      List(GroupItem("B", 1), GroupItem("B", 2), GroupItem("B", 3)),
      List(GroupItem("C", 1), GroupItem("C", 2)),
      List(GroupItem("A", 2), GroupItem("A", 3))
    )

  }

}
