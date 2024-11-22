package org.example.util;

import org.hibernate.Session;

public interface TransactionalExecution<T> {
    T execute (Session session);
}
