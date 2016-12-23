
import com.google.inject.{AbstractModule, Provides}
import java.time.Clock
import javax.inject.Named

import models.actors.{PerRequestActor, PerRequestActorFactory, QuoteActor}
import models.persistence.{QuotePersistence, StockPersistence}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.AkkaGuiceSupport

import scala.concurrent.ExecutionContext


/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.

 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule with AkkaGuiceSupport {

 // import SlickTables.stockTableQ

  override def configure(): Unit = {
    // Use the system clock as the default implementation of Clock
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)

    bind(classOf[StockPersistence]).asEagerSingleton()

    bind(classOf[QuotePersistence]).asEagerSingleton()

    bindActor[QuoteActor](QuoteActor.Name)

    bindActorFactory[PerRequestActor, PerRequestActorFactory]

  }
}



