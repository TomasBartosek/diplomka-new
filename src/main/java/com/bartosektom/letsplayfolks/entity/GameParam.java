package com.bartosektom.letsplayfolks.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class GameParam {

    private Integer id;
    private Timestamp created;
    private String name;
    private String value;
    private Game gameByGameId;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "created", nullable = false)
    @CreationTimestamp
    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    @Basic
    @Column(name = "name", nullable = false, length = 45)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameParam gameParam = (GameParam) o;
        return Objects.equals(id, gameParam.id) &&
                Objects.equals(created, gameParam.created) &&
                Objects.equals(name, gameParam.name) &&
                Objects.equals(value, gameParam.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, created, name, value);
    }

    @ManyToOne
    @JoinColumn(name = "gameId", referencedColumnName = "id", nullable = false)
    public Game getGameByGameId() {
        return gameByGameId;
    }

    public void setGameByGameId(Game gameByGameId) {
        this.gameByGameId = gameByGameId;
    }
}
