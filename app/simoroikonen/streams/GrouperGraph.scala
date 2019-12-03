package simoroikonen.streams

import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}

import scala.collection.mutable.ListBuffer

/**
 * Groups incoming stream into lists of similar following objects in appearance order.
 * e.g. 1,1,1,2,2,3,3,1,1,1 => List(1,1,1), List(2,2), List(3,3), List(1,1,1)
 *      when "similar" function: first.equals(second)
 *
 * @param similar a function that defines what means being equal
 * @tparam A type of objects flowing trough
 */
class GrouperGraph[A](similar: (A, A) => Boolean) extends GraphStage[FlowShape[A, List[A]]] {
  val in: Inlet[A] = Inlet("Grouper.in")
  val out: Outlet[List[A]] = Outlet("Grouper.out")
  override val shape: FlowShape[A, List[A]] = FlowShape(in, out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {

      val similarItems: ListBuffer[A] = new ListBuffer[A]()

      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          val elem = grab(in)
          if (similarItems.isEmpty || similar(similarItems(0), elem)) {
            similarItems += elem
            pull(in)
          } else {
            push(out, similarItems.toList)
            similarItems.clear()
            similarItems += elem
          }
        }

        override def onUpstreamFinish(): Unit = {
          if (similarItems.nonEmpty) {
            push(out, similarItems.toList)
            similarItems.clear()
          }
          complete(out)
        }

      })
      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          pull(in)
        }
      })
    }
}
