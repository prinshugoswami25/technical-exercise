package zendesk;

import io.vertx.core.Vertx;

public class ServiceStarter {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
}
