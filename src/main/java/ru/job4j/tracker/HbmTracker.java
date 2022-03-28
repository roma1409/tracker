package ru.job4j.tracker;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;

public class HbmTracker implements Store, AutoCloseable {
    private final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure().build();
    private final SessionFactory sf = new MetadataSources(registry)
            .buildMetadata().buildSessionFactory();

    @Override
    public Item add(Item item) {
        try(Session session = sf.openSession()) {
            session.beginTransaction();
            session.save(item);
            session.getTransaction().commit();
        }
        return item;
    }

    @Override
    public boolean replace(String id, Item item) {
        try(Session session = sf.openSession()) {
            session.beginTransaction();
            item.setId(Integer.parseInt(id));
            session.update(item);
            session.getTransaction().commit();
        }
        return true;
    }

    @Override
    public boolean delete(String id) {
        try(Session session = sf.openSession()) {
            session.beginTransaction();
            Item item = new Item(null);
            item.setId(Integer.parseInt(id));
            session.delete(item);
            session.getTransaction().commit();
        }
        return true;
    }

    @Override
    public List<Item> findAll() {
        try(Session session = sf.openSession()) {
            return session.createQuery("from Item", Item.class).list();
        }
    }

    @Override
    public List<Item> findByName(String key) {
        try(Session session = sf.openSession()) {
            return session.createQuery("from Item where name = :name", Item.class)
                    .setParameter("name", key)
                    .list();
        }
    }

    @Override
    public Item findById(String id) {
        try(Session session = sf.openSession()) {
            return session.get(Item.class, id);
        }
    }

    @Override
    public void close() {
        StandardServiceRegistryBuilder.destroy(registry);
    }
}