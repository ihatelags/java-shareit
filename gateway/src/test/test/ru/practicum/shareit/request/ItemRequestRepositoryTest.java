package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void findItemRequestsOfUserTest() {
        User user = new User(null, "user 1", "user1@email");
        ItemRequest itemRequest = new ItemRequest(null, "газонокосилка", user, LocalDateTime.now());
        em.persist(user);
        em.persist(itemRequest);

        List<ItemRequest> itemsList = itemRequestRepository.findItemRequestsOfUser(user.getId(), PageRequest.of(0, 10));

        then(itemsList).size().isEqualTo(1);
        then(itemsList).containsExactlyElementsOf(List.of(itemRequest));
    }

    @Test
    void findItemRequestsOfAllUsersTest() {
        User user = new User(null, "user 1", "user1@email");
        User userNew = new User(null, "user 2", "user2@email");
        ItemRequest itemRequest = new ItemRequest(null, "газонокосилка", user, LocalDateTime.now());
        em.persist(user);
        em.persist(userNew);
        em.persist(itemRequest);

        List<ItemRequest> itemsList = itemRequestRepository.findItemRequestsOfAllUsers(userNew.getId(), PageRequest.of(0, 10));

        then(itemsList).size().isEqualTo(1);
        then(itemsList).containsExactlyElementsOf(List.of(itemRequest));
    }


}
