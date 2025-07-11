package org.tony.ross.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.tony.ross.entity.Actor;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ActorRepository implements PanacheRepository<Actor> {

    public List<Actor> findByFirstName(String firstName) {
        return find("firstName", firstName).list();
    }

    public List<Actor> findByLastName(String lastName) {
        return find("lastName", lastName).list();
    }

    public List<Actor> findByFullName(String firstName, String lastName) {
        return find("firstName = ?1 and lastName = ?2", firstName, lastName).list();
    }

    public List<Actor> findTop10() {
        return find("ORDER BY actorId").range(0, 9).list();
    }
}
