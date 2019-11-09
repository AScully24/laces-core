package com.laces.core.jpa

import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class BaseEntity (
        @Id
        @GeneratedValue(strategy = IDENTITY)
        override var id: Long? = null
): HasId