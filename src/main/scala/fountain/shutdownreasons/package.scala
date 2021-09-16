package fountain

import akka.actor.CoordinatedShutdown

package object shutdownreasons {

  case object EndOfStreamShutdown extends CoordinatedShutdown.Reason
  case object ErrorShutdown extends CoordinatedShutdown.Reason
}
