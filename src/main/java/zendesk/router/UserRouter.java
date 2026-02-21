package zendesk.router;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class UserRouter {

  public static void register(Router router, Vertx vertx) {

    EventBus eventBus = vertx.eventBus();

    // GET /users
    router.get("/users").handler(ctx -> {
      eventBus.request("user.findAll", "", reply -> {
        if (reply.succeeded()) {
          ctx.json(reply.result().body());
        } else {
          ctx.fail(reply.cause());
        }
      });
    });

    // POST /users
    router.post("/users").handler(ctx -> {

      JsonObject body = ctx.body().asJsonObject();

      eventBus.request("user.create", body, reply -> {
        if (reply.succeeded()) {
          ctx.json(reply.result().body());
        } else {
          ctx.fail(reply.cause());
        }
      });
    });

    //Delete /user/{id}
    router.delete("/users/:id").handler(ctx -> {

      String id = ctx.pathParam("id");

      JsonObject request = new JsonObject().put("id", id);

      ctx.vertx().eventBus().request("user.delete", request, reply -> {
        if (reply.succeeded()) {
          ctx.response().setStatusCode(204).end();
        } else {
          ctx.response()
            .setStatusCode(404)
            .end(reply.cause().getMessage());
        }
      });
    });
  }
}


