package zendesk.service;

import io.vertx.core.Future;
import zendesk.model.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.*;
public class UserService {

  private final Map<String, User> store = new ConcurrentHashMap<>();

  public Future<List<User>> findAll() {
    return Future.succeededFuture(
      new ArrayList<>(store.values())
    );
  }

  public Future<User> findById(String id) {
    User user = store.get(id);
    if (user == null) {
      return Future.failedFuture("User not found");
    }
    return Future.succeededFuture(user);
  }

  public Future<User> create(User user) {
    user.id = UUID.randomUUID().toString();
    store.put(user.id, user);
    return Future.succeededFuture(user);
  }

  public Future<User> update(String id, User user) {
    if (!store.containsKey(id)) {
      return Future.failedFuture("User not found");
    }
    user.id = id;
    store.put(id, user);
    return Future.succeededFuture(user);
  }

  public Future<Void> delete(String id) {
    store.remove(id);
    return Future.succeededFuture();
  }
}

