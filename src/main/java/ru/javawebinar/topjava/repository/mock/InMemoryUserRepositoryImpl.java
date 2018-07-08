package ru.javawebinar.topjava.repository.mock;

import com.sun.org.apache.bcel.internal.generic.LRETURN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Repository
public class InMemoryUserRepositoryImpl implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepositoryImpl.class);
    private Map<Integer, User> userRepo = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        userRepo.put(1, new User(1, "Sergiej", "korniz33@gmail.com", "12345678", Role.ROLE_ADMIN, Role.ROLE_USER));
        userRepo.put(2, new User(2, "Volodymyr", "kuzivdr12@gmail.com", "87654321", Role.ROLE_USER, Role.ROLE_USER));

    }

    @Override
    public User save(User user) {
        log.info("save {}", user);
        if (user.isNew()) {
            user.setId(counter.incrementAndGet());
            userRepo.put(user.getId(), user);
            return user;
        }
        return userRepo.computeIfPresent(user.getId(), (id, oldUser) -> user);
    }

    @Override
    public boolean delete(int id) {
        log.info("delete {}", id);
        return userRepo.values().removeIf(v -> v.getId().intValue() == id);
    }

    @Override
    public User get(int id) {
        log.info("get {}", id);
        return userRepo.values().stream()
                .filter(v -> (v.getId().intValue() == id))
                .findAny().orElse(null);
    }

    @Override
    public User getByEmail(String email) {
        log.info("getByEmail {}", email);
        return userRepo.values().stream().filter(v -> v.getEmail().equalsIgnoreCase(email)).findAny().orElse(null);
    }

    @Override
    public List<User> getAll() {
        log.info("getAll");
        if (userRepo.values() == null || userRepo == null) return Collections.emptyList();
        return new ArrayList<>(userRepo.values()).stream().
                sorted(((o1, o2) -> (o1.getName().compareToIgnoreCase(o2.getName())))).
                collect(Collectors.toList());
    }
}
