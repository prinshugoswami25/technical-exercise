package zendesk;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import zendesk.consumer.UserEventBusConsumer;
import zendesk.kafka.UserEventProducer;
import zendesk.router.UserRouter;
import zendesk.service.UserService;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    UserService userService = new UserService();
    UserEventProducer userEventProducer = new UserEventProducer(vertx);
    UserEventBusConsumer userEventBusConsumer = new UserEventBusConsumer(vertx, userService, userEventProducer);
    UserRouter.register(router, vertx);

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8080);

    System.out.println("Server started on port 8080");
  }
}
