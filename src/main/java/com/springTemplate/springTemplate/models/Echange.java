package com.springTemplate.springTemplate.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "Echange")
public class Echange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private User user1;

    @ManyToOne
    private User user2;

    @ManyToOne
    private Carte carteOfferte;

    @ManyToOne
    private Carte carteDemande;

    @Temporal(TemporalType.DATE)
    private Date dateEchange;
}
