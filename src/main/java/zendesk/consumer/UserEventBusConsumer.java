package zendesk.consumer;


import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Vertx;
import zendesk.kafka.UserEventProducer;
import zendesk.service.UserService;

public class UserEventBusConsumer {

  private final UserService service;
  private final UserEventProducer userEventProducer;

  public UserEventBusConsumer(Vertx vertx, UserService service, UserEventProducer userEventProducer) {
    this.service = service;
    this.userEventProducer = userEventProducer;
    registerConsumers(vertx.eventBus());
  }

  private void registerConsumers(EventBus eventBus) {

    // CREATE USER
    eventBus.consumer("user.create", msg -> {
      JsonObject body = (JsonObject) msg.body();
      service.create(body.mapTo(zendesk.model.User.class))
        .onSuccess(user -> {
            msg.reply(JsonObject.mapFrom(user));
            //publishing to kafka
            userEventProducer.publish("user.create", body);
            System.out.println("user.create event consumed and sent to kafka : " + body);
          }
        )
        .onFailure(err ->
          msg.fail(500, err.getMessage())
        );
    });

    // GET ALL USERS
    eventBus.consumer("user.findAll", msg -> {
      service.findAll()
        .onSuccess(users -> {
            JsonArray array = new JsonArray();
            users.forEach(user ->
              array.add(JsonObject.mapFrom(user))
            );
            msg.reply(array);
            System.out.println("user.findAll event consumed : " + array);
          }

        )
        .onFailure(err ->
          msg.fail(500, err.getMessage())
        );
    });

    // UPDATE USER
    eventBus.consumer("user.update", msg -> {
      JsonObject body = (JsonObject) msg.body();

      String id = body.getString("id");

      if (id == null) {
        msg.fail(400, "User id is required");
        return;
      }

      zendesk.model.User user = body.mapTo(zendesk.model.User.class);

      service.update(id, user)
        .onSuccess(updatedUser -> {
            msg.reply(JsonObject.mapFrom(updatedUser));
            System.out.println("user.update event consumed : " + body);
          }
        )
        .onFailure(err ->
          msg.fail(404, err.getMessage())
        );
    });


    // DELETE USER
    eventBus.consumer("user.delete", msg -> {
      JsonObject body = (JsonObject) msg.body();

      String id = body.getString("id");

      if (id == null) {
        msg.fail(400, "User id is required");
        return;
      }

      service.delete(id)
        .onSuccess(v -> {
            msg.reply(new JsonObject().put("status", "deleted"));
            System.out.println("user.delete event consumed : " + id);
          }
        )
        .onFailure(err ->
          msg.fail(404, err.getMessage())
        );
    });


  }
}

