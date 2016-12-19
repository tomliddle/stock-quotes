
import com.google.inject.{AbstractModule, Provides}
import java.time.Clock

import models.entities.Stock
import models.persistence.{AbstractBaseDAO, BaseDAO, SlickTables}
import models.persistence.SlickTables.{QuotesTable, StocksTable}


/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.

 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule {

  import SlickTables.stockTableQ

  override def configure(): Unit = {
    // Use the system clock as the default implementation of Clock
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
  }

 @Provides
 def provideStocksDAO : AbstractBaseDAO[StocksTable,Stock] = new BaseDAO[StocksTable,Stock]

  @Provides
  def provideQuoteDAO : AbstractBaseDAO[QuotesTable,Stock] = new BaseDAO[QuotesTable,Stock]
}



