package org.example.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "amount")
    private Long moneyAmount;

    public Account() {
    }

    public Account(User user, Long moneyAmount) {
        this.user = user;
        this.moneyAmount = moneyAmount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Long getUserId() {
        return user.getId();
    }

    public Long getMoneyAmount() {
        return moneyAmount;
    }

    public void setMoneyAmount(Long moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    @Override
    public String toString() {
        return "Аккаунт (" +
                "Id=" + id +
                ", Id владельца=" + user.getId() +
                ", Остаток =" + moneyAmount +
                ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) && user == account.user;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user);
    }
}
