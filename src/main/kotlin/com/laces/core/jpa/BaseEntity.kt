package com.laces.core.jpa

import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class BaseEntity : HasId {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    override var id: Long? = null

}